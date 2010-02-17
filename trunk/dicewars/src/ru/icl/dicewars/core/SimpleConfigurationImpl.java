package ru.icl.dicewars.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class SimpleConfigurationImpl implements Configuration {
	private final static String[] CONFIG_FILE_LOCATIONS = new String[] { "configuration.properties" };
	
	private final static String DEFAULT_PLAYER_PROPERTY_NAME = "default_players";
	
	private final static String PLAYERS_SCAN_DIR_PROPERTY_NAME = "players_scan_dir";
	
	private final static String MAX_DICE_COUNT_IN_RESERVE_PROPERTY_NAME = "max_dice_in_reserve";
	
	private final static String DEFAULT_PLAYER_SCAN_DIR = "players"; 
	
	private static final int DEFAULT_MAX_DICE_COUNT_IN_RESERVE = 64;

	private int maxDiceCountInReserve = DEFAULT_MAX_DICE_COUNT_IN_RESERVE;

	private FullWorldGenerator fullWorldGenerator = null;
	private Object flag2 = new Object();
	private String playerScanDir = DEFAULT_PLAYER_SCAN_DIR;

	private Class<Player>[] allPlayerClasses;
	private Class<Player>[] playerClasses;
	
	public SimpleConfigurationImpl() {
		Properties properties = new Properties();
		for (int i = 0; i < CONFIG_FILE_LOCATIONS.length; i++) {
			InputStream is = null;
			try{
				is = this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_LOCATIONS[i]);
				properties.load(new InputStreamReader(is));
			}catch (IOException e) {
				throw new InvalidConfigurationLoadingExecption(e);
			}finally{
				if (is != null){
					try{
						is.close();
					}catch (IOException e) {
					}
				}
			}
		}

		String players = properties.getProperty(DEFAULT_PLAYER_PROPERTY_NAME);
		String[] defaultPlayerClassNames = players.split(",");
		
		playerScanDir = properties.getProperty(PLAYERS_SCAN_DIR_PROPERTY_NAME);
		
		File dir = new File(playerScanDir);
		
		Set<String> classNames = new HashSet<String>();
		
		for (String className : defaultPlayerClassNames){
			classNames.add(className);
		}
		
		if (dir.isDirectory()){
			File[] files = dir.listFiles();
			Queue<File> queue = new LinkedList<File>();
			for (File file : files){
				queue.add(file);
			}
			while (!queue.isEmpty()){
				File file = queue.poll();
				if (file.isDirectory()){
					File[] fs = file.listFiles();
					for (File f : fs){
						queue.add(f);
					}		
				}else{
					String filename = file.getName();
					String ext = filename.substring(filename.lastIndexOf('.')+1, filename.length());
					if ("class".equals(ext)){
						try{
							ClassReader cr = new ClassReader(new FileInputStream(file));
							Boolean f = false;
							if (cr.getInterfaces().length > 0){
								for (String s : cr.getInterfaces()){
									if (s.replaceAll("/", ".").equals(Player.class.getName())){
										f = true;
									}
								}
							}
							if (f)
								classNames.add(cr.getClassName().replaceAll("/", "."));
						}catch (IOException e) {
						}
					}
				}
			}
		}
		
		Class<Player>[] allPlayerClasses = loadPlayerClasses(classNames.toArray(new String[]{}));
		
		this.allPlayerClasses = allPlayerClasses;
		this.playerClasses = allPlayerClasses;
		
		try{
			this.maxDiceCountInReserve = Integer.valueOf(properties.getProperty(MAX_DICE_COUNT_IN_RESERVE_PROPERTY_NAME));
		}catch (NumberFormatException e) {
		}
	}

	private Class<Player>[] loadPlayerClasses(String[] classNames) {
		List<Class<Player>> playersList = new ArrayList<Class<Player>>();
		
		ClassLoader classLoader = this.getClass().getClassLoader();
		for (String clazz : classNames) {
			try{
				Class<?> loadedClass = classLoader.loadClass(clazz);
				if (ClassUtil.isAssignable(Player.class, loadedClass)) {
					playersList.add((Class<Player>) loadedClass);
				}else{
					throw new InvalidPlayerClassLoadingException();
				}
			}catch (Exception e) {
				throw new InvalidPlayerClassLoadingException(e);
			}
		}
		return playersList.toArray(new Class[] {});
	}
	
	@Override
	public FullWorldGenerator geFullWorldGenerator() {
		if (fullWorldGenerator == null) {
			synchronized (flag2) {
				if (fullWorldGenerator == null) {
					RealFullWorldGeneratorImpl generator = new RealFullWorldGeneratorImpl();
					generator.setPlayersCount(getPlayersCount());
					return generator;
				}
			}
		}
		return fullWorldGenerator;
	}

	public Class<Player>[] getAllPlayers() {
		return allPlayerClasses;
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
