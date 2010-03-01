package ru.icl.dicewars.core;

import java.util.LinkedList;
import java.util.Queue;

import ru.icl.dicewars.core.activity.DiceCountInReserveChangedActivity;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.FlagDistributedActivity;
import ru.icl.dicewars.core.activity.GameEndedActivity;
import ru.icl.dicewars.core.activity.LandUpdatedActivity;
import ru.icl.dicewars.core.activity.MaxConnectedLandsCountChangedActivity;
import ru.icl.dicewars.core.activity.PlayerAttackActivity;
import ru.icl.dicewars.core.activity.TotalDiceCountChangedActivity;
import ru.icl.dicewars.core.activity.WorldCreatedActivity;

public class ActivityQueueImpl implements ActivityQueue {
	private Queue<DiceWarsActivity> queue = new LinkedList<DiceWarsActivity>();

	boolean isWorldCreated = false;
	boolean isFlagDistributed = false;
	boolean isGameEnded = false;

	@Override
	public synchronized DiceWarsActivity poll() {
		return queue.poll();
	}

	@Override
	public synchronized void add(DiceWarsActivity e) {
		if (e instanceof LandUpdatedActivity
				&& (!isFlagDistributed || !isWorldCreated || isGameEnded)) {
			throw new IllegalStateException();
		}

		if (e instanceof PlayerAttackActivity
				&& (!isFlagDistributed || !isWorldCreated || isGameEnded)) {
			throw new IllegalStateException();
		}

		if (e instanceof GameEndedActivity
				&& (!isFlagDistributed || !isWorldCreated || isGameEnded)) {
			throw new IllegalStateException();
		}

		if (e instanceof TotalDiceCountChangedActivity
				&& (!isFlagDistributed || !isWorldCreated || isGameEnded)) {
			throw new IllegalStateException();
		}

		if (e instanceof DiceCountInReserveChangedActivity
				&& (!isFlagDistributed || !isWorldCreated || isGameEnded)) {
			throw new IllegalStateException();
		}

		if (e instanceof MaxConnectedLandsCountChangedActivity
				&& (!isFlagDistributed || !isWorldCreated || isGameEnded)) {
			throw new IllegalStateException();
		}

		queue.add(e);
		
		if (e instanceof FlagDistributedActivity) {
			isFlagDistributed = true;
		}

		if (e instanceof WorldCreatedActivity) {
			isWorldCreated = true;
		}
		
		if (e instanceof GameEndedActivity){
			isGameEnded = true;
		}
	}

	@Override
	public synchronized void clear() {
		queue.clear();
		isWorldCreated = false;
		isFlagDistributed = false;
		isGameEnded = false;
	}

	@Override
	public synchronized int size() {
		return queue.size();
	}
}
