package ru.icl.dicewars.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;
import ru.icl.dicewars.client.Lead;
import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.client.World;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.SimpleDiceCountInReserveChangedActivity;
import ru.icl.dicewars.core.activity.SimpleFlagDistributedActivity;
import ru.icl.dicewars.core.activity.SimpleGameEndedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleLandUpdatedActivity;
import ru.icl.dicewars.core.activity.SimpleMaxConnectedLandsCountChangedActivityImpl;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivity;
import ru.icl.dicewars.core.activity.SimpleTotalDiceCountChangedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleWorldCreatedActivityImpl;
import ru.icl.dicewars.core.exception.InvalidPlayerClassInstatiationException;
import ru.icl.dicewars.core.exception.InvalidPlayersCountException;
import ru.icl.dicewars.core.roll.LandRoll;
import ru.icl.dicewars.core.roll.LandRollResult;
import ru.icl.dicewars.core.util.RandomUtil;
import ru.icl.dicewars.util.ClassUtil;

public class GamePlayThread extends Thread{
	
	private static final int MAX_ACTIVITY_COUNT_IN_QUEUE = 50;

	private Configuration configuration;
	
	private ActivityQueue activityQueue = new ActivityQueueImpl();
	
	private Map<Flag, Integer> totalDiceCountMap = new HashMap<Flag, Integer>();
	
	//private Player winnerPlayer;
	
	private boolean started = false;
	
	private Object startedFlag = new Object();
	
	private boolean t = true;
	
	public void kill(){
		t = false;
		synchronized (this) {
			this.notifyAll();	
		}
	}
	
	/*public Player getWinnerPlayer() {
		return winnerPlayer;
	}*/
	
	public DiceWarsActivity pollFromActivityQueue() {
		DiceWarsActivity activity = activityQueue.poll();
		synchronized (this) {
			this.notify();	
		}
		return activity;
	}
	
	public GamePlayThread(Configuration configuration) {
		this.configuration = configuration;
	}
	
	private boolean hasWinner(World world) {
		return world.getFlags().size() == 1;
	}

	private Player[] getPlayers(){
		Class<Player>[] playerClasses = configuration.getPlayerClasses(); 
		final int playerCount = playerClasses.length;
		
		if (playerCount < 2 || playerCount > Flag.values().length)
			throw new InvalidPlayersCountException();
		
		Player[] players = new Player[playerCount];
		for (int i = 0; i < playerCount; i++) {
			Constructor<Player> constructor = ClassUtil
					.getConstructorIfAvailable(playerClasses[i],
							new Class<?>[] {});
			try {
				Player player = constructor.newInstance();
				players[i] = player;
			} catch (InvocationTargetException e) {
				throw new InvalidPlayerClassInstatiationException();
			} catch (IllegalAccessException e) {
				throw new InvalidPlayerClassInstatiationException();
			} catch (InstantiationException e) {
				throw new InvalidPlayerClassInstatiationException();
			}
		}
		return players;
	}
	
	private void initPlayers(Player[] players){
		final int playerCount = players.length;
		for (int i = 0; i < playerCount; i++) {
			try {
				players[i].init();
			} catch (Exception e) {
			}
		}
	}
	
	private FullLand getRandomLandForDiceIncreasingByFlag(final FullWorld world, final Flag flag){
		if (world == null || flag == null) throw new IllegalArgumentException();
		Set<FullLand> lands = new HashSet<FullLand>();
		
		for (FullLand land : world.getFullLands()){
			if (flag.equals(land.getFlag()) && land.getDiceCount() < 8){
				lands.add(land);
			}
		}
		
		if (lands.isEmpty()) return null;
		
		FullLand[] arrayLands = lands.toArray(new FullLand[]{}); 
		
		int rnd = RandomUtil.getRandomInt(lands.size());
		
		return arrayLands[rnd];
	}
	
