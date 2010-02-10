package ru.icl.dicewars.core;

public interface Configuration {
	public int getPlayersCount();
	
	public int getMaxDiceCountInReserve();

	public PlayerClassesLoader getPlayerClassesLoader();
	
	public FullWorldGenerator geFullWorldGenerator();
}
