package ru.icl.dicewars.core;

import ru.icl.dicewars.core.activity.DiceWarsActivity;

public interface ActivityQueue {
	public void add(DiceWarsActivity e);
	
	public DiceWarsActivity poll();
	
	public void clear();
	
	public int size();
}
