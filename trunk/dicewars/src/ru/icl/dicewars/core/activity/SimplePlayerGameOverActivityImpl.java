package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public class SimplePlayerGameOverActivityImpl implements PlayerGameOverActivity {
	private Flag flag;
	private int place;

	public SimplePlayerGameOverActivityImpl(Flag flag, int place) {
		if (place < 1 || place > 8) throw new IllegalArgumentException();
		this.flag = flag;
		this.place = place;
	}

	@Override
	public Flag getFlag() {
		return flag;
	}
	
	@Override
	public int getPlace() {
		return place;
	}
}
