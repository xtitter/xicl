package ru.icl.dicewars.client;

import java.io.Serializable;
import java.util.Set;


public interface Land extends Serializable{
	public int getLandId();
	
	public int getDiceCount();
	
	public Flag getFlag();

	public Set<Land> getNeighbouringLands();
}
