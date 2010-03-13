package ru.icl.dicewars.client;

import java.io.Serializable;
import java.util.Set;

public interface Player extends Serializable{

	public String getName();

	public void init();
	
	public Flag chooseFlag(World world, Set<Flag> availableFlags);

	public void opponentAttack(Flag opponentFlag, Attack attack, World beforeWorld, boolean wasAttackWon);
	
	public Attack attack(World world);
}
