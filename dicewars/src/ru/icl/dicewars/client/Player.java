package ru.icl.dicewars.client;

public interface Player {

	public String getName();

	public void init();

	public Lead attack(World world);
}
