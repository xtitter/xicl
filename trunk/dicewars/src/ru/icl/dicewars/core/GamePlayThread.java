package ru.icl.dicewars.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import ru.icl.dicewars.client.Attack;
import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;
import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.client.World;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.SimpleDiceCountInReserveChangedActivity;
import ru.icl.dicewars.core.activity.SimpleFlagDistributedActivity;
import ru.icl.dicewars.core.activity.SimpleGameEndedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleLandUpdatedActivity;
import ru.icl.dicewars.core.activity.SimpleMaxConnectedLandsCountChangedActivityImpl;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivity;
import ru.icl.dicewars.core.activity.SimplePlayersLoadedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleTotalDiceCountChangedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleWorldCreatedActivityImpl;
import ru.icl.dicewars.core.exception.InvalidPlayerClassInstatiationException;
import ru.icl.dicewars.core.exception.InvalidPlayersCountException;
import ru.icl.dicewars.core.roll.LandRoll;
import ru.icl.dicewars.core.roll.LandRollResult;
import ru.icl.dicewars.core.util.RandomUtil;
import ru.icl.dicewars.util.ClassUtil;

import com.javamex.classmexer.MemoryUtil;
import com.javamex.classmexer.MemoryUtil.VisibilityFilter;

public class GamePlayThread extends Thread {

	private static final Logger logger = Logger.getLogger(GamePlayThread.class.toString());
	
	private static final int MAX_ACTIVITY_COUNT_IN_QUEUE = 500;
	
	private static final long MAX_MEMORY_TO_PLAYER = 1024*1024*16; //16mb
	
	private static final long MAX_TIME_INIT_PLAYER = 1000; //1 sec.
	
	private static final long MAX_TIME_OPPONENT_ATTACK_PLAYER = 100; //0.1 sec.
	
	private static final long MAX_TIME_ATTACK_PLAYER = 200; //1 sec.
	
	private static final long MAX_TIME_CHOOSE_FLAG_PLAYER = 1000; //1 sec.
	
	private Configuration configuration;

	private ActivityQueue activityQueue = new ActivityQueueImpl();

	private Map<Flag, Integer> totalDiceCountMap = new HashMap<Flag, Integer>();

	private boolean started = false;

	private Object startedFlag = new Object();

	private volatile boolean t = true;

	private boolean useActivityQueue = true;
	
	public boolean isUseActivityQueue() {
		return useActivityQueue;
	}

	public void setUseActivityQueue(boolean useActivityQueue) {
		this.useActivityQueue = useActivityQueue;
	}

	public void kill() {
		t = false;
		synchronized (this) {
			this.notifyAll();
		}
	}

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

	private Player[] getPlayers() {
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

	private void initPlayers(Player[] players) {
		final int playerCount = players.length;
		for (int i = 0; i < playerCount; i++) {
			players[i] = fireInit(players[i]);
		}
	}

	private FullLand getRandomLandForDiceIncreasingByFlag(
			final FullWorld world, final Flag flag) {
		if (world == null || flag == null)
			throw new IllegalArgumentException();
		Set<FullLand> lands = new HashSet<FullLand>();

		for (FullLand land : world.getFullLands()) {
			if (flag.equals(land.getFlag()) && land.getDiceCount() < 8) {
				lands.add(land);
			}
		}

		if (lands.isEmpty())
			return null;

		FullLand[] arrayLands = lands.toArray(new FullLand[] {});

		int rnd = RandomUtil.getRandomInt(lands.size());

		return arrayLands[rnd];
	}

	private void addToActivityQueue(DiceWarsActivity activity) {
		if (!useActivityQueue)
			return;
		while (activityQueue.size() > MAX_ACTIVITY_COUNT_IN_QUEUE && t) {
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				kill();
			}
		}
		activityQueue.add(activity);
	}

	protected void addOneDiceToLand(FullLand fullLand) {
		fullLand.incDiceCount();
		addToActivityQueue(new SimpleLandUpdatedActivity(fullLand));
	}

