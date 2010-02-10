package ru.icl.dicewars.gui.manager;

import javax.swing.BorderFactory;

import org.apache.log4j.Logger;

import ru.icl.dicewars.gui.World;

public class WindowManager {
	
	private static WindowManager windowManager = null;
	private static final Logger log = Logger.getLogger(WindowManager.class);
	
	private World world;
	
	public static WindowManager getManager() {
		if (windowManager == null) {
			windowManager = new WindowManager();
		}
		return windowManager;
	}
	
	public World getWorld() {
		if (world == null) {
			world = new World();
			world.setBorder(BorderFactory.createEtchedBorder());
		}
		return world;
	}
	
}
