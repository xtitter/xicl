package ru.icl.dicewars.core.activity;

import java.util.Set;

import ru.icl.dicewars.core.FullLand;


public interface WorldGrantedActivity extends DiceWarsActivity{

	public Set<FullLand> getLands();
}