	private void addToActivityQueue(DiceWarsActivity activity){
		while (activityQueue.size() > MAX_ACTIVITY_COUNT_IN_QUEUE && t){
			try{
				synchronized (this) {
					this.wait();
				}
			}catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		activityQueue.add(activity);
	}
	
	private void grantWorldByFlag(final FullWorld world, final Flag playerFlag) {
		int j = world.getMaxConnectedLandsByFlag(playerFlag);
		
		//*TODO speed up this block
		int totalAddedDiceCount = 0;
		while (j > 0){
			if (configuration.getMaxDiceCountInReserve() > world.getDiceCountInReserve(playerFlag)){
				world.incDiceCountInReserve(playerFlag);
				totalAddedDiceCount++;
			}
			j--;
		}
		
		addTotalDiceCountByFlag(playerFlag, totalAddedDiceCount);
		
		Land land = getRandomLandForDiceIncreasingByFlag(world, playerFlag);
		while (land != null && world.hasInReserve(playerFlag)){
			if (land.getDiceCount() > 7) throw new IllegalStateException();
			if (!land.getFlag().equals(playerFlag)){
				throw new IllegalStateException();
			}
			if (land instanceof FullLand){
				FullLand fullLand = (FullLand) land;
				fullLand.incDiceCount();
				addToActivityQueue(new SimpleLandUpdatedActivity(fullLand));
				addTotalDiceCountByFlag(playerFlag, 1);
			}else{
				throw new IllegalStateException();
			}
			world.decDiceCountInReserve(playerFlag);
			land = getRandomLandForDiceIncreasingByFlag(world, playerFlag);
		}
		
		addToActivityQueue(new SimpleTotalDiceCountChangedActivityImpl(playerFlag, totalDiceCountMap.get(playerFlag)));
		addToActivityQueue(new SimpleDiceCountInReserveChangedActivity(playerFlag, world.getDiceCountInReserve(playerFlag)));		
	}
	
	/*private Flag getWinner(World world){
		for (Land land : world.getLands()){
			return land.getFlag();
		}
		throw new IllegalStateException();
	}*/
	
	private Map<Flag, String> getFlagToNameMap(Map<Player, Flag> playerFlagMap) {
		Map<Flag, String> flagToNameMap = new HashMap<Flag, String>();
		for (Player player : playerFlagMap.keySet()){
			flagToNameMap.put(playerFlagMap.get(player), player.getName());
		}
		return flagToNameMap;
	}

	private Integer getTotalDiceCountByFlag(Flag flag){
		Integer totalDiceCount = totalDiceCountMap.get(flag);
		if (totalDiceCount == null) totalDiceCount = 0;
		return totalDiceCount;
	}
	
	private void addTotalDiceCountByFlag(Flag flag, int diceCount){
		Integer totalDiceCount = totalDiceCountMap.get(flag);
		if (totalDiceCount == null) totalDiceCount = 0;
		totalDiceCount += diceCount;
		totalDiceCountMap.put(flag, totalDiceCount);
	}
	
	@Override
	public void run() {
		synchronized (startedFlag) {
			if (started) throw new IllegalStateException("This thread can't be run twice.");
			started = true;
		}
		//winnerPlayer = null;
		activityQueue.clear();
		
		Player[] players = getPlayers();
		initPlayers(players);
		final int playerCount = players.length;
		FullWorldGenerator fullWorldGenerator = configuration.geFullWorldGenerator();
		final FullWorld world = fullWorldGenerator.generate();

		for (FullLand land : world.getFullLands()){
			Flag flag = land.getFlag();
			addTotalDiceCountByFlag(flag, land.getDiceCount());
		}
		
		/* Players map to flags */
		Map<Player, Flag> playerFlagMap = new HashMap<Player,Flag>();
		Map<Flag, Player> flagPlayerMap = new HashMap<Flag,Player>();
		
		int v = 0;
		for (Flag flag : world.getFlags()){
			//System.out.println(flag.toString() + ": " + players[v].getName());
			playerFlagMap.put(players[v], flag);
			flagPlayerMap.put(flag, players[v]);
			v++;
		}
		
		addToActivityQueue(new SimpleFlagDistributedActivity(getFlagToNameMap(playerFlagMap)));
		
		addToActivityQueue(new SimpleWorldCreatedActivityImpl(new FullWorldImpl(world)));
		
		for (Flag flag : world.getFlags()){
			addToActivityQueue(new SimpleTotalDiceCountChangedActivityImpl(flag, getTotalDiceCountByFlag(flag)));
		}
		
		for (Flag flag : world.getFlags()){
			int maxConectedLandsCount = world.getMaxConnectedLandsByFlag(flag);
			addToActivityQueue(new SimpleMaxConnectedLandsCountChangedActivityImpl(flag, maxConectedLandsCount));
		}
		
		/* Start the game */
		int leadNumber = 1;
		while (!hasWinner(world) && t) {
			for (int i = 0; i < playerCount; i++) {
				Flag playerFlag = playerFlagMap.get(players[i]);
				world.setMyFlag(playerFlag);
			
				if (!world.getFlags().contains(playerFlag)) continue;
				boolean canLead = true;
				int stepNumber = 1;
				while (canLead && t){
					world.setAvailableLeadCount(leadNumber-stepNumber+1);
					final World immutableWorld = new ImmutableWorldImpl(world);
					try{
						SortedSet<Land> lands  = new TreeSet<Land>(new Comparator<Land>(){
							@Override
							public int compare(Land o1, Land o2) {
								return o1.getLandId() - o2.getLandId(); 
							}
						}
						);
						lands.addAll(world.getLands());

						//*TODO should be run in another thread 
						Lead lead = players[i].attack(immutableWorld);
						
						if (lead != null)
							addToActivityQueue(new SimplePlayerAttackActivity(playerFlag, lead));
						if (lead == null) {
							canLead = false; 
						}else{
							int fromLandId = lead.getFromLandId();
							int toLandId = lead.getToLandId();
							boolean successAttacked = false;
							for (FullLand land : world.getFullLands()){
								if (!successAttacked && land.getLandId() == fromLandId && land.getFlag().equals(playerFlag) && land.getDiceCount() > 1){
									for (FullLand neighbouringLand : land.getNeighbouringFullLands()){
										if (!successAttacked && !neighbouringLand.getFlag().equals(playerFlag) && neighbouringLand.getLandId() == toLandId){
											Flag neighbouringLandFlag = neighbouringLand.getFlag();
											LandRollResult landRollResult = LandRoll.roll(land, neighbouringLand);
											if (landRollResult.isLeftWin()){
												addTotalDiceCountByFlag(playerFlag, (neighbouringLand.getDiceCount())*(-1));
												neighbouringLand.setDiceCount(DiceStack.valueOf(land.getDiceCount() - 1));
												
												neighbouringLand.setFlag(playerFlag);
												
												addToActivityQueue(new SimpleLandUpdatedActivity(neighbouringLand));
												
												if (!world.isExistsLandByFlag(neighbouringLandFlag)){
													world.getFlags().remove(neighbouringLandFlag);
												}
												
												land.setDiceCount(DiceStack.ONE);
												
												addToActivityQueue(new SimpleLandUpdatedActivity(land));
												addToActivityQueue(new SimpleTotalDiceCountChangedActivityImpl(neighbouringLandFlag, getTotalDiceCountByFlag(neighbouringLandFlag)));
											}else{
												
												addTotalDiceCountByFlag(playerFlag, (land.getDiceCount() - 1)*(-1));
												
												land.setDiceCount(DiceStack.ONE);

												addToActivityQueue(new SimpleTotalDiceCountChangedActivityImpl(playerFlag, getTotalDiceCountByFlag(playerFlag)));
												addToActivityQueue(new SimpleLandUpdatedActivity(land));
											}
											int m = world.getMaxConnectedLandsByFlag(neighbouringLandFlag);
											addToActivityQueue(new SimpleMaxConnectedLandsCountChangedActivityImpl(neighbouringLandFlag, m));
											m = world.getMaxConnectedLandsByFlag(playerFlag);
											addToActivityQueue(new SimpleMaxConnectedLandsCountChangedActivityImpl(playerFlag, m));
											successAttacked = true;
										}
									}
								}
							}
							if (!successAttacked){ 	
								throw new Exception();
							}else{
								for (int j = 0; j < playerCount; j++) {
									Flag f = playerFlagMap.get(players[j]);
									if (!f.equals(playerFlag) && world.isExistsLandByFlag(f)){
										try{
											players[j].apponentAttack(f, lead, immutableWorld);
										}catch (Exception e) {
										}
									}
								}
							}
						}
					}catch (Exception e) {
						canLead = false;
						e.printStackTrace();
					}
					stepNumber++;
					canLead = canLead && (leadNumber >= stepNumber);
				}
				
				grantWorldByFlag(world, playerFlag);
				
			}
			leadNumber++;
		}
		
		//Add activity that the game is ended.
		if (t) 
			addToActivityQueue(new SimpleGameEndedActivityImpl());
		
		//winnerPlayer = flagPlayerMap.get(getWinner(world));
	}
}