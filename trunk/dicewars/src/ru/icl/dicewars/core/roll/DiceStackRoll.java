package ru.icl.dicewars.core.roll;

import ru.icl.dicewars.core.DiceStack;

class DiceStackRoll {
	public static int roll(DiceStack diceStack) {
		if (diceStack == null) throw new IllegalArgumentException();
		int count = diceStack.getIntValue();
		int sum = 0;
		for (int i = 0; i < count; i++) {
			Dice diceSide = DiceRoll.roll();
			sum += diceSide.getIntValue();
		}
		return sum;
	}
}
