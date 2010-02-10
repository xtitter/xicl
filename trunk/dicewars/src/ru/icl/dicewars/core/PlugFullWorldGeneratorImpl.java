package ru.icl.dicewars.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ru.icl.dicewars.client.Flag;

public class PlugFullWorldGeneratorImpl implements FullWorldGenerator {

	private int playersCount;

	public void setPlayersCount(int playersCount) {
		this.playersCount = playersCount;
	}

	@Override
	public FullWorld generate() {
		Flag[] flags = Flag.values();
		List<Flag> playerFlags = new ArrayList<Flag>();
		for (int i = 0; i < playersCount; i++) {
			playerFlags.add(flags[i]);
		}
		
		Random rnd = new Random(System.currentTimeMillis());
		Set<FullLand> lands = new HashSet<FullLand>();
		for (int i = 0; i < 5; i++) {
			for (int j = 1; j <= 5; j++) {
				int landId = i * 5 + j;
				FullLand fullLand = new FullLandImpl(landId);
				//fullLand.setFlag(flags[(i * 5 + j) % playersCount]);
				if (i * 5 + j < 13){
					fullLand.setFlag(flags[0]);
				}else{
					fullLand.setFlag(flags[1]);
				}
				if (i*5 + j == 13){
					fullLand.setDiceCount(DiceStack.valueOf(1));
				}else{
					fullLand.setDiceCount(DiceStack.valueOf(8));
				}
				Set<Point> points = new HashSet<Point>();
				for (int y = 0; y < 20; y++) {
					for (int x = 1; x <= 20; x++) {
						points.add(new Point((j - 1) * 20 + x, i * 20 + y + 1));
					}
				}
				fullLand.setPoints(points);
				lands.add(fullLand);
			}
		}

		for (FullLand land : lands) {
			Set<FullLand> neighbouringFullLands = new HashSet<FullLand>();
			for (FullLand land2 : lands) {
				if (land2.getLandId() == land.getLandId() - 5) {
					neighbouringFullLands.add(land2);
				}
				if (land2.getLandId() == land.getLandId() + 5) {
					neighbouringFullLands.add(land2);
				}
				if (land2.getLandId() == land.getLandId() + 1
						&& land.getLandId() % 5 != 0) {
					neighbouringFullLands.add(land2);
				}
				if (land2.getLandId() == land.getLandId() - 1
						&& (land.getLandId() - 1) % 5 != 0) {
					neighbouringFullLands.add(land2);
				}
			}
			land.setNeighbouringLands(neighbouringFullLands);
		}

		FullWorld world = new FullWorldImpl(0, lands, playerFlags,
				new HashMap<Flag, Integer>());
		return world;
	}
}
