package ru.icl.dicewars.core;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import ru.icl.dicewars.client.Attack;
import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;
import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.client.World;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.SimpleFlagChosenActivityImpl;
import ru.icl.dicewars.core.activity.SimpleGameEndedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleLandUpdatedActivity;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivityImpl;
import ru.icl.dicewars.core.activity.SimplePlayerGameOverActivityImpl;
import ru.icl.dicewars.core.activity.SimplePlayersLoadedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleTurnNumberChangedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleWorldCreatedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleWorldInfoUpdatedActivityImpl;
import ru.icl.dicewars.core.exception.InvalidPlayerClassInstatiationException;
import ru.icl.dicewars.core.exception.InvalidPlayersCountException;
import ru.icl.dicewars.core.roll.LandRoll;
import ru.icl.dicewars.core.roll.LandRollResult;
import ru.icl.dicewars.core.util.RandomUtil;
import ru.icl.dicewars.util.ClassUtil;
import ru.icl.dicewars.util.CloneUtil;

import com.javamex.classmexer.MemoryUtil;
import com.javamex.classmexer.MemoryUtil.VisibilityFilter;

public class GamePlayThread extends Thread {

	private static final Logger logger = Logger.getLogger(GamePlayThread.class
			.toString());

	private static final int AMOUNT_OF_THREADS_TO_START_GC = 200;

	private final static int MAX_ACTIVITY_COUNT_IN_QUEUE = 500;
	
	Configuration configuration;

	private boolean started = false;

	private Object startedFlag = new Object();

	volatile boolean t = true;

	private ActivityQueue activityQueue = new ActivityQueueImpl();

	private Map<Flag, Integer> totalDiceCountMap = new HashMap<Flag, Integer>();
	private boolean useActivityQueue = true;

	public boolean isUseActivityQueue() {
		return useActivityQueue;
	}

	public void setUseActivityQueue(boolean useActivityQueue) {
		this.useActivityQueue = useActivityQueue;
	}

	public DiceWarsActivity pollFromActivityQueue() {
		DiceWarsActivity activity = activityQueue.poll();
		synchronized (this) {
			this.notify();
		}
		return activity;
	}

