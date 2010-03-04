package ru.icl.dicewars.core;

import ru.icl.dicewars.client.Player;

public interface Configuration {
	public int getPlayersCount();
	
	public int getMaxDiceCountInReserve();

	public Class<Player>[] getPlayerClasses();
	
	public ClassLoader getClassLoader();
	
	public FullWorld getFullWorld();
	
	public long getMaxMemoryForPlayer();
	
	public long getMaxTimeForInitMethod();
	
	public long getMaxTimeForChooseFlagMethod();
	
	public long getMaxTimeForOpponentAttackMethod();
	
	public long getMaxTimeForAttackMethod();
	
	public Type getType();
	
	public static enum Type{
		INTERRUPT_EXECUTION, NOTIFY, OFF
	}
}
