package ru.icl.dicewars.core;

import java.util.ArrayList;
import java.util.List;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.core.exception.InvalidPlayerClassLoadingException;
import ru.icl.dicewars.util.ClassUtil;

public class SimplePlayerClassesLoaderImpl implements PlayerClassesLoader{
	private String[] classNames = new String[]{};
	
	public String[] getClassNames() {
		return classNames;
	}
	
	public SimplePlayerClassesLoaderImpl(String[] classNames) {
		this.classNames = classNames;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class<Player>[] getPlayers() {
		List<Class<Player>> playersList = new ArrayList<Class<Player>>();
		int playerCount = classNames.length;
		if (classNames.length != playerCount || playerCount > Flag.values().length) 
			throw new IllegalArgumentException();
		
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
}
