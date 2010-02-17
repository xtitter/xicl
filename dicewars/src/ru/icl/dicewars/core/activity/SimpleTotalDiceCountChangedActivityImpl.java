package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public class SimpleTotalDiceCountChangedActivityImpl implements
		TotalDiceCountChangedActivity {
	private Flag flag;
	private int totalDiceCount;
	
	public SimpleTotalDiceCountChangedActivityImpl(Flag flag, int totalDiceCount) {
		this.flag = flag;
		this.totalDiceCount = totalDiceCount;
	}
	
	@Override
	public int getTotalDiceCount() {
		return totalDiceCount;
	}

	@Override
	public Flag getFlag() {
		return flag;
	}
}
