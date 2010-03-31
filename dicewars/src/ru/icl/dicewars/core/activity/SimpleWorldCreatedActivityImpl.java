package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.FullWorldImpl;

public class SimpleWorldCreatedActivityImpl implements WorldCreatedActivity{
	private FullWorld fullWorld;

	public SimpleWorldCreatedActivityImpl(FullWorld fullWorld) {
		this.fullWorld = new FullWorldImpl(fullWorld);
	}

	public FullWorld getFullWorld() {
		return new FullWorldImpl(fullWorld);
	}
}
