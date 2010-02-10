package ru.icl.dicewars.core;

public enum DiceStack {
	ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8);

	private int value;

	private DiceStack(int value) {
		this.value = value;
	}

	public int getIntValue() {
		return value;
	}

	public static DiceStack valueOf(int value) {
		if (value < 1 || value > 8) throw new IllegalArgumentException();
		if (value == 1)
			return ONE;
		if (value == 2)
			return TWO;
		if (value == 3)
			return THREE;
		if (value == 4)
			return FOUR;
		if (value == 5)
			return FIVE;
		if (value == 6)
			return SIX;
		if (value == 7)
			return SEVEN;
		return EIGHT;
	}
}
