package ru.icl.dicewars.core.activity;

import java.util.List;

public interface PlayersLoadedActivity extends DiceWarsActivity{
	public List<String> getPlayerNames();
}