	protected void addToActivityQueue(DiceWarsActivity activity) {
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

	public void kill() {
		t = false;
		synchronized (this) {
			this.notifyAll();
		}
	}

	public GamePlayThread(Configuration configuration) {
		this.configuration = configuration;
	}

	private boolean hasWinner(World world) {
		return world.getFlags().size() == 1;
	}

	void initPlayers(Player[] players) {
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

	void grantWorldByFlag(final FullWorld world, final Flag playerFlag) {
		int j = world.getMaxConnectedLandsByFlag(playerFlag);

		// *TODO speed up this block
		int totalAddedDiceCount = 0;
		while (j > 0) {
			//if (configuration.getMaxDiceCountInReserve() > world
			//	.getDiceCountInReserve(playerFlag)) {
				world.incDiceCountInReserve(playerFlag);
				totalAddedDiceCount++;
			//}
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
				fullLand.incDiceCount();
				addToActivityQueue(new SimpleLandUpdatedActivity(fullLand));
			} else {
				throw new IllegalStateException();
			}
			world.decDiceCountInReserve(playerFlag);
			land = getRandomLandForDiceIncreasingByFlag(world, playerFlag);
		}
		
		// *TODO speed up this block
		while (world.getDiceCountInReserve(playerFlag) > configuration.getMaxDiceCountInReserve()){
			world.decDiceCountInReserve(playerFlag);
			addTotalDiceCountByFlag(playerFlag, -1);
		}
		
		addToActivityQueue(new SimpleWorldInfoUpdatedActivityImpl(playerFlag, getTotalDiceCountByFlag(playerFlag), totalAddedDiceCount, world.getDiceCountInReserve(playerFlag)));
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

	private long calcPlayerMemoryUsage(Player player) {
		List<Object> l = new ArrayList<Object>();
		l.add(player);
		l.add(player.getClass());
		for (Class<?> clazz : player.getClass().getDeclaredClasses()){
			l.add(clazz);
		}
		return MemoryUtil.deepMemoryUsageOf(l, VisibilityFilter.ALL);
	}

	private Player copyPlayer(Player player) {
		try {
			return CloneUtil.deepCloneSerialization(player, configuration
					.getClassLoader());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e1) {
			throw new IllegalStateException(e1);
		}
	}

	// Bug with eclipse debug mode fixing...
	private int threadWasStartedCount = 0;

	private void threadCreatedNotify() {
		threadWasStartedCount++;
		if (threadWasStartedCount % AMOUNT_OF_THREADS_TO_START_GC == 0) {
			System.gc();
			System.gc();
		}
	}

	@SuppressWarnings("deprecation")
	private Player fireInit(final Player player) {
		if (configuration.getType() == Configuration.Type.OFF) {
			try{
				player.init();
			}catch (Exception e) {
			}
			return player;
		}

		class InitThread extends Thread {
			volatile Long startTime = null;

			@Override
			public void run() {
				try {
					//System.gc();
					startTime = System.currentTimeMillis();
					player.init();
					startTime = null;
				} catch (Exception e) {
				}
			}

			public Long getStartTime() {
				return startTime;
			}
		}
		InitThread initThread = new InitThread();
		initThread.setPriority(Thread.MAX_PRIORITY);
		Player previousStateOfPlayer = copyPlayer(player);
		threadCreatedNotify();
		initThread.start();
		boolean isMemoryLimitExceeded = false;
		boolean isTimeLimitExceeded = false;
		try {
			while (initThread.isAlive()) {
				long t1 = System.currentTimeMillis();
				Long startTime = initThread.getStartTime();
				if (!isTimeLimitExceeded
						&& startTime != null
						&& t1 - startTime.longValue() > configuration
								.getMaxTimeForInitMethod() + 10) {
					logger
							.info(player.getClass()
									+ " - time limit has exceeded on execution init method. Method execution was continued.");
					isTimeLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						initThread.stop();
						return previousStateOfPlayer;
					}
				}

				if (!isMemoryLimitExceeded
						&& calcPlayerMemoryUsage(player) > configuration
								.getMaxMemoryForPlayer()) {
					logger
							.info(player.getClass()
									+ " - memory limit has exceeded on execution of init method. Method execution was aborted.");
					isMemoryLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						initThread.stop();
						return previousStateOfPlayer;
					}
				}
				initThread.join(10);
			}
		} catch (InterruptedException e) {
			return previousStateOfPlayer;
		}
		return player;
	}

