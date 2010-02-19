package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public class SimpleMaxConnectedLandsCountChangedActivityImpl implements
		MaxConnectedLandsCountChangedActivity {
	private Flag flag;
	private int landsCount;

	public SimpleMaxConnectedLandsCountChangedActivityImpl(Flag flag,
			int landsCount) {
		this.landsCount = landsCount;
		this.flag = flag;
	}

	@Override
	public Flag getFlag() {
		return flag;
	}

	@Override
	public int getLandsCount() {
		return landsCount;
	}
}
