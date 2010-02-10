package ru.icl.dicewars.core.roll;

import ru.icl.dicewars.core.util.RandomUtil;

class DiceRoll {
	public static Dice roll() {
		int d = RandomUtil.getRandomInt(6) + 1;
		return Dice.valueOf(d);
	}
}
