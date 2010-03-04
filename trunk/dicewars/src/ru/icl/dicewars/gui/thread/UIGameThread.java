package ru.icl.dicewars.gui.thread;

import ru.icl.dicewars.core.Configuration;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.GamePlayThread;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.FlagChosenActivity;
import ru.icl.dicewars.core.activity.GameEndedActivity;
import ru.icl.dicewars.core.activity.LandUpdatedActivity;
import ru.icl.dicewars.core.activity.PlayersLoadedActivity;
import ru.icl.dicewars.core.activity.SimpleLandUpdatedActivity;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivityImpl;
import ru.icl.dicewars.core.activity.WorldCreatedActivity;
import ru.icl.dicewars.core.activity.WorldInfoUpdatedActivity;
import ru.icl.dicewars.gui.manager.WindowManager;

public class UIGameThread extends Thread {
	
	volatile boolean t = true;
	
	volatile int speed = 1;
	
	Configuration configuration;

	public UIGameThread(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public void setSpeed(int speed) {
		if (speed >= 0){
			synchronized (this) {
				this.notifyAll();
			}
		}
		this.speed = speed;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	private void checkPause() throws InterruptedException{
		if (speed < 0){
			synchronized (this) {
				this.wait();	
			}
		}
	}
	
	@Override
	public void run() {
		GamePlayThread gamePlayThread = new GamePlayThread(configuration);
		gamePlayThread.start();
		try{
			while (t) {
				DiceWarsActivity activity = gamePlayThread.pollFromActivityQueue();
				if (activity instanceof WorldCreatedActivity) {
					FullWorld world = ((WorldCreatedActivity) activity).getFullWorld();
					WindowManager.getInstance().getWorldJPanel().updateWorld(world);
				} if (activity instanceof PlayersLoadedActivity){ 
					PlayersLoadedActivity playersLoadedActivity = (PlayersLoadedActivity) activity;
					WindowManager.getInstance().getInfoJPanel().initPlayers(playersLoadedActivity);
				} else if (activity instanceof FlagChosenActivity){
					FlagChosenActivity flagDistributedActivity = (FlagChosenActivity) activity;
					WindowManager.getInstance().getInfoJPanel().addPlayer(flagDistributedActivity);
					_sleep(500, speed);
				} else if (activity instanceof SimpleLandUpdatedActivity) {
					LandUpdatedActivity landUpdatedActivity = (LandUpdatedActivity) activity;
					WindowManager.getInstance().getWorldJPanel().updateLand(landUpdatedActivity.getFullLand());
				} else if (activity instanceof SimplePlayerAttackActivityImpl) {
					SimplePlayerAttackActivityImpl simplePlayerActivity = ((SimplePlayerAttackActivityImpl) activity);
					WindowManager.getInstance().getWorldJPanel().updateAttackingPlayer(simplePlayerActivity.getFromLandId());
					_sleep(700, speed);
					checkPause();
					WindowManager.getInstance().getWorldJPanel().updateDefendingPlayerLandId(simplePlayerActivity.getToLandId());
					_sleep(250, speed);
					checkPause();
					if (speed == 1){
						WindowManager.getInstance().getWorldJPanel().drawArrow(simplePlayerActivity.getFromLandId(), simplePlayerActivity.getToLandId());
					}
					_sleep(350, speed);
					checkPause();
					WindowManager.getInstance().getWorldJPanel().eraseArrow();
					WindowManager.getInstance().getWorldJPanel().updateAttackingPlayer(0);
					WindowManager.getInstance().getWorldJPanel().updateDefendingPlayerLandId(0);
					_sleep(300, speed);
					checkPause();
				} else if (activity instanceof WorldInfoUpdatedActivity) {
					WorldInfoUpdatedActivity worldInfoUpdatedActivity = (WorldInfoUpdatedActivity)activity;
					WindowManager.getInstance().getInfoJPanel().update(worldInfoUpdatedActivity.getFlag(), worldInfoUpdatedActivity.getTotalDiceCount(), worldInfoUpdatedActivity.getMaxConnectedLandsCount(), worldInfoUpdatedActivity.getDiceCountInReserve());
				} else if (activity instanceof GameEndedActivity){
					WindowManager.getInstance().getMainFrame().notifyThatGameIsEnded();
					break;
				}
				
				if (speed == 1)
					_sleep(100);
	
				if (speed == 2)
					_sleep(10);
	
				checkPause();
			}
		}catch (InterruptedException e) {
		}
		
		while (gamePlayThread.isAlive()){
			gamePlayThread.kill();
			try{
				_sleep(10);
			}catch (InterruptedException e) {
			}
		}
	}
	
	private void _sleep(long time) throws InterruptedException{
		Thread.sleep(time);
	}

	private void _sleep(long time, int speed) {
		try {
			if (speed == 1)
				Thread.sleep(time);
			if (speed == 2)
				Thread.sleep(time / 10);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	public void kill() {
		this.t = false;
		synchronized (this) {
			WindowManager.getInstance().getWorldJPanel().stopDrawArrow();
			this.notifyAll();
		}
	}
}
