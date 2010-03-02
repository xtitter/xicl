package ru.icl.dicewars.core.activity;

import java.util.List;

public class SimplePlayersLoadedActivityImpl implements PlayersLoadedActivity{
	private List<String> playerNames;
	
	public SimplePlayersLoadedActivityImpl(List<String> playerNames) {
		if (playerNames == null) throw new IllegalArgumentException();
		this.playerNames = playerNames;
	}
	
	@Override
	public List<String> getPlayerNames() {
		return playerNames;
	}
}
