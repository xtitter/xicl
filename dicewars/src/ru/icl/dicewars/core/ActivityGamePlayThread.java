package ru.icl.dicewars.core;

import java.util.ArrayList;
import java.util.List;

import ru.icl.dicewars.client.Attack;
import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.client.Player;
import ru.icl.dicewars.client.World;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.SimpleFlagChosenActivityImpl;
import ru.icl.dicewars.core.activity.SimpleGameEndedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleLandUpdatedActivity;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivityImpl;
import ru.icl.dicewars.core.activity.SimplePlayersLoadedActivityImpl;
import ru.icl.dicewars.core.activity.SimpleWorldCreatedActivityImpl;

public class ActivityGamePlayThread extends GamePlayThread {

	private final static int MAX_ACTIVITY_COUNT_IN_QUEUE = 50;
	
	private ActivityQueue activityQueue = new ActivityQueueImpl();

	private boolean useActivityQueue = true;
	
	public ActivityGamePlayThread(Configuration configuration) {
		super(configuration);
	}

	public boolean isUseActivityQueue() {
		return useActivityQueue;
	}

	public void setUseActivityQueue(boolean useActivityQueue) {
		this.useActivityQueue = useActivityQueue;
	}

	public void kill() {
		super.kill();
		synchronized (this) {
			this.notifyAll();
		}
	}

	public DiceWarsActivity pollFromActivityQueue() {
		DiceWarsActivity activity = activityQueue.poll();
		synchronized (this) {
			this.notify();
		}
		return activity;
	}

	protected void addToActivityQueue(DiceWarsActivity activity) {
		if (!useActivityQueue)
			return;
		while (activityQueue.size() > MAX_ACTIVITY_COUNT_IN_QUEUE && t) {
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				kill();
			}
		}
		activityQueue.add(activity);
	}

	@Override
	void landUpdatedFired(FullLand fullLand) {
		super.landUpdatedFired(fullLand);
		addToActivityQueue(new SimpleLandUpdatedActivity(fullLand));
	}
	
	@Override
	Player[] instantiatePlayers() {
		Player[] players = super.instantiatePlayers();
		List<String> playerNames = new ArrayList<String>();
		for (int i = 0; i < players.length; i++) {
			//TODO* this is a bug
			playerNames.add(players[i].getName());
		}
		addToActivityQueue(new SimplePlayersLoadedActivityImpl(playerNames));
		return players;
	}

	@Override
	FullWorld getInitialWorld() {
		FullWorld world = super.getInitialWorld();
		addToActivityQueue(new SimpleWorldCreatedActivityImpl(new FullWorldImpl(world)));
		return world;
	}
	
	@Override
	void afterPlay() {
		super.afterPlay();
		if (t)
			addToActivityQueue(new SimpleGameEndedActivityImpl());
	}
	
	@Override
	void attackFired(World world, Attack attack, boolean isWin) {
		super.attackFired(world, attack, isWin);
		addToActivityQueue(new SimplePlayerAttackActivityImpl(attack, isWin));
	}
	
	@Override
	void flagChosenFired(Flag flag, int playerPosition) {
		super.flagChosenFired(flag, playerPosition);
		addToActivityQueue(new SimpleFlagChosenActivityImpl(flag, playerPosition));
	}
}
