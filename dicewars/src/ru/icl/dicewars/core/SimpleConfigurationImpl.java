package ru.icl.dicewars.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import org.objectweb.asm.ClassReader;

import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.core.exception.InvalidConfigurationLoadingExecption;

public class SimpleConfigurationImpl implements Configuration {
	private final static String[] CONFIG_FILE_LOCATIONS = new String[] { "configuration.properties" };
	
	private final static String DEFAULT_PLAYER_PROPERTY_NAME = "default_players";
	
	private final static String PLAYERS_SCAN_DIR_PROPERTY_NAME = "players_scan_dir";
	
	private final static String MAX_DICE_COUNT_IN_RESERVE_PROPERTY_NAME = "max_dice_in_reserve";
	
	private final static String DEFAULT_PLAYER_SCAN_DIR = "players"; 
	
	private static final int DEFAULT_MAX_DICE_COUNT_IN_RESERVE = 64;

	private int playersCount = 0;
	private int maxDiceCountInReserve = DEFAULT_MAX_DICE_COUNT_IN_RESERVE;
	private String[] classNames = new String[] {};
	private PlayerClassesLoader playerClassesLoader = null;
	private FullWorldGenerator fullWorldGenerator = null;
	private Object flag = new Object();
	private Object flag2 = new Object();
	private String playerScanDir = DEFAULT_PLAYER_SCAN_DIR;

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
		
		this.classNames = classNames.toArray(new String[]{});
		this.playersCount = classNames.size();
		
		try{
			this.maxDiceCountInReserve = Integer.valueOf(properties.getProperty(MAX_DICE_COUNT_IN_RESERVE_PROPERTY_NAME));
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
