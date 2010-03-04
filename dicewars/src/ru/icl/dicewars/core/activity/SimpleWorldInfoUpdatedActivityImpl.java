package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public class SimpleWorldInfoUpdatedActivityImpl implements WorldInfoUpdatedActivity{
	private Flag flag;
	private int diceTotalCount;
	private int maxConnectedLandsCount;
	private int diceCountInReserve;
	
	public SimpleWorldInfoUpdatedActivityImpl(Flag flag, int totalDiceCount, int maxConnectedLandsCount, int diceCountInReserve) {
		if (flag == null) throw new IllegalArgumentException();
		if (totalDiceCount < 0) throw new IllegalArgumentException();
		if (maxConnectedLandsCount < 0) throw new IllegalArgumentException();
		if (diceCountInReserve < 0) throw new IllegalArgumentException();
		
		this.flag = flag;
		this.diceTotalCount = totalDiceCount;
		this.maxConnectedLandsCount = maxConnectedLandsCount;
		this.diceCountInReserve = diceCountInReserve;
	}
	
	@Override
	public int getTotalDiceCount() {
		return diceTotalCount;
	}
	
	@Override
	public Flag getFlag() {
		return flag;
	}
	
	@Override
	public int getDiceCountInReserve() {
		return diceCountInReserve;
	}
	
	@Override
	public int getMaxConnectedLandsCount() {
		return maxConnectedLandsCount;
	}
}
