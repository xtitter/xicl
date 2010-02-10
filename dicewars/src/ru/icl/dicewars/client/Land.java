package ru.icl.dicewars.client;

import java.util.Set;


public interface Land {
	public int getLandId();
	
	public int getDiceCount();
	
	public Flag getFlag();

	public Set<Land> getNeighbouringLands();
}
