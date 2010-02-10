package ru.icl.dicewars.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;

public class FullLandImpl implements FullLand, Serializable {
	private int landId;
	private DiceStack diceStack;
	private Flag flag;
	private Set<FullLand> neighbouringLands = new HashSet<FullLand>();
	private Set<Point> points = new HashSet<Point>();
	
	public FullLandImpl(int landId) {
		this.landId = landId;
	}
	
	public FullLandImpl(int landId, DiceStack diceStack, Flag flag,
			Set<FullLand> neighbouringLands, Set<Point> points) {
		if (landId < 0)
			throw new IllegalArgumentException();
		if (diceStack == null)
			throw new IllegalArgumentException();
		if (flag == null)
			throw new IllegalArgumentException();
		if (neighbouringLands == null)
			throw new IllegalArgumentException();
		if (points == null)
			throw new IllegalArgumentException();
		this.points = points;
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
	public Set<FullLand> getNeighbouringFullLands() {
		return neighbouringLands;
	}
	
	@Override
	public Set<Land> getNeighbouringLands() {
		Set<Land> result = new HashSet<Land>(neighbouringLands);
		return result;
	}

	@Override	
	public Set<Point> getPoints() {
		return points;
	}

	@Override
	public void setLandId(int landId) {
		if (landId < 0)
			throw new IllegalArgumentException();
		this.landId = landId;
	}

	@Override
	public void setDiceCount(DiceStack diceStack) {
		if (diceStack == null)
			throw new IllegalArgumentException();
		this.diceStack = diceStack;
	}
	
	@Override
	public void setFlag(Flag flag) {
		if (flag == null)
			throw new IllegalArgumentException();
		this.flag = flag;
	}

	@Override
	public void setNeighbouringLands(Set<FullLand> neighbouringLands) {
		if (neighbouringLands == null)
			throw new IllegalArgumentException();
		this.neighbouringLands = neighbouringLands;
	}

	@Override
	public void setPoints(Set<Point> points) {
		if (points == null)
			throw new IllegalArgumentException();
		this.points = points;
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
	
	@Override
	public void incDiceCount() {
		if (DiceStack.EIGHT.equals(diceStack)) throw new IllegalStateException();
		diceStack = DiceStack.valueOf(diceStack.getIntValue() + 1);
	}
}
