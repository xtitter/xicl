package ru.icl.dicewars.core.activity;

import java.util.List;

import ru.icl.dicewars.client.Flag;

public interface FlagDistributedActivity extends DiceWarsActivity {
	public List<Flag> getFlags();

	public String getNameByFlag(Flag flag);
}
