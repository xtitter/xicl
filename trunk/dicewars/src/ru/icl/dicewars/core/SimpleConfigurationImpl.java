package ru.icl.dicewars.core;

import ru.icl.dicewars.client.Player;

public class SimpleConfigurationImpl implements Configuration {
	private Class<Player>[] playerClasses;
	private int maxDiceCountInReserve;
	
	private FullWorldGenerator fullWorldGenerator;
	private Object fullWorldGeneratorFlag = new Object();
	
	@SuppressWarnings("unchecked")
	public SimpleConfigurationImpl(Class<Player>[] playerClasses,
			int maxDiceCountInReserve) {
		if (playerClasses == null || playerClasses.length < 2
				|| playerClasses.length > 8)
			throw new IllegalArgumentException();

		this.playerClasses = new Class[playerClasses.length];

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
	public FullWorldGenerator geFullWorldGenerator() {
		if (fullWorldGenerator == null) {
			synchronized (fullWorldGeneratorFlag) {
				if (fullWorldGenerator == null) {
					RealFullWorldGeneratorImpl generator = new RealFullWorldGeneratorImpl();
					generator.setPlayersCount(getPlayersCount());
					return generator;
				}
			}
		}
		return fullWorldGenerator;
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
}