	private void grantWorldByFlag(final FullWorld world, final Flag playerFlag) {
		int j = world.getMaxConnectedLandsByFlag(playerFlag);

		// *TODO speed up this block
		int totalAddedDiceCount = 0;
		while (j > 0) {
			if (configuration.getMaxDiceCountInReserve() > world
					.getDiceCountInReserve(playerFlag)) {
				world.incDiceCountInReserve(playerFlag);
				totalAddedDiceCount++;
			}
			j--;
		}

		addTotalDiceCountByFlag(playerFlag, totalAddedDiceCount);

		Land land = getRandomLandForDiceIncreasingByFlag(world, playerFlag);
		while (land != null && world.hasInReserve(playerFlag)) {
			if (land.getDiceCount() > 7)
				throw new IllegalStateException();
			if (!land.getFlag().equals(playerFlag)) {
				throw new IllegalStateException();
			}
			if (land instanceof FullLand) {
				FullLand fullLand = (FullLand) land;
				addOneDiceToLand(fullLand);
			} else {
				throw new IllegalStateException();
			}
			world.decDiceCountInReserve(playerFlag);
			land = getRandomLandForDiceIncreasingByFlag(world, playerFlag);
		}

		addToActivityQueue(new SimpleTotalDiceCountChangedActivityImpl(
				playerFlag, getTotalDiceCountByFlag(playerFlag)));
		addToActivityQueue(new SimpleDiceCountInReserveChangedActivity(
				playerFlag, world.getDiceCountInReserve(playerFlag)));
	}

	private Integer getTotalDiceCountByFlag(Flag flag) {
		Integer totalDiceCount = totalDiceCountMap.get(flag);
		if (totalDiceCount == null)
			totalDiceCount = 0;
		return totalDiceCount;
	}

	private void addTotalDiceCountByFlag(Flag flag, int diceCount) {
		Integer totalDiceCount = totalDiceCountMap.get(flag);
		if (totalDiceCount == null)
			totalDiceCount = 0;
		totalDiceCount += diceCount;
		totalDiceCountMap.put(flag, totalDiceCount);
	}

	private long calcPlayerMemoryUsage(Player player){
		return MemoryUtil.deepMemoryUsageOf(player, VisibilityFilter.ALL);
	}
	
