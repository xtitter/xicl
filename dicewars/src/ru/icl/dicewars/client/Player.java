package ru.icl.dicewars.client;

public interface Player {

	public String getName();

	public void init();

	public void apponentAttack(Flag apponentFlag, Lead lead, World world);
	
	public Lead attack(World world);
}
