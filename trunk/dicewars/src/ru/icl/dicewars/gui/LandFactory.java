package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullLandImpl;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.Point;

class LandFactory {
	final static LandFactory EMPTY_LAND_FACTORY = new LandFactory(null); 
	
	private HashMap<Integer, LandPainter> landPainters = new HashMap<Integer, LandPainter>();

	private BackgroundPainter backgroundPainter = null;
	
	public LandFactory(FullWorld fullWorld) {
		if (fullWorld != null){
			buildBackground(fullWorld);
			buildTheWorld(fullWorld);
		}
	}
	
	ColoredLand getLand(int landId, Flag flag) {
		if (landPainters.containsKey(Integer.valueOf(landId))) {
			return landPainters.get(Integer.valueOf(landId)).getLand(flag);
		}
		return null;
	}

	private void buildTheWorld(FullWorld world) {
		for (FullLand land : world.getFullLands()) {
			LandPainter landPainter = new LandPainter(land);
			landPainters.put(land.getLandId(), landPainter);
		}
	}

	private void buildBackground(FullWorld world) {
		FullLand empty = new FullLandImpl(0);
		for (int i = WorldJPanel.MIN_X; i < WorldJPanel.MAX_X + 1; i++) {
			for (int j = WorldJPanel.MIN_Y; j < WorldJPanel.MAX_Y + 1; j++) {
				empty.getPoints().add(new Point(i, j));
			}
		}
		for (FullLand land : world.getFullLands()) {
			empty.getPoints().removeAll(land.getPoints());
		}
		BackgroundPainter backgroundPainter = new BackgroundPainter(empty, new Color(230, 230,
				230));
		this.backgroundPainter = backgroundPainter;
	}

	BufferedImage getBackground() {
		if (backgroundPainter != null){
			return backgroundPainter.getBackgroundImage();
		}else{
			return null;
		}
	}
}
