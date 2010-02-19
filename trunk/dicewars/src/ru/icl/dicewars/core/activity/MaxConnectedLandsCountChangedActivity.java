package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public interface MaxConnectedLandsCountChangedActivity extends DiceWarsActivity {
	public Flag getFlag();
	
	public int getLandsCount();
}
