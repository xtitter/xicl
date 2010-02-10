package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.core.FullWorld;

public class SimpleWorldCreatedActivityImpl implements WorldCreatedActivity{
	private FullWorld fullWorld;

	public SimpleWorldCreatedActivityImpl(FullWorld fullWorld) {
		this.fullWorld = fullWorld;
	}

	public FullWorld getFullWorld() {
		return fullWorld;
	}
}
