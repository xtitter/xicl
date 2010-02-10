package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Lead;

public class SimplePlayerAttackActivity implements PlayerAttackActivity{
	private Flag flag;
	private Lead lead;
	public SimplePlayerAttackActivity(Flag flag, Lead lead) {
		if (flag == null) throw new IllegalArgumentException();
		if (lead == null) throw new IllegalArgumentException();
		this.flag = flag;
		this.lead = lead;
	}
	
	@Override
	public Flag getFlag() {
		return flag;
	}
	
	@Override
	public int getFromLandId(){
		return lead.getFromLandId();
	}
	
	@Override
	public int getToLandId(){
		return lead.getToLandId();
	}
}
