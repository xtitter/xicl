package ru.icl.dicewars.core.roll;

public interface LandRollResult {
	public int getLeftSum();

	public int getRightSum();

	public boolean isLeftWin();

	public boolean isRightWin();

	public boolean isLeftLose();

	public boolean isRightLose();
}
