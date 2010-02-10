package ru.icl.dicewars.client;

import java.util.List;
import java.util.Set;

public interface World {
	public Set<Land> getLands();
	
	public List<Flag> getFlags();
	
	public Flag getMyFlag();

	public int getDiceCountInReserve(Flag flag);

	public int getAvailableLeadCount();
}
