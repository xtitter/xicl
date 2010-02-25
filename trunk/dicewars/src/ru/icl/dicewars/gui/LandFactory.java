package ru.icl.dicewars.gui;

import java.awt.Color;
import java.util.HashMap;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullLandImpl;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.Point;

class LandFactory {

	private HashMap<Integer, LandPainter> landPainters = new HashMap<Integer, LandPainter>();

	ColoredLand getLand(int landId, Flag flag) {
		if (landPainters.containsKey(Integer.valueOf(landId))) {
			return landPainters.get(Integer.valueOf(landId)).getLand(flag);
		}
		return null;
	}

	void buildTheWorld(FullWorld world) {
		for (FullLand land : world.getFullLands()) {
			LandPainter landPainter = new LandPainter(land, world.getFlags());
			landPainters.put(land.getLandId(), landPainter);
		}
	}

	void buildBackground(FullWorld world) {
		FullLand empty = new FullLandImpl(0);
		for (int i = WorldJPanel.MIN_X; i < WorldJPanel.MAX_X + 1; i++) {
			for (int j = WorldJPanel.MIN_Y; j < WorldJPanel.MAX_Y + 1; j++) {
				empty.getPoints().add(new Point(i, j));
			}
		}
		for (FullLand land : world.getFullLands()) {
			empty.getPoints().removeAll(land.getPoints());
		}
		LandPainter landPainter = new LandPainter(empty, new Color(240, 240,
				240, 150));
		landPainters.put(0, landPainter);
	}

	ColoredLand getBackground() {
		if (landPainters.containsKey(Integer.valueOf(0))) {
			return landPainters.get(Integer.valueOf(0)).getBackground();
		}
		return null;
	}
}
