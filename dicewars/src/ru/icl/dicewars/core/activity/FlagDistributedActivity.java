package ru.icl.dicewars.core.activity;

import java.util.Set;

import ru.icl.dicewars.client.Flag;

public interface FlagDistributedActivity extends DiceWarsActivity {
	public Set<Flag> getFlags();

	public String getNameByFlag(Flag flag);
}
