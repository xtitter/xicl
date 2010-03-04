package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public interface WorldInfoUpdatedActivity extends DiceWarsActivity {
	public Flag getFlag();
	
	public int getTotalDiceCount();

	public int getDiceCountInReserve();

	public int getMaxConnectedLandsCount();
}
