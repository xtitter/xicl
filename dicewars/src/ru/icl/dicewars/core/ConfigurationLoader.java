package ru.icl.dicewars.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import org.objectweb.asm.ClassReader;

import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.core.exception.InvalidConfigurationLoadingExecption;
import ru.icl.dicewars.core.exception.InvalidPlayerClassLoadingException;
import ru.icl.dicewars.util.ClassUtil;

public class ConfigurationLoader {
	private final static String[] CONFIG_FILE_LOCATIONS = new String[] { "configuration.properties" };

	private final static String DEFAULT_PLAYER_PROPERTY_NAME = "default_players";

	private final static String PLAYERS_SCAN_DIR_PROPERTY_NAME = "players_scan_dir";

	private final static String MAX_DICE_COUNT_IN_RESERVE_PROPERTY_NAME = "max_dice_in_reserve";

	private final static String DEFAULT_PLAYER_SCAN_DIR = "players";

	private static final int DEFAULT_MAX_DICE_COUNT_IN_RESERVE = 64;

	private static ConfigurationLoader configurationLoader = null;

	private static final Object sync = new Object();

	private int maxDiceCountInReserve = DEFAULT_MAX_DICE_COUNT_IN_RESERVE;

	private String playerScanDir = DEFAULT_PLAYER_SCAN_DIR;

	private Class<Player>[] allPlayerClasses;
	private Class<Player>[] playerClasses;

	private ConfigurationLoader() {
	}

	public static ConfigurationLoader getInstance() {
		if (configurationLoader == null) {
			synchronized (sync) {
				if (configurationLoader == null) {
					configurationLoader = new ConfigurationLoader();
					configurationLoader.load();
				}
			}
		}
		return configurationLoader;
	}

	public void load() {
		Properties properties = new Properties();
		for (int i = 0; i < CONFIG_FILE_LOCATIONS.length; i++) {
			InputStream is = null;
			try {
				is = this.getClass().getClassLoader().getResourceAsStream(
						CONFIG_FILE_LOCATIONS[i]);
				properties.load(new InputStreamReader(is));
			} catch (IOException e) {
				throw new InvalidConfigurationLoadingExecption(e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}

		String players = properties.getProperty(DEFAULT_PLAYER_PROPERTY_NAME);
		String[] defaultPlayerClassNames = players.split(",");

		playerScanDir = properties.getProperty(PLAYERS_SCAN_DIR_PROPERTY_NAME);

		File dir = new File(playerScanDir);

		Set<String> classNames = new HashSet<String>();

		for (String className : defaultPlayerClassNames) {
			classNames.add(className);
		}

		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			Queue<File> queue = new LinkedList<File>();
			for (File file : files) {
				queue.add(file);
			}
			while (!queue.isEmpty()) {
				File file = queue.poll();
				if (file.isDirectory()) {
					File[] fs = file.listFiles();
					for (File f : fs) {
						queue.add(f);
					}
				} else {
					String filename = file.getName();
					String ext = filename.substring(
							filename.lastIndexOf('.') + 1, filename.length());
					if ("class".equals(ext)) {
						try {
							ClassReader cr = new ClassReader(
									new FileInputStream(file));
							Boolean f = false;
							if (cr.getInterfaces().length > 0) {
								for (String s : cr.getInterfaces()) {
									if (s.replaceAll("/", ".").equals(
											Player.class.getName())) {
										f = true;
									}
								}
							}
							if (f)
								classNames.add(cr.getClassName().replaceAll(
										"/", "."));
						} catch (IOException e) {
						}
					}
				}
			}
		}

		Class<Player>[] allPlayerClasses = loadPlayerClasses(classNames
				.toArray(new String[] {}));

		this.allPlayerClasses = allPlayerClasses;
		this.playerClasses = allPlayerClasses;

		try {
			this.maxDiceCountInReserve = Integer.valueOf(properties
					.getProperty(MAX_DICE_COUNT_IN_RESERVE_PROPERTY_NAME));
			if (this.maxDiceCountInReserve < 0) {
				this.maxDiceCountInReserve = 0;
			}
		} catch (NumberFormatException e) {
		}
	}
	
	@SuppressWarnings("unchecked")
	private Class<Player>[] loadPlayerClasses(String[] classNames) {
		List<Class<Player>> playersList = new ArrayList<Class<Player>>();
		File dir = new File(playerScanDir);
		ClassLoader classLoader = null;
		try{
			classLoader = new URLClassLoader(new URL[]{dir.toURI().toURL()});
		}catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
		for (String clazz : classNames) {
			try {
				Class<?> loadedClass = classLoader.loadClass(clazz);
				if (ClassUtil.isAssignable(Player.class, loadedClass)) {
					playersList.add((Class<Player>) loadedClass);
				} else {
					throw new InvalidPlayerClassLoadingException();
				}
			} catch (Exception e) {
				throw new InvalidPlayerClassLoadingException(e);
			}
		}
		return playersList.toArray(new Class[] {});
	}

	public Class<Player>[] getAllPlayerClasses() {
		return allPlayerClasses;
	}

	public Class<Player>[] getPlayerClasses() {
		return playerClasses;
	}
	
	public int getMaxDiceCountInReserve() {
		return maxDiceCountInReserve;
	}
}
