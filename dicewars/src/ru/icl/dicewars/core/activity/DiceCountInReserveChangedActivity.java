package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public interface DiceCountInReserveChangedActivity extends DiceWarsActivity {
	public int getDiceCount();

	public Flag getFlag();
}
