package ru.icl.dicewars.core.roll;

public class LandRollResultImpl implements LandRollResult{
	private int leftSum;
	private int rightSum;
	
	public LandRollResultImpl(int leftSum, int rightSum) {
		if (leftSum < 1 || leftSum > 48) throw new IllegalArgumentException();
		if (rightSum < 1 || rightSum > 48) throw new IllegalArgumentException();
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
}
