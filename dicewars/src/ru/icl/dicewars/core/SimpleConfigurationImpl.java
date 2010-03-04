package ru.icl.dicewars.core;

import ru.icl.dicewars.client.Player;

public class SimpleConfigurationImpl implements Configuration {
	
	private FullWorld fullWorld;
	
	private Class<Player>[] playerClasses;
	
	private int maxDiceCountInReserve;
	
	private ClassLoader classLoader;
	
	public SimpleConfigurationImpl(FullWorld fullWorld, Class<Player>[] playerClasses,
			int maxDiceCountInReserve) {
		this(fullWorld, playerClasses, maxDiceCountInReserve, null);
	}

	@SuppressWarnings("unchecked")
	public SimpleConfigurationImpl(FullWorld fullWorld, Class<Player>[] playerClasses,
			int maxDiceCountInReserve, ClassLoader classLoader) {
		if (fullWorld == null || playerClasses == null || playerClasses.length < 2
				|| playerClasses.length > 8)
			throw new IllegalArgumentException();

		this.fullWorld = fullWorld;
		
		this.playerClasses = new Class[playerClasses.length];

		if (classLoader != null){
			this.classLoader = classLoader;
		}else{
			this.classLoader = Thread.currentThread().getContextClassLoader();
		}
		
		int i = 0;
		for (Class<Player> playerClass : playerClasses) {
			this.playerClasses[i] = playerClass;
			i++;
		}

		if (maxDiceCountInReserve < 0) {
			this.maxDiceCountInReserve = 0;
		} else {
			this.maxDiceCountInReserve = maxDiceCountInReserve;
		}
	}
	
	@Override
	public FullWorld getFullWorld() {
		return fullWorld;
	}

	@Override
	public Class<Player>[] getPlayerClasses() {
		return playerClasses;
	}
	
	@Override
	public int getPlayersCount() {
		return getPlayerClasses().length;
	}

	@Override
	public int getMaxDiceCountInReserve() {
		return maxDiceCountInReserve;
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}
	
	@Override
	public long getMaxMemoryForPlayer() {
		return 1024*1024*8;
	}
	
	@Override
	public long getMaxTimeForAttackMethod() {
		return 1000;
	}
	
	@Override
	public long getMaxTimeForChooseFlagMethod() {
		return 100;
	}
	
	@Override
	public long getMaxTimeForInitMethod() {
		return 1000;
	}
	
	@Override
	public long getMaxTimeForOpponentAttackMethod() {
		return 100;
	}
	
	@Override
	public Type getType() {
		return Configuration.Type.NOTIFY;
	}
}
