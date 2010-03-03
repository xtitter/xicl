package ru.icl.dicewars.client;

import java.util.Set;

public interface Player {

	public String getName();

	public void init();
	
	public Flag chooseFlag(World world, Set<Flag> availableFlags);

	public void opponentAttack(Flag opponentFlag, Attack attack, World world);
	
	public Attack attack(World world);
}