	private Object copyObject(Object object){
		ByteArrayOutputStream buffer = null;
		ObjectOutputStream objectOutputStream = null;
		ObjectInputStream objectInputStream = null;
		ByteArrayInputStream byteArrayInputStream = null;
		try{
			buffer = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(buffer);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			buffer.close();
			
			byteArrayInputStream = new ByteArrayInputStream(buffer.toByteArray());
			objectInputStream = new CustomObjectInputStream(byteArrayInputStream, configuration.getClassLoader());

			return objectInputStream.readObject();
		}catch (IOException e) {
			throw new IllegalStateException(e);
		}catch (ClassNotFoundException e1){
			throw new IllegalStateException(e1);
		}finally{
			if (buffer != null){ 
				try{
					buffer.close();
				}catch (IOException e) {
				}
			}

			if (objectOutputStream != null){ 
				try{
					objectOutputStream.close();
				}catch (IOException e) {
				}
			}
			
			if (byteArrayInputStream != null){ 
				try{
					byteArrayInputStream.close();
				}catch (IOException e) {
				}
			}

			if (objectInputStream != null){ 
				try{
					objectInputStream.close();
				}catch (IOException e) {
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private Player fireInit(final Player player){
		class InitThread extends Thread{
			volatile Long startTime = null; 
			
			@Override
			public void run() {
				try{
					startTime = System.currentTimeMillis();
					player.init();
					startTime = null;
				}catch (Exception e) {
				}
			}
			
			public Long getStartTime() {
				return startTime;
			}
		}
		InitThread initThread = new InitThread();
		Player previousStateOfPlayer = (Player) copyObject(player);
		initThread.start();
		boolean isMemoryLimitExceeded = false;
		boolean isTimeLimitExceeded = false;
		try{
			while (initThread.isAlive()){
				long t1 = System.currentTimeMillis();
				Long startTime = initThread.getStartTime();
				if (!isTimeLimitExceeded && startTime != null && t1 - startTime.longValue() > MAX_TIME_INIT_PLAYER + 10){
					isTimeLimitExceeded = true;
					logger.info(player.getClass() + " - time limit has exceeded on execution init method. Method execution was continued.");
				}

				if (!isMemoryLimitExceeded && calcPlayerMemoryUsage(player) > MAX_MEMORY_TO_PLAYER){
					logger.info(player.getClass() + " - memory limit has exceeded on execution of init method. Method execution was aborted.");
					isMemoryLimitExceeded = true;
					initThread.stop();
					return previousStateOfPlayer;
				}
				initThread.join(10);
			}
		}catch (InterruptedException e) {
			return previousStateOfPlayer;
		}
		return player;
	}
	
	@SuppressWarnings("deprecation")
	private FireChooseFlagResult fireChooseFlag(final Player player, final World world, final Set<Flag> availableFlags){
		final FireChooseFlagResult chooseFlagResult = new FireChooseFlagResult();
		class ChooseFlagThread extends Thread{
			volatile Long startTime = null; 
			
			@Override
			public void run() {
				try{
					startTime = System.currentTimeMillis();
					Flag flag = player.chooseFlag(world, availableFlags);
					startTime = null;
					chooseFlagResult.flag = flag;
				}catch (Exception e) {
					startTime = null;
					chooseFlagResult.flag = null;
				}
			}
			
			public Long getStartTime() {
				return startTime;
			}
		}
		ChooseFlagThread chooseFlagThread = new ChooseFlagThread();
		Player previousStateOfPlayer = (Player) copyObject(player);
		chooseFlagThread.start();
		boolean isMemoryLimitExceeded = false;
		boolean isTimeLimitExceeded = false;
		try{
			while (chooseFlagThread.isAlive()){
				long t1 = System.currentTimeMillis();
				Long startTime = chooseFlagThread.getStartTime();
				if (!isTimeLimitExceeded && startTime != null && t1 - startTime.longValue() > MAX_TIME_CHOOSE_FLAG_PLAYER + 10){
					isTimeLimitExceeded = true;
					logger.info(player.getClass() + " - time limit has exceeded on execution chooseFlag method. Method execution was continued.");
				}

				if (!isMemoryLimitExceeded && calcPlayerMemoryUsage(player) > MAX_MEMORY_TO_PLAYER){
					logger.info(player.getClass() + " - memory limit has exceeded on execution of chooseFlag method. Method execution was aborted.");
					isMemoryLimitExceeded = true;
					chooseFlagThread.stop();
					chooseFlagResult.player = previousStateOfPlayer;
					return chooseFlagResult;
				}
				chooseFlagThread.join(10);
			}
		}catch (InterruptedException e) {
			chooseFlagResult.player = previousStateOfPlayer;
			return chooseFlagResult;
		}

		chooseFlagResult.player = player;
		return chooseFlagResult;
	}
	
	@SuppressWarnings("deprecation")
	private Player fireOpponentAttack(final Player player, final Flag opponentFlag, final Attack attack, final World world){
		class OpponentAttackThread extends Thread{
			volatile Long startTime = null; 
			
			@Override
			public void run() {
				try{
					startTime = System.currentTimeMillis();
					player.opponentAttack(opponentFlag, attack, world);
					startTime = null;
				}catch (Exception e) {
				}
			}
			
			public Long getStartTime() {
				return startTime;
			}
		}
		OpponentAttackThread opponentAttackThread = new OpponentAttackThread();
		Player previousStateOfPlayer = (Player) copyObject(player);
		opponentAttackThread.setPriority(MAX_PRIORITY);
		opponentAttackThread.start();
		boolean isMemoryLimitExceeded = false;
		boolean isTimeLimitExceeded = false;
		try{
			while (opponentAttackThread.isAlive()){
				long t1 = System.currentTimeMillis();
				Long startTime = opponentAttackThread.getStartTime();
				if (!isTimeLimitExceeded && startTime != null && t1 - startTime.longValue() > MAX_TIME_OPPONENT_ATTACK_PLAYER + 10){
					isTimeLimitExceeded = true;
					logger.info(player.getClass() + " - time limit has exceeded on execution opponentAttack method. Method execution was continued.");
				}
				if (!isMemoryLimitExceeded && calcPlayerMemoryUsage(player) > MAX_MEMORY_TO_PLAYER){
					logger.info(player.getClass() + " - memory limit has exceeded on execution of opponentAttack method. Method execution was aborted.");
					isMemoryLimitExceeded = true;
					opponentAttackThread.stop();
					return previousStateOfPlayer;
				}
				opponentAttackThread.join(10);
			}
		}catch (InterruptedException e) {
			return previousStateOfPlayer;
		}
		return player;
	}
	
	@SuppressWarnings("deprecation")
	private FireAttackResult fireAttack(final Player player, final World world){
		final FireAttackResult attackResult = new FireAttackResult();
		class AttackThread extends Thread{
			volatile Long startTime = null; 
			
			@Override
			public void run() {
				try{
					startTime = System.currentTimeMillis();
					Attack attack = player.attack(world);
					startTime = null;
					attackResult.attack = attack;
				}catch (Exception e) {
					startTime = null;
					attackResult.attack = null;
				}
			}
			
			public Long getStartTime() {
				return startTime;
			}
		}
		AttackThread chooseFlagThread = new AttackThread();
		Player previousStateOfPlayer = (Player) copyObject(player);
		chooseFlagThread.start();
		boolean isMemoryLimitExceeded = false;
		boolean isTimeLimitExceeded = false;
		try{
			while (chooseFlagThread.isAlive()){
				long t1 = System.currentTimeMillis();
				Long startTime = chooseFlagThread.getStartTime();
				if (!isTimeLimitExceeded && startTime != null && t1 - startTime.longValue() > MAX_TIME_ATTACK_PLAYER + 10){
					isTimeLimitExceeded = true;
					logger.info(player.getClass() + " - time limit has exceeded on execution attack method. Method execution was continued.");
				}

				if (!isMemoryLimitExceeded && calcPlayerMemoryUsage(player) > MAX_MEMORY_TO_PLAYER){
					logger.info(player.getClass() + " - memory limit has exceeded on execution of attack method. Method execution was aborted.");
					isMemoryLimitExceeded = true;
					chooseFlagThread.stop();
					attackResult.player = previousStateOfPlayer;
					return attackResult;
				}
				chooseFlagThread.join(10);
			}
		}catch (InterruptedException e) {
			attackResult.player = previousStateOfPlayer;
			return attackResult;
		}
		attackResult.player = player;
		return attackResult;
	}
	
	@Override
	public void run() {
		synchronized (startedFlag) {
			if (started)
				throw new IllegalStateException(
						"This thread can't be run twice.");
			started = true;
		}

		activityQueue.clear();

		Player[] players = getPlayers();
		final int playerCount = players.length;

		List<String> playerNames = new ArrayList<String>();
		for (int i = 0; i < playerCount; i++) {
			playerNames.add(players[i].getName());
		}
		addToActivityQueue(new SimplePlayersLoadedActivityImpl(playerNames));

		final FullWorld world = configuration.getFullWorld();
		addToActivityQueue(new SimpleWorldCreatedActivityImpl(
				new FullWorldImpl(world)));

		initPlayers(players);
		
		/* Players map to flags */
		Map<Player, Flag> playerFlagMap = new HashMap<Player, Flag>();
		Map<Flag, Player> flagPlayerMap = new HashMap<Flag, Player>();

		final World w = new ImmutableWorldImpl(world);

		Set<Flag> availableFlags = new HashSet<Flag>(world.getFlags());

		for (int i = 0; i < playerCount; i++) {
			Set<Flag> immutableAvailableFlags = Collections
					.unmodifiableSet(new HashSet<Flag>(availableFlags));
			try {
				FireChooseFlagResult result = fireChooseFlag(players[i], w, immutableAvailableFlags);
				Flag flag = result.flag;
				players[i] = result.player;
				if (!playerFlagMap.values().contains(flag) && flag != null) {
					playerFlagMap.put(players[i], flag);
					addToActivityQueue(new SimpleFlagDistributedActivity(i,
							flag));
					flagPlayerMap.put(flag, players[i]);
					availableFlags.remove(flag);
				}
			} catch (Exception e) {
			}
		}

		for (int i = 0; i < playerCount; i++) {
			if (!flagPlayerMap.values().contains(players[i])) {
				Flag flag = availableFlags.iterator().next();
				playerFlagMap.put(players[i], flag);
				addToActivityQueue(new SimpleFlagDistributedActivity(i, flag));
				flagPlayerMap.put(flag, players[i]);
				availableFlags.remove(flag);
			}
		}

		for (FullLand land : world.getFullLands()) {
			Flag flag = land.getFlag();
			addTotalDiceCountByFlag(flag, land.getDiceCount());
		}

		for (Flag flag : world.getFlags()) {
			addToActivityQueue(new SimpleTotalDiceCountChangedActivityImpl(
					flag, getTotalDiceCountByFlag(flag)));
		}

		for (Flag flag : world.getFlags()) {
			int maxConectedLandsCount = world.getMaxConnectedLandsByFlag(flag);
			addToActivityQueue(new SimpleMaxConnectedLandsCountChangedActivityImpl(
					flag, maxConectedLandsCount));
		}

		/* Start the game */
		int turnNumber = 1;
		while (!hasWinner(world) && t) {
			for (int i = 0; i < playerCount; i++) {
				Flag playerFlag = playerFlagMap.get(players[i]);
				world.setMyFlag(playerFlag);

				if (!world.getFlags().contains(playerFlag))
					continue;
				boolean canAttack = true;
				int stepNumber = 1;
				while (canAttack && t) {
					world.setAvailableAttackCount(turnNumber - stepNumber + 1);
					final World immutableWorld = new ImmutableWorldImpl(world);
					try {
						SortedSet<Land> lands = new TreeSet<Land>(
								new Comparator<Land>() {
									@Override
									public int compare(Land o1, Land o2) {
										return o1.getLandId() - o2.getLandId();
									}
								});
						lands.addAll(world.getLands());

						// *TODO should be run in another thread
						FireAttackResult fireAttackResult = fireAttack(players[i], immutableWorld);
						Attack attack = fireAttackResult.attack;
						players[i] = fireAttackResult.player;
						
						if (attack != null)
							addToActivityQueue(new SimplePlayerAttackActivity(
									playerFlag, attack));
						if (attack == null) {
							canAttack = false;
						} else {
							int fromLandId = attack.getFromLandId();
							int toLandId = attack.getToLandId();
							boolean successAttacked = false;
							for (FullLand land : world.getFullLands()) {
								if (!successAttacked
										&& land.getLandId() == fromLandId
										&& land.getFlag().equals(playerFlag)
										&& land.getDiceCount() > 1) {
									for (FullLand neighbouringLand : land
											.getNeighbouringFullLands()) {
										if (!successAttacked
												&& !neighbouringLand.getFlag()
														.equals(playerFlag)
												&& neighbouringLand.getLandId() == toLandId) {
											Flag neighbouringLandFlag = neighbouringLand
													.getFlag();
											LandRollResult landRollResult = LandRoll
													.roll(land,
															neighbouringLand);
											if (landRollResult.isLeftWin()) {
												addTotalDiceCountByFlag(
														neighbouringLandFlag,
														(neighbouringLand
																.getDiceCount())
																* (-1));
												neighbouringLand
														.setDiceCount(DiceStack
																.valueOf(land
																		.getDiceCount() - 1));

												neighbouringLand
														.setFlag(playerFlag);

												addToActivityQueue(new SimpleLandUpdatedActivity(
														neighbouringLand));

												if (!world
														.isExistsLandByFlag(neighbouringLandFlag)) {
													world
															.getFlags()
															.remove(
																	neighbouringLandFlag);
												}

												land
														.setDiceCount(DiceStack.ONE);

												addToActivityQueue(new SimpleLandUpdatedActivity(
														land));
												addToActivityQueue(new SimpleTotalDiceCountChangedActivityImpl(
														neighbouringLandFlag,
														getTotalDiceCountByFlag(neighbouringLandFlag)));
											} else {

												addTotalDiceCountByFlag(
														playerFlag,
														(land.getDiceCount() - 1)
																* (-1));

												land
														.setDiceCount(DiceStack.ONE);

												addToActivityQueue(new SimpleTotalDiceCountChangedActivityImpl(
														playerFlag,
														getTotalDiceCountByFlag(playerFlag)));
												addToActivityQueue(new SimpleLandUpdatedActivity(
														land));
											}
											int m = world
													.getMaxConnectedLandsByFlag(neighbouringLandFlag);
											addToActivityQueue(new SimpleMaxConnectedLandsCountChangedActivityImpl(
													neighbouringLandFlag, m));
											m = world
													.getMaxConnectedLandsByFlag(playerFlag);
											addToActivityQueue(new SimpleMaxConnectedLandsCountChangedActivityImpl(
													playerFlag, m));
											successAttacked = true;
										}
									}
								}
							}
							if (!successAttacked) {
								throw new Exception();
							} else {
								for (int j = 0; j < playerCount; j++) {
									Flag f = playerFlagMap.get(players[j]);
									if (!f.equals(playerFlag)
											&& world.isExistsLandByFlag(f)) {
										players[i] = fireOpponentAttack(players[i], f, attack, immutableWorld);
									}
								}
							}
						}
					} catch (Exception e) {
						canAttack = false;
						e.printStackTrace();
					}
					stepNumber++;
					canAttack = canAttack && (turnNumber >= stepNumber);
					//This is fixed issue with eclipse debug mode... Eclipse can't garbage collect a huge amount of thread... :(
					System.gc();
					System.gc();
				}

				grantWorldByFlag(world, playerFlag);

			}
			turnNumber++;
		}

		// Add activity that the game is ended.
		if (t)
			addToActivityQueue(new SimpleGameEndedActivityImpl());
	}
	
	static class FireChooseFlagResult{
		Player player;
		Flag flag;
	}
	
	static class FireAttackResult{
		Player player;
		Attack attack;
	}
	
	static class CustomObjectInputStream extends ObjectInputStream {
	    private ClassLoader classLoader;
	    
	    public CustomObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
	        super(in);
	        this.classLoader = classLoader;
	    }
	    
	    protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
	        return Class.forName(desc.getName(), false, classLoader);
	    }
	}
}
