package ru.icl.dicewars.core;

import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Land;

public interface FullLand extends Land{
	public Set<Point> getPoints();
	
	public void setPoints(Set<Point> points);
	
	public void setLandId(int landId);

	public void setDiceCount(DiceStack diceStack);
	
	public void setFlag(Flag flag);

	public Set<FullLand> getNeighbouringFullLands();
	
	public void setNeighbouringLands(Set<FullLand> neighbouringLands);
	
	public void incDiceCount();
}
