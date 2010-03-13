package ru.icl.dicewars.core.roll;

import java.util.List;

public interface LandRollResult {
	
	public List<Integer> getRightDices();
	
	public List<Integer> getLeftDices();
	
	public int getLeftSum();

	public int getRightSum();

	public boolean isLeftWin();

	public boolean isRightWin();

	public boolean isLeftLose();

	public boolean isRightLose();
}
