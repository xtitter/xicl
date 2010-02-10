package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;

public interface PlayerAttackActivity extends DiceWarsActivity {
	public Flag getFlag();

	public int getFromLandId();

	public int getToLandId();
}
