package ru.icl.dicewars.core.roll;

enum Dice {
	ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6);

	private int value;

	private Dice(int value) {
		this.value = value;
	}

	public int getIntValue() {
		return value;
	}

	public static Dice valueOf(int value) {
		if (value < 1 || value > 6)
			throw new IllegalArgumentException();
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
		return SIX;
	}
}
