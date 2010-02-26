package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Attack;

public class SimplePlayerAttackActivity implements PlayerAttackActivity{
	private Flag flag;
	private Attack attack;
	public SimplePlayerAttackActivity(Flag flag, Attack attack) {
		if (flag == null) throw new IllegalArgumentException();
		if (attack == null) throw new IllegalArgumentException();
		this.flag = flag;
		this.attack = attack;
	}
	
	@Override
	public Flag getFlag() {
		return flag;
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
