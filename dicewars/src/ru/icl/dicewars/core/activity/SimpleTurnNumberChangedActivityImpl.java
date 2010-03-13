package ru.icl.dicewars.core.activity;

public class SimpleTurnNumberChangedActivityImpl implements
		TurnNumberChangedActivity {
	private int turnNumber;

	public SimpleTurnNumberChangedActivityImpl(int turnNumber) {
		if (turnNumber < 0)
			throw new IllegalArgumentException();
		this.turnNumber = turnNumber;
	}

	@Override
	public int getTurnNumber() {
		return turnNumber;
	}
}
