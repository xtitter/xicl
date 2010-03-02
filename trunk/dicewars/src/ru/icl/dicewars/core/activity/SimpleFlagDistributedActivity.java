package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public class SimpleFlagDistributedActivity implements FlagDistributedActivity{
	private Flag flag;
	private int position;
	
	public SimpleFlagDistributedActivity(int position, Flag flag) {
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
