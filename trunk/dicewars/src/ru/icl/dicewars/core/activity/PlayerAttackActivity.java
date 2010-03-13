package ru.icl.dicewars.core.activity;

import java.util.List;

import ru.icl.dicewars.client.Flag;

public interface PlayerAttackActivity extends DiceWarsActivity {
	public int getFromLandId();

	public int getToLandId();

	public List<Integer> getPlayerDices();

	public List<Integer> getOpponentDices();

	public Flag getOpponentFlag();

	public Flag getPlayerFlag();
}
