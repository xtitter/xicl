package ru.icl.dicewars.core;

import java.util.LinkedList;
import java.util.Queue;

import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.FlagChosenActivity;
import ru.icl.dicewars.core.activity.GameEndedActivity;
import ru.icl.dicewars.core.activity.LandUpdatedActivity;
import ru.icl.dicewars.core.activity.PlayerAttackActivity;
import ru.icl.dicewars.core.activity.PlayersLoadedActivity;
import ru.icl.dicewars.core.activity.WorldCreatedActivity;

public class ActivityQueueImpl implements ActivityQueue {
	private Queue<DiceWarsActivity> queue = new LinkedList<DiceWarsActivity>();

	boolean isPlayerLoaded = false;
	boolean isWorldCreated = false;
	boolean isFlagDistributed = false;
	boolean isGameEnded = false;
	int playerCount = 0;

	@Override
	public synchronized DiceWarsActivity poll() {
		return queue.poll();
	}

	@Override
	public synchronized void add(DiceWarsActivity e) {
		if (isGameEnded) throw new IllegalStateException();
		
		if (e instanceof PlayersLoadedActivity
				&& isPlayerLoaded) {
			throw new IllegalStateException();
		}
		
		if (e instanceof WorldCreatedActivity
				&& (!isPlayerLoaded || isWorldCreated)) {
			throw new IllegalStateException();
		}
		
		if (e instanceof FlagChosenActivity
				&& (!isWorldCreated || playerCount <= 0)) {
			throw new IllegalStateException();
		}

		if (e instanceof LandUpdatedActivity
				&& !isFlagDistributed) {
			throw new IllegalStateException();
		}

		if (e instanceof PlayerAttackActivity
				&& !isFlagDistributed) {
			throw new IllegalStateException();
		}

		if (e instanceof GameEndedActivity
				&& !isFlagDistributed) {
			throw new IllegalStateException();
		}

		queue.add(e);
		
		if (e instanceof PlayersLoadedActivity) {
			isPlayerLoaded = true;
			PlayersLoadedActivity playersLoadedActivity = (PlayersLoadedActivity) e;
			playerCount = playersLoadedActivity.getPlayerNames().size();
		}
		
		if (e instanceof WorldCreatedActivity) {
			isWorldCreated = true;
		}

		if (e instanceof FlagChosenActivity) {
			playerCount--;
			if (playerCount == 0) isFlagDistributed = true;
		}

		if (e instanceof GameEndedActivity){
			isGameEnded = true;
		}
	}

	@Override
	public synchronized void clear() {
		queue.clear();
		isPlayerLoaded = false;
		isWorldCreated = false;
		isFlagDistributed = false;
		isGameEnded = false;
		playerCount = 0;
	}

	@Override
	public synchronized int size() {
		return queue.size();
	}
}
