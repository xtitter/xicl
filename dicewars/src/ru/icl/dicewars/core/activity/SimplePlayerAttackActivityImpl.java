package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Attack;

public class SimplePlayerAttackActivityImpl implements PlayerAttackActivity{
	private Attack attack;
	private boolean isWin;
	public SimplePlayerAttackActivityImpl(Attack attack, boolean isWin) {
		if (attack == null) throw new IllegalArgumentException();
		this.attack = attack;
		this.isWin = isWin;
	}
	
	@Override
	public int getFromLandId(){
		return attack.getFromLandId();
	}
	
	@Override
	public int getToLandId(){
		return attack.getToLandId();
	}
	
	public boolean isWin() {
		return isWin;
	}
}
