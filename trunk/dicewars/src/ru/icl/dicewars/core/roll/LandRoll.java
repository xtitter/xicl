package ru.icl.dicewars.core.roll;

import ru.icl.dicewars.client.Land;
import ru.icl.dicewars.core.DiceStack;

public class LandRoll {
	/**
	 * Left side land argument attack right side land.
	 * @param leftLand
	 * @param rightLand
	 * @return
	 */
	public static LandRollResult roll(Land leftLand, Land rightLand){
		if (leftLand == null || rightLand == null) throw new IllegalArgumentException();
		if (leftLand.getFlag().equals(rightLand.getFlag())) throw new IllegalArgumentException();
		if (rightLand.getDiceCount() < 1 || rightLand.getDiceCount() > 8) throw new IllegalArgumentException();
		if (leftLand.getDiceCount() < 2 || leftLand.getDiceCount() > 8) throw new IllegalArgumentException();
		
		DiceStack leftLandDiceStack = DiceStack.valueOf(leftLand.getDiceCount());
		DiceStack rightLandDiceStack = DiceStack.valueOf(rightLand.getDiceCount());
		
		int leftLandDiceStackResult = DiceStackRoll.roll(leftLandDiceStack);
		int rightLandDiceStackResult = DiceStackRoll.roll(rightLandDiceStack);
		
		return new LandRollResultImpl(leftLandDiceStackResult, rightLandDiceStackResult);
	}
}
