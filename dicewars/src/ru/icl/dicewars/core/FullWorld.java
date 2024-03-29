package ru.icl.dicewars.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.World;

public interface FullWorld extends World {

	public void setMyFlag(Flag myFlag);

	public void setAvailableAttackCount(int availableAttackCount);

	public void setFlags(List<Flag> flags);

	public Set<FullLand> getFullLands();
	
	public void setFullLands(Set<FullLand> fullLands);
	
	public void setDiceReserve(Map<Flag, Integer> diceReserve);
	
	public Map<Flag, Integer> getDiceReserve();
	
	public void incDiceCountInReserve(Flag flag);
	
	public void decDiceCountInReserve(Flag flag);
	
	public boolean hasInReserve(Flag flag);
	
	public int getMaxConnectedLandsByFlag(Flag flag);
	
	public boolean isExistsLandByFlag(Flag flag);
}
