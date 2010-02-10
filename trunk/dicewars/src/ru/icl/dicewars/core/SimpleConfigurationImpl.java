package ru.icl.dicewars.core;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import ru.icl.dicewars.core.exception.InvalidConfigurationLoadingExecption;

public class SimpleConfigurationImpl implements Configuration {
	private final static String[] CONFIG_FILE_LOCATIONS = new String[] {
			"configuration.properties", "players.properties" };
	private final static String PLAYER_CLASSES = "players";
	private final static String MAX_DICE_COUNT_IN_RESERVE = "maxDiceCountInReserve";
	
	private static final int DEFAULT_MAX_DICE_COUNT_IN_RESERVE = 64;

	private int playersCount = 0;
	private int maxDiceCountInReserve = DEFAULT_MAX_DICE_COUNT_IN_RESERVE;
	private String[] classNames = new String[] {};
	private PlayerClassesLoader playerClassesLoader = null;
	private FullWorldGenerator fullWorldGenerator = null;
	private Object flag = new Object();
	private Object flag2 = new Object();

	public SimpleConfigurationImpl() {
		Properties properties = new Properties();
		try {
			for (int i = 0; i < CONFIG_FILE_LOCATIONS.length; i++) {
				properties.load(new FileReader(new File(
						CONFIG_FILE_LOCATIONS[i])));
			}
		} catch (Exception e) {
			throw new InvalidConfigurationLoadingExecption(e);
		}
		String players = properties.getProperty(PLAYER_CLASSES);
		String[] classNames = players.split(",");
		int playersCount = classNames.length;
		this.playersCount = playersCount;
		this.classNames = classNames;
		try{
			this.maxDiceCountInReserve = Integer.valueOf(properties.getProperty(MAX_DICE_COUNT_IN_RESERVE));
		}catch (NumberFormatException e) {
		}
	}

	@Override
	public FullWorldGenerator geFullWorldGenerator() {
		if (fullWorldGenerator == null) {
			synchronized (flag2) {
				if (fullWorldGenerator == null) {
                    RealFullWorldGeneratorImpl generator = new RealFullWorldGeneratorImpl();
                    generator.setPlayersCount(playersCount);
                    return generator;
				}
			}
		}
		return fullWorldGenerator;
	}

	@Override
	public PlayerClassesLoader getPlayerClassesLoader() {
		if (playerClassesLoader == null) {
			synchronized (flag) {
				if (playerClassesLoader == null) {
					playerClassesLoader = new SimplePlayerClassesLoaderImpl(
							classNames);
				}
			}
		}
		return playerClassesLoader;
	}

	@Override
	public int getPlayersCount() {
		return playersCount;
	}
	
	@Override
	public int getMaxDiceCountInReserve() {
		return maxDiceCountInReserve;
	}
}
