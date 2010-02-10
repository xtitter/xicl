package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public class SimpleDiceCountInReserveChangedActivity implements DiceCountInReserveChangedActivity{
	private Flag flag;
	private int diceCount;
	
	public SimpleDiceCountInReserveChangedActivity(Flag flag, int diceCount) {
		if (flag == null) throw new IllegalArgumentException();
		if (diceCount < 0) throw new IllegalArgumentException();
		
		this.flag = flag;
		this.diceCount = diceCount;
	}
	
	@Override
	public int getDiceCount() {
		return diceCount;
	}
	
	@Override
	public Flag getFlag() {
		return flag;
	}
}
