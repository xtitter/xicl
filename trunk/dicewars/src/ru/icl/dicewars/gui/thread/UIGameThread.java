package ru.icl.dicewars.gui.thread;

import ru.icl.dicewars.core.ActivityQueueStorage;
import ru.icl.dicewars.core.Configuration;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.GamePlayThread;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.FlagChosenActivity;
import ru.icl.dicewars.core.activity.GameEndedActivity;
import ru.icl.dicewars.core.activity.LandUpdatedActivity;
import ru.icl.dicewars.core.activity.PlayerGameOverActivity;
import ru.icl.dicewars.core.activity.PlayersLoadedActivity;
import ru.icl.dicewars.core.activity.SimpleLandUpdatedActivity;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivityImpl;
import ru.icl.dicewars.core.activity.TurnNumberChangedActivity;
import ru.icl.dicewars.core.activity.WorldCreatedActivity;
import ru.icl.dicewars.core.activity.WorldInfoUpdatedActivity;
import ru.icl.dicewars.gui.manager.WindowManager;

public class UIGameThread extends Thread {
	public static final int NORMAL_SPEED = 1;
	public static final int FORWARD_SPEED = 2;
	public static final int FAST_FORWARD_SPEED = 0;
	public static final int PAUSE_SPEED = -1;
	
	volatile boolean t = true;
	
	volatile int speed = 1;
	//volatile boolean freeze = false;
	//final Object freezeFlag = new Object();
	
	Configuration configuration = null;
	ActivityQueueStorage activityQueueStorage = null;

	public UIGameThread(Configuration configuration) {
		if (configuration == null) throw new IllegalArgumentException();
		this.configuration = configuration;
	}
	
	public UIGameThread(ActivityQueueStorage activityQueueStorage) {
		if (activityQueueStorage == null) throw new IllegalArgumentException();
		this.activityQueueStorage = activityQueueStorage;
	}
	
	public void setSpeed(int speed) {
		if (speed != PAUSE_SPEED){
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
		if (speed == PAUSE_SPEED){
			synchronized (this) {
				this.wait();	
			}
		}
	}
	
	@Override
	public void run() {
		GamePlayThread gamePlayThread = null;
		if (activityQueueStorage != null){
			activityQueueStorage.load();
		}else{
			gamePlayThread = new GamePlayThread(configuration);
			gamePlayThread.start();
		}
		try{
			while (t) {
				DiceWarsActivity activity = null;
				if (activityQueueStorage != null)
					activity = activityQueueStorage.pollFromActivityQueue();
				if (gamePlayThread != null)
					activity = gamePlayThread.pollFromActivityQueue();
				if (activity instanceof WorldCreatedActivity) {
					FullWorld world = ((WorldCreatedActivity) activity).getFullWorld();
					WindowManager.getInstance().getWorldJPanel().updateWorld(world);
					WindowManager.getInstance().getBottomInfoJPanel().init();
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
					if (speed == NORMAL_SPEED || speed == FORWARD_SPEED){
						WindowManager.getInstance().getBottomInfoJPanel().updateDices(simplePlayerActivity.getPlayerFlag(), simplePlayerActivity.getOpponentFlag(), simplePlayerActivity.getPlayerDices(), simplePlayerActivity.getOpponentDices());
					}
					WindowManager.getInstance().getWorldJPanel().updateAttackingPlayer(simplePlayerActivity.getFromLandId());
					_sleep(700, speed);
					checkPause();
					WindowManager.getInstance().getWorldJPanel().updateDefendingPlayerLandId(simplePlayerActivity.getToLandId());
					_sleep(250, speed);
					checkPause();
					if (speed == 1){
						WindowManager.getInstance().getWorldJPanel().drawArrow(simplePlayerActivity.getFromLandId(), simplePlayerActivity.getToLandId());
					}
					_sleep(500, speed);
					checkPause();
					WindowManager.getInstance().getWorldJPanel().eraseArrow();
					WindowManager.getInstance().getWorldJPanel().updateAttackingPlayer(0);
					WindowManager.getInstance().getWorldJPanel().updateDefendingPlayerLandId(0);
					_sleep(300, speed);
					checkPause();
					WindowManager.getInstance().getBottomInfoJPanel().updateDices(null, null, null, null);
				} else if (activity instanceof WorldInfoUpdatedActivity) {
					WorldInfoUpdatedActivity worldInfoUpdatedActivity = (WorldInfoUpdatedActivity)activity;
					WindowManager.getInstance().getInfoJPanel().updatePlayerInfo(worldInfoUpdatedActivity.getFlag(), worldInfoUpdatedActivity.getTotalDiceCount(), worldInfoUpdatedActivity.getMaxConnectedLandsCount(), worldInfoUpdatedActivity.getDiceCountInReserve());
				} else if (activity instanceof PlayerGameOverActivity){
					PlayerGameOverActivity playerLostActivity = (PlayerGameOverActivity) activity;
					WindowManager.getInstance().getInfoJPanel().updatePlayerPlace(playerLostActivity.getFlag(), playerLostActivity.getPlace());
				} else if (activity instanceof TurnNumberChangedActivity){
					TurnNumberChangedActivity turnNumberChangedActivity = (TurnNumberChangedActivity) activity;
					int turnNumber = turnNumberChangedActivity.getTurnNumber();
					WindowManager.getInstance().getBottomInfoJPanel().updateTurnNumber(turnNumber);
				} else if (activity instanceof GameEndedActivity){
					if (gamePlayThread != null){
						WindowManager.getInstance().getMainFrame().notifyThatGameIsEnded();
						break;
					}else{
						if (activityQueueStorage != null){
							if (activityQueueStorage.hasNext()){
								speed = PAUSE_SPEED;
								WindowManager.getInstance().getMainFrame().pauseSelect();
							}else{
								WindowManager.getInstance().getMainFrame().notifyThatGameIsEnded();
								break;
							}
						}
					}
				} 				
				if (speed == NORMAL_SPEED)
					_sleep(100);
	
				if (speed == FORWARD_SPEED)
					_sleep(10);
	
				checkPause();
			}
		}catch (InterruptedException e) {
		}
		
		while (gamePlayThread != null && gamePlayThread.isAlive()){
			gamePlayThread.kill();
			try{
				_sleep(10);
			}catch (InterruptedException e) {
			}
		}
	}
	
	private void _sleep(long time) throws InterruptedException{
		/*while (freeze){
			freeze = false;
			synchronized (freezeFlag) {
				freezeFlag.wait(1000);
			}
		}*/
		Thread.sleep(time);
	}

	private void _sleep(long time, int speed) throws InterruptedException{
		/*while (freeze){
			freeze = false;
			synchronized (freezeFlag) {
				freezeFlag.wait(1000);
			}
		}*/
		if (speed == NORMAL_SPEED)
			Thread.sleep(time);
		if (speed == FORWARD_SPEED)
			Thread.sleep(time / 10);
	}

	public void kill() {
		this.t = false;
		synchronized (this) {
			WindowManager.getInstance().getWorldJPanel().stopDrawArrow();
			this.notifyAll();
		}
	}

	/*public void freeze() {
		freeze = true;
	}*/
}
