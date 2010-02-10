package ru.icl.dicewars.sample;

import java.util.Set;

import ru.icl.dicewars.client.Land;
import ru.icl.dicewars.client.Lead;
import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.client.World;

public class AttackPlayer4 implements Player {
	@Override
	public String getName() {
		return "AttackPlayer4";
	}

	@Override
	public void init(){
	}

	@Override
	public Lead attack(World world) {
		Set<Land> lands = world.getLands();
		for (final Land land : lands) {
			if (land.getFlag().equals(world.getMyFlag()) && land.getDiceCount() > 1) {
				Set<Land> neighbouringLands = land.getNeighbouringLands();
				for (final Land neighbouringLand : neighbouringLands) {
					if (!neighbouringLand.getFlag().equals(world.getMyFlag())) {
						return new Lead() {
							@Override
							public int getFromLandId() {
								return land.getLandId();
							}

							@Override
							public int getToLandId() {
								return neighbouringLand.getLandId();
							}
						};
					}
				}
			}
		}
		return null;
	}
}
