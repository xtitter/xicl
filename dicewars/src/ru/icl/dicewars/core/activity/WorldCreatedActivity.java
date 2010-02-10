package ru.icl.dicewars.core.activity;

import ru.icl.dicewars.core.FullWorld;

public interface WorldCreatedActivity extends DiceWarsActivity {
	public FullWorld getFullWorld();
}
