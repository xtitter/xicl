package ru.icl.dicewars.sample;

import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;
import ru.icl.dicewars.client.Attack;
import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.client.World;

public abstract class AbstractAttackPlayer implements Player{
	@Override
	public void init(){
	}
	
	@Override
	public Flag chooseFlag(World world, Set<Flag> availableFlags) {
		return availableFlags.iterator().next();
	}
	
	@Override
	public void opponentAttack(Flag apponentFlag, Attack attack, World world) {
	}

	@Override
	public Attack attack(World world) {
		Set<Land> lands = world.getLands();
		for (final Land land : lands) {
			if (land.getFlag().equals(world.getMyFlag()) && land.getDiceCount() > 1) {
				Set<Land> neighbouringLands = land.getNeighbouringLands();
				for (final Land neighbouringLand : neighbouringLands) {
					if (!neighbouringLand.getFlag().equals(world.getMyFlag()) && land.getDiceCount() > neighbouringLand.getDiceCount()) {
						return new Attack() {
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
		for (final Land land : lands) {
			if (land.getFlag().equals(world.getMyFlag()) && land.getDiceCount() > 1) {
				Set<Land> neighbouringLands = land.getNeighbouringLands();
				for (final Land neighbouringLand : neighbouringLands) {
					if (!neighbouringLand.getFlag().equals(world.getMyFlag()) && land.getDiceCount() >= neighbouringLand.getDiceCount()) {
						return new Attack() {
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
