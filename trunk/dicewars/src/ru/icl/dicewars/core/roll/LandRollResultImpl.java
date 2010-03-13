package ru.icl.dicewars.core.roll;

import java.util.List;

public class LandRollResultImpl implements LandRollResult{
	private int leftSum;
	private int rightSum;
	
	private List<Integer> leftDicesList;
	private List<Integer> rightDicesList;
	
	public LandRollResultImpl(int leftSum, int rightSum, List<Integer> leftDicesList,  List<Integer> rightDicesList) {
		if (leftSum < 1 || leftSum > 48) throw new IllegalArgumentException();
		if (rightSum < 1 || rightSum > 48) throw new IllegalArgumentException();
		
		if (leftDicesList.size() < 1 || rightDicesList.size() > 8 || rightDicesList.size() < 1 || rightDicesList.size() > 8)
			throw new IllegalArgumentException();
		
		for (Integer i : rightDicesList){
			if (i < 1 || i > 6) throw new IllegalArgumentException();
		}

		for (Integer i : leftDicesList){
			if (i < 1 || i > 6) throw new IllegalArgumentException();
		}
		
		this.leftDicesList = leftDicesList;
		this.rightDicesList = rightDicesList;		
		
		this.leftSum = leftSum;
		this.rightSum = rightSum;
	}
	
	@Override
	public int getLeftSum() {
		return leftSum;
	}
	
	@Override
	public int getRightSum() {
		return rightSum;
	}
	
	@Override
	public boolean isLeftLose() {
		return isRightWin();
	}
	
	@Override
	public boolean isLeftWin() {
		return leftSum > rightSum;
	}
	
	@Override
	public boolean isRightLose() {
		return isLeftWin();
	}
	
	@Override
	public boolean isRightWin() {
		return !isLeftWin();
	}
	
	@Override
	public List<Integer> getLeftDices() {
		return leftDicesList;
	}
	
	@Override
	public List<Integer> getRightDices() {
		return rightDicesList;
	}
}