	@SuppressWarnings("deprecation")
	private FireChooseFlagResult fireChooseFlag(final Player player,
			final World world, final Set<Flag> availableFlags) {
		if (configuration.getType() == Configuration.Type.OFF) {
			Flag flag = null; 
			try{
				flag = player.chooseFlag(world, availableFlags);
			}catch (Exception e) {
				flag = null;
			}
			return new FireChooseFlagResult(flag, player);
		}

		class ChooseFlagThread extends Thread {
			volatile Long startTime = null;
			volatile Flag flag;

			@Override
			public void run() {
				try {
					//System.gc();
					startTime = System.currentTimeMillis();
					flag = player.chooseFlag(world, availableFlags);
					startTime = null;
				} catch (Exception e) {
					startTime = null;
				}
			}

			public Long getStartTime() {
				return startTime;
			}

			public Flag getFlag() {
				return flag;
			}
		}
		
		ChooseFlagThread chooseFlagThread = new ChooseFlagThread();
		chooseFlagThread.setPriority(Thread.MAX_PRIORITY);
		Player previousStateOfPlayer = (Player) copyPlayer(player);
		threadCreatedNotify();
		chooseFlagThread.start();
		boolean isMemoryLimitExceeded = false;
		boolean isTimeLimitExceeded = false;
		try {
			while (chooseFlagThread.isAlive()) {
				long t1 = System.currentTimeMillis();
				Long startTime = chooseFlagThread.getStartTime();
				if (!isTimeLimitExceeded
						&& startTime != null
						&& t1 - startTime.longValue() > configuration
								.getMaxTimeForChooseFlagMethod() + 10) {
					logger
							.info(player.getClass()
									+ " - time limit has exceeded on execution chooseFlag method. Method execution was continued.");
					isTimeLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						chooseFlagThread.stop();
						return new FireChooseFlagResult(null,
								previousStateOfPlayer);
					}
				}

				if (!isMemoryLimitExceeded
						&& calcPlayerMemoryUsage(player) > configuration
								.getMaxMemoryForPlayer()) {
					logger
							.info(player.getClass()
									+ " - memory limit has exceeded on execution of chooseFlag method. Method execution was aborted.");
					isMemoryLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						chooseFlagThread.stop();
						return new FireChooseFlagResult(null,
								previousStateOfPlayer);
					}
				}
				chooseFlagThread.join(10);
			}
		} catch (InterruptedException e) {
			return new FireChooseFlagResult(null, previousStateOfPlayer);
		}

		return new FireChooseFlagResult(chooseFlagThread.getFlag(), player);
	}

	@SuppressWarnings("deprecation")
	private Player fireOpponentAttack(final Player player,
			final Flag opponentFlag, final Attack attack, final World beforeWorld, final boolean wasAttackWon) {
		if (configuration.getType() == Configuration.Type.OFF) {
			try{
				player.opponentAttack(opponentFlag, attack, beforeWorld, wasAttackWon);
			}catch (Exception e) {
			}
			return player;
		}

		class OpponentAttackThread extends Thread {
			volatile Long startTime = null;

			@Override
			public void run() {
				try {
					//System.gc();
					startTime = System.currentTimeMillis();
					player.opponentAttack(opponentFlag, attack, beforeWorld, wasAttackWon);
					startTime = null;
				} catch (Exception e) {
				}
			}

			public Long getStartTime() {
				return startTime;
			}
		}
		OpponentAttackThread opponentAttackThread = new OpponentAttackThread();
		Player previousStateOfPlayer = (Player) copyPlayer(player);
		opponentAttackThread.setPriority(MAX_PRIORITY);
		threadCreatedNotify();
		opponentAttackThread.start();
		boolean isMemoryLimitExceeded = false;
		boolean isTimeLimitExceeded = false;
		try {
			while (opponentAttackThread.isAlive()) {
				long t1 = System.currentTimeMillis();
				Long startTime = opponentAttackThread.getStartTime();
				if (!isTimeLimitExceeded
						&& startTime != null
						&& t1 - startTime.longValue() > configuration
								.getMaxTimeForOpponentAttackMethod() + 10) {
					logger
							.info(player.getClass()
									+ " - time limit has exceeded on execution opponentAttack method. Method execution was continued.");
					isTimeLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						opponentAttackThread.stop();
						return previousStateOfPlayer;
					}
				}
				if (!isMemoryLimitExceeded
						&& calcPlayerMemoryUsage(player) > configuration
								.getMaxMemoryForPlayer()) {
					logger
							.info(player.getClass()
									+ " - memory limit has exceeded on execution of opponentAttack method. Method execution was aborted.");
					isMemoryLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						opponentAttackThread.stop();
						return previousStateOfPlayer;
					}
				}
				opponentAttackThread.join(10);
			}
		} catch (InterruptedException e) {
			return previousStateOfPlayer;
		}
		return player;
	}
	
	@SuppressWarnings("deprecation")
	private FireGetNameResult fireGetName(final Player player) {
		if (configuration.getType() == Configuration.Type.OFF) {
			String name = null;
			try{
				name = player.getName();
			}catch (Exception e) {
				name = null;
			}
			return new FireGetNameResult(name, player);
		}

		class GetNameThread extends Thread {
			volatile Long startTime = null;
			
			String playerName = null;

			@Override
			public void run() {
				try {
					//System.gc();
					startTime = System.currentTimeMillis();
					playerName = player.getName();
					startTime = null;
				} catch (Exception e) {
				}
			}

			public Long getStartTime() {
				return startTime;
			}
			
			public String getPlayerName() {
				return playerName;
			}
		}
		GetNameThread getNameThread = new GetNameThread();
		Player previousStateOfPlayer = (Player) copyPlayer(player);
		getNameThread.setPriority(MAX_PRIORITY);
		threadCreatedNotify();
		getNameThread.start();
		boolean isMemoryLimitExceeded = false;
		boolean isTimeLimitExceeded = false;
		try {
			while (getNameThread.isAlive()) {
				long t1 = System.currentTimeMillis();
				Long startTime = getNameThread.getStartTime();
				if (!isTimeLimitExceeded
						&& startTime != null
						&& t1 - startTime.longValue() > configuration
								.getMaxTimeForGetNameMethod() + 10) {
					logger
							.info(player.getClass()
									+ " - time limit has exceeded on execution getName method. Method execution was continued.");
					isTimeLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						getNameThread.stop();
						return new FireGetNameResult(null, previousStateOfPlayer);
					}
				}
				if (!isMemoryLimitExceeded
						&& calcPlayerMemoryUsage(player) > configuration
								.getMaxMemoryForPlayer()) {
					logger
							.info(player.getClass()
									+ " - memory limit has exceeded on execution of getName method. Method execution was aborted.");
					isMemoryLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						getNameThread.stop();
						return new FireGetNameResult(null, previousStateOfPlayer);
					}
				}
				getNameThread.join(10);
			}
		} catch (InterruptedException e) {
			return new FireGetNameResult(null, previousStateOfPlayer);
		}
		return new FireGetNameResult(getNameThread.getPlayerName(), previousStateOfPlayer);
	}

	@SuppressWarnings("deprecation")
	private FireAttackResult fireAttack(final Player player, final World world) {
		if (configuration.getType() == Configuration.Type.OFF) {
			Attack attack = player.attack(world);
			return new FireAttackResult(attack, player);
		}

		class AttackThread extends Thread {
			volatile Long startTime = null;
			volatile Attack attack = null;

			@Override
			public void run() {
				try {
					//System.gc();
					startTime = System.currentTimeMillis();
					attack = player.attack(world);
					startTime = null;
				} catch (Exception e) {
					startTime = null;
					attack = null;
				}
			}

			public Long getStartTime() {
				return startTime;
			}

			public Attack getAttack() {
				return attack;
			}
		}
		AttackThread attackThread = new AttackThread();
		attackThread.setPriority(Thread.MAX_PRIORITY);
		Player previousStateOfPlayer = (Player) copyPlayer(player);
		threadCreatedNotify();
		attackThread.start();
		boolean isMemoryLimitExceeded = false;
		boolean isTimeLimitExceeded = false;
		try {
			while (attackThread.isAlive()) {
				long t1 = System.currentTimeMillis();
				Long startTime = attackThread.getStartTime();
				if (!isTimeLimitExceeded
						&& startTime != null
						&& t1 - startTime.longValue() > configuration
								.getMaxTimeForAttackMethod() + 10) {
					logger
							.info(player.getClass()
									+ " - time limit has exceeded on execution attack method. Method execution was continued.");
					isTimeLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						attackThread.stop();
						return new FireAttackResult(null, previousStateOfPlayer);
					}
				}

				if (!isMemoryLimitExceeded
						&& calcPlayerMemoryUsage(player) > configuration
								.getMaxMemoryForPlayer()) {
					logger
							.info(player.getClass()
									+ " - memory limit has exceeded on execution of attack method. Method execution was aborted.");
					isMemoryLimitExceeded = true;
					if (configuration.getType() == Configuration.Type.INTERRUPT_EXECUTION) {
						attackThread.stop();
						return new FireAttackResult(null, previousStateOfPlayer);
					}
				}
				attackThread.join(10);
			}
		} catch (InterruptedException e) {
			return new FireAttackResult(null, previousStateOfPlayer);
		}

		return new FireAttackResult(attackThread.getAttack(), player);
	}

	Map<Player, Flag> chooseFlags(Player[] players, final World world) {
		int playerCount = players.length;

		Map<Player, Flag> playerToFlagMap = new HashMap<Player, Flag>();
		Map<Flag, Player> flagToPlayerMap = new HashMap<Flag, Player>();

		final World w = new ImmutableWorldImpl(world);

		Set<Flag> availableFlags = new HashSet<Flag>(world.getFlags());

		for (int i = 0; i < playerCount; i++) {
			Set<Flag> immutableAvailableFlags = Collections
					.unmodifiableSet(new HashSet<Flag>(availableFlags));
			try {
				FireChooseFlagResult result = fireChooseFlag(players[i], w,
						immutableAvailableFlags);
				Flag flag = result.flag;
				players[i] = result.player;
				if (!playerToFlagMap.values().contains(flag) && flag != null) {
					playerToFlagMap.put(players[i], flag);
					flagToPlayerMap.put(flag, players[i]);
					addToActivityQueue(new SimpleFlagChosenActivityImpl(flag, i));
					availableFlags.remove(flag);
				}
			} catch (Exception e) {
			}
		}

		for (int i = 0; i < playerCount; i++) {
			if (!flagToPlayerMap.values().contains(players[i])) {
				Flag flag = availableFlags.iterator().next();
				playerToFlagMap.put(players[i], flag);
				flagToPlayerMap.put(flag, players[i]);
				addToActivityQueue(new SimpleFlagChosenActivityImpl(flag, i));
				availableFlags.remove(flag);
			}
		}

		return playerToFlagMap;
	}

	Player[] instantiatePlayers() {
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

	FullWorld getInitialWorld() {
		try {
			return CloneUtil.deepCloneSerialization(configuration
					.getFullWorld());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException();
		} catch (IOException e1) {
			throw new IllegalStateException();
		}
	}

	void play() {
		Player[] players = instantiatePlayers();
		
		initPlayers(players);
		
		List<String> playerNames = new ArrayList<String>();
		for (int i = 0; i < players.length; i++) {
			FireGetNameResult fireGetNameResult = fireGetName(players[i]);
			playerNames.add(fireGetNameResult.getName());
			players[i] = fireGetNameResult.getPlayer();
		}
		addToActivityQueue(new SimplePlayersLoadedActivityImpl(playerNames));

		final int playerCount = players.length;

		final FullWorld world = getInitialWorld();

		addToActivityQueue(new SimpleWorldCreatedActivityImpl(
				new FullWorldImpl(world)));

		/* Players map to flags */
		Map<Player, Flag> playerToFlagMap = chooseFlags(players,
				new ImmutableWorldImpl(world));

		for (FullLand land : world.getFullLands()) {
			Flag flag = land.getFlag();
			addTotalDiceCountByFlag(flag, land.getDiceCount());
		}

		for (Flag flag : world.getFlags()) {
			int maxConectedLandsCount = world.getMaxConnectedLandsByFlag(flag);
			addToActivityQueue(new SimpleWorldInfoUpdatedActivityImpl(flag, getTotalDiceCountByFlag(flag), maxConectedLandsCount, 0));
		}

		/* Start the game */
		int turnNumber = 1;
		int place = playerCount;
		while (!hasWinner(world) && t) {
			addToActivityQueue(new SimpleTurnNumberChangedActivityImpl(turnNumber));
			for (int i = 0; i < playerCount; i++) {
				Flag playerFlag = playerToFlagMap.get(players[i]);
				world.setMyFlag(playerFlag);

				// Skip losers
				if (!world.getFlags().contains(playerFlag))
					continue;

				boolean canAttack = true;
				int stepNumber = 1;
				while (canAttack && t) {
					world.setAvailableAttackCount(turnNumber - stepNumber + 1);
					final World immutableWorld = new ImmutableWorldImpl(world);
					FireAttackResult fireAttackResult = fireAttack(players[i],
							immutableWorld);
					Attack attack = fireAttackResult.getAttack();
					players[i] = fireAttackResult.getPlayer();

					if (attack == null) {
						canAttack = false;
					} else {
						int fromLandId = attack.getFromLandId();
						int toLandId = attack.getToLandId();
						boolean successAttacked = false;
						boolean wasAttackWon = false;
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
												.roll(land, neighbouringLand);
										addToActivityQueue(new SimplePlayerAttackActivityImpl(
												attack, playerFlag, neighbouringLandFlag, landRollResult.getLeftDices(), landRollResult.getRightDices()));
										if (landRollResult.isLeftWin()) {
											wasAttackWon = true;
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
												world.getFlags().remove(
														neighbouringLandFlag);
												addToActivityQueue(new SimplePlayerGameOverActivityImpl(neighbouringLandFlag, place));
												place--;
											}

											land.setDiceCount(DiceStack.ONE);
											addToActivityQueue(new SimpleLandUpdatedActivity(
													land));
										} else {
											addTotalDiceCountByFlag(playerFlag,
													(land.getDiceCount() - 1)
															* (-1));
											land.setDiceCount(DiceStack.ONE);
											
											addToActivityQueue(new SimpleLandUpdatedActivity(
													land));
										}
										int m = world
												.getMaxConnectedLandsByFlag(neighbouringLandFlag);
										addToActivityQueue(new SimpleWorldInfoUpdatedActivityImpl(neighbouringLandFlag, getTotalDiceCountByFlag(neighbouringLandFlag), m, world.getDiceCountInReserve(neighbouringLandFlag)));
										
										m = world
												.getMaxConnectedLandsByFlag(playerFlag);
										addToActivityQueue(new SimpleWorldInfoUpdatedActivityImpl(playerFlag, getTotalDiceCountByFlag(playerFlag), m, world.getDiceCountInReserve(playerFlag)));
										successAttacked = true;
									}
								}
							}
						}
						if (successAttacked) {
							for (int j = 0; j < playerCount; j++) {
								Flag f = playerToFlagMap.get(players[j]);
								if (!f.equals(playerFlag)
										&& world.isExistsLandByFlag(f)) {
									players[j] = fireOpponentAttack(players[j],
											f, attack, immutableWorld, wasAttackWon);
								}
							}
						}else{
							for (int j = 0; j < playerCount; j++) {
								Flag f = playerToFlagMap.get(players[j]);
								if (!f.equals(playerFlag)
										&& world.isExistsLandByFlag(f)) {
									players[j] = fireOpponentAttack(players[j],
											f, null, immutableWorld, false);
								}
							}
						}
					}
					stepNumber++;
					canAttack = canAttack && (turnNumber >= stepNumber);
				}

				grantWorldByFlag(world, playerFlag);

			}
			turnNumber++;
		}
		if (t){
			if (hasWinner(world)){
				Flag flag = getWinner(world);
				addToActivityQueue(new SimplePlayerGameOverActivityImpl(flag, 1));
			}
			addToActivityQueue(new SimpleGameEndedActivityImpl());
		}
	}

	private Flag getWinner(World world){
		return world.getFlags().iterator().next();
	}
	
	@Override
	public final void run() {
		synchronized (startedFlag) {
			if (started)
				throw new IllegalStateException(
						"This thread can't be run twice.");
			started = true;
		}
		play();
	}

	static class FireChooseFlagResult {
		private Player player;
		private Flag flag;

		FireChooseFlagResult(Flag flag, Player player) {
			if (player == null)
				throw new IllegalArgumentException();
			this.player = player;
			this.flag = flag;
		}

		public Flag getFlag() {
			return flag;
		}

		public Player getPlayer() {
			return player;
		}
	}

	static class FireAttackResult {
		private Player player;
		private Attack attack;

		FireAttackResult(Attack attack, Player player) {
			this.attack = attack;
			this.player = player;
		}

		public Player getPlayer() {
			return player;
		}

		public Attack getAttack() {
			return attack;
		}
	}
	
	static class FireGetNameResult {
		private Player player;
		private String name;;

		FireGetNameResult(String name, Player player) {
			this.name = name;
			this.player = player;
		}

		public Player getPlayer() {
			return player;
		}
		
		public String getName() {
			return name;
		}
	}
}
