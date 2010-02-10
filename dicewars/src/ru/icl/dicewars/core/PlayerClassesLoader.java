package ru.icl.dicewars.core;

import ru.icl.dicewars.client.Player;

public interface PlayerClassesLoader {
	public Class<Player>[] getPlayers();
}
