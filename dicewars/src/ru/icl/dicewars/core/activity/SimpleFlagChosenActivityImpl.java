package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public class SimpleFlagChosenActivityImpl implements FlagChosenActivity{
	private Flag flag;
	private int position;
	
	public SimpleFlagChosenActivityImpl(Flag flag, int position) {
		if (flag == null) throw new IllegalArgumentException();
		this.flag = flag;
		this.position = position;
	}
	
	@Override
	public Flag getFlag() {
		return flag;
	}
	
	@Override
	public int getPosition() {
		return position;
	}
	
}
