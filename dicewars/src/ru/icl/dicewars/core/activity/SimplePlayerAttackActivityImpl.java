package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Attack;

public class SimplePlayerAttackActivityImpl implements PlayerAttackActivity{
	private Attack attack;
	public SimplePlayerAttackActivityImpl(Attack attack) {
		if (attack == null) throw new IllegalArgumentException();
		this.attack = attack;
	}
	
	@Override
	public int getFromLandId(){
		return attack.getFromLandId();
	}
	
	@Override
	public int getToLandId(){
		return attack.getToLandId();
	}
}
