package ru.icl.dicewars.core;

import java.util.HashSet;
import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;

public class ImmutableLandImpl implements Land {
	private int landId;
	private DiceStack diceStack;
	private Flag flag;
	private Set<Land> neighbouringLands = new HashSet<Land>();

	public ImmutableLandImpl(int landId, DiceStack diceStack, Flag flag,
			Set<Land> neighbouringLands) {
		if (landId < 0)
			throw new IllegalArgumentException();
		if (diceStack == null)
			throw new IllegalArgumentException();
		if (flag == null)
			throw new IllegalArgumentException();
		if (neighbouringLands == null)
			throw new IllegalArgumentException();
		this.landId = landId;
		this.diceStack = diceStack;
		this.flag = flag;
		this.neighbouringLands = neighbouringLands;
	}

	@Override
	public int getDiceCount() {
		return diceStack.getIntValue();
	}

	@Override
	public Flag getFlag() {
		return flag;
	}

	@Override
	public int getLandId() {
		return landId;
	}

	@Override
	public Set<Land> getNeighbouringLands() {
		return neighbouringLands;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Land))
			return false;
		Land land = (Land) obj;
		return this.getLandId() == land.getLandId();
	}
	
	@Override
	public int hashCode() {
		return landId;
	}
}
