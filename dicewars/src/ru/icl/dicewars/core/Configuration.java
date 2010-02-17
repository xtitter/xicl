package ru.icl.dicewars.core;

import ru.icl.dicewars.client.Player;

public interface Configuration {
	public int getPlayersCount();
	
	public int getMaxDiceCountInReserve();

	public Class<Player>[] getPlayerClasses();
	
	public FullWorldGenerator geFullWorldGenerator();
}
