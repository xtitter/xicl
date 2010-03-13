package ru.icl.dicewars.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
import java.util.Scanner;
import java.util.Set;

import org.objectweb.asm.ClassReader;

import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.core.exception.InvalidConfigurationLoadingExecption;
import ru.icl.dicewars.core.exception.InvalidPlayerClassLoadingException;
import ru.icl.dicewars.util.ClassUtil;

public class ConfigurationLoader {
	private final static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private final static String[] CONFIG_FILE_LOCATIONS = new String[] { "configuration.properties" };

	private final static String DEFAULT_PLAYERS_PROPERTY_NAME = "default_players";

	private final static String PLAYERS_SCAN_DIR_PROPERTY_NAME = "players_scan_dir";
	
	private final static String PLAYERS_CONF_FILENAME_PROPERTY_NAME = "players_conf_filename";

	private final static String MAX_DICE_COUNT_IN_RESERVE_PROPERTY_NAME = "max_dice_in_reserve";

	private final static String DEFAULT_PLAYER_SCAN_DIR = "players";
	
	private final static String DEFAULT_PLAYERS_CONF_FILENAME = "players.conf";

	private static final int DEFAULT_MAX_DICE_COUNT_IN_RESERVE = 100;

	private static ConfigurationLoader configurationLoader = null;

	private static final Object sync = new Object();
	private static final Object sync2 = new Object();

	private int maxDiceCountInReserve = DEFAULT_MAX_DICE_COUNT_IN_RESERVE;
	
	private String playersConfFileName = DEFAULT_PLAYERS_CONF_FILENAME;

	private String playerScanDir = DEFAULT_PLAYER_SCAN_DIR;

	private Class<Player>[] allPlayerClasses;
	private Class<Player>[] playerClasses;

	private ClassLoader classLoader;
	
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

	public boolean storePlayersConf(Class<Player>[] classes, Boolean[] isActive){
		if (classes == null || isActive == null){
			throw new IllegalArgumentException();
		}
		if (classes.length != isActive.length){
			throw new IllegalArgumentException();
		}
		
		synchronized (sync2) {
			File file = new File(playersConfFileName);
			FileWriter fileWriter = null;
			try{
				fileWriter = new FileWriter(file);
				for (int i = 0;i<classes.length;i++){
					fileWriter.write(classes[i].getCanonicalName() + "," + isActive[i].toString() + LINE_SEPARATOR);
				}
				return true;
			}catch (IOException e) {
				return false;
			}finally{
				if (fileWriter != null)
				try{
					fileWriter.close();
				}catch (IOException e2) {
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
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

		String players = properties.getProperty(DEFAULT_PLAYERS_PROPERTY_NAME);
		
		String[] defaultPlayerClassNames;
		if (players != null){
			defaultPlayerClassNames = players.split(",");
		}else{
			defaultPlayerClassNames = new String[]{};
		}
		
		playerScanDir = properties.getProperty(PLAYERS_SCAN_DIR_PROPERTY_NAME, DEFAULT_PLAYER_SCAN_DIR);

		playersConfFileName = properties.getProperty(PLAYERS_CONF_FILENAME_PROPERTY_NAME, DEFAULT_PLAYERS_CONF_FILENAME);
		
		File dir = new File(playerScanDir);

		List<String> classNames = new ArrayList<String>();

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
		
		List<Class<Player>> allPlayerClassesSorted = new ArrayList<Class<Player>>();
		List<Class<Player>> playerClasses = new ArrayList<Class<Player>>();
		
		File file = new File(playersConfFileName);
		
		Set<Class<Player>> marker = new HashSet<Class<Player>>();
		
		try{
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()){
				String line = scanner.nextLine();
				String[] tmp = line.split(",");
				
				if (tmp.length != 2) throw new IllegalStateException();
				
				for (Class<Player> clazz : allPlayerClasses){
					if (clazz != null && tmp[0].trim().equals(clazz.getCanonicalName())){
						allPlayerClassesSorted.add(clazz);
						marker.add(clazz);
						if ("true".toUpperCase().equals(tmp[1].trim().toUpperCase())){
							playerClasses.add(clazz);
						}
						break;
					}
				}
			}
		}catch (IOException e) {
		}
		
		for (Class<Player> clazz : allPlayerClasses){
			if (!marker.contains(clazz)){
				allPlayerClassesSorted.add(clazz);
				playerClasses.add(clazz);
			}
		}

		this.allPlayerClasses = allPlayerClassesSorted.toArray(new Class[]{});
		this.playerClasses = playerClasses.toArray(new Class[]{});

		try {
			this.maxDiceCountInReserve = Integer.valueOf(properties
					.getProperty(MAX_DICE_COUNT_IN_RESERVE_PROPERTY_NAME));
			if (this.maxDiceCountInReserve < 0) {
				this.maxDiceCountInReserve = 0;
			}
		} catch (NumberFormatException e) {
		}
	}
	
	public ClassLoader getClassLoader(){
		return this.classLoader;
	}
	
	@SuppressWarnings("unchecked")
	private Class<Player>[] loadPlayerClasses(String[] classNames) {
		List<Class<Player>> playersList = new ArrayList<Class<Player>>();
		File dir = new File(playerScanDir);
		try{
			this.classLoader = new URLClassLoader(new URL[]{dir.toURI().toURL()});
		}catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
		for (String clazz : classNames) {
			try {
				Class<?> loadedClass = this.classLoader.loadClass(clazz);
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
