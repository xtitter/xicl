package ru.icl.dicewars.client;

import java.util.Set;

public interface World {
	public Set<Land> getLands();
	
	public Set<Flag> getFlags();
	
	public Flag getMyFlag();

	public int getDiceCountInReserve(Flag flag);

	public int getAvailableLeadCount();
}
