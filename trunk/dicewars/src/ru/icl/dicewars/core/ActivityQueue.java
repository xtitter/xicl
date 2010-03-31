package ru.icl.dicewars.core;

import java.io.Serializable;

import ru.icl.dicewars.core.activity.DiceWarsActivity;

public interface ActivityQueue extends Serializable{
	public void add(DiceWarsActivity e);
	
	public DiceWarsActivity poll();
	
	public void clear();
	
	public int size();

	public boolean hasNext();
}
