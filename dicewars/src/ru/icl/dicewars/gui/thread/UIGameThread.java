package ru.icl.dicewars.gui.thread;

import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.GamePlayThread;
import ru.icl.dicewars.core.SimpleConfigurationImpl;
import ru.icl.dicewars.core.activity.DiceCountInReserveChangedActivity;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.FlagDistributedActivity;
import ru.icl.dicewars.core.activity.GameEndedActivity;
import ru.icl.dicewars.core.activity.LandUpdatedActivity;
import ru.icl.dicewars.core.activity.MaxConnectedLandsCountChangedActivity;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivity;
import ru.icl.dicewars.core.activity.TotalDiceCountChangedActivity;
import ru.icl.dicewars.core.activity.WorldCreatedActivity;
import ru.icl.dicewars.gui.LandFactory;
import ru.icl.dicewars.gui.manager.WindowManager;

public class UIGameThread extends Thread {

	boolean t = true;
	
	volatile int speed = 1;
	
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
	
	private void checkPause(){
		if (speed < 0){
			try{
				synchronized (this) {
					this.wait();	
				}
			}catch (InterruptedException e) {
			}
		}
	}
	
	@Override
	public void run() {
		GamePlayThread gamePlayThread = new GamePlayThread(new SimpleConfigurationImpl());
		gamePlayThread.start();
		
		while (t) {
			DiceWarsActivity activity = gamePlayThread.pollFromActivityQueue();
			if (activity instanceof WorldCreatedActivity) {
				FullWorld world = ((WorldCreatedActivity) activity).getFullWorld();
				LandFactory.buildTheWorld(world);
				LandFactory.buildBackground(world);
				WindowManager.getInstance().getWorldJPanel().update(world);
			} else if (activity instanceof FlagDistributedActivity){
				FlagDistributedActivity fda = (FlagDistributedActivity) activity;
				WindowManager.getInstance().getInfoJPanel().initPlayers(fda);
			} else if (activity instanceof LandUpdatedActivity) {
				FullLand land = ((LandUpdatedActivity) activity).getFullLand();
				WindowManager.getInstance().getWorldJPanel().update(land);
			} else if (activity instanceof SimplePlayerAttackActivity) {
				SimplePlayerAttackActivity pa = ((SimplePlayerAttackActivity) activity);
				WindowManager.getInstance().getWorldJPanel().updateAttackingPlayer(pa.getFromLandId());
				WindowManager.getInstance().getWorldJPanel().repaint();
				//if (!alreadyFrozen()) _sleep(700, speed);
				_sleep(700, speed);
				checkPause();
				WindowManager.getInstance().getWorldJPanel().updateDefendingPlayer(pa.getToLandId());
				//Arrow arrow = WindowManager.getManager().getArrow(pa, ArrowType.BEZIER);
				//WindowManager.getManager().getJLayeredPane().add(arrow, JLayeredPane.MODAL_LAYER, 1);
				WindowManager.getInstance().getWorldJPanel().repaint();
				//if (!alreadyFrozen()) _sleep(1000, speed);
				_sleep(1000, speed);
				checkPause();
				//WindowManager.getManager().getJLayeredPane().remove(arrow);
				WindowManager.getInstance().getWorldJPanel().updateAttackingPlayer(0);
				WindowManager.getInstance().getWorldJPanel().updateDefendingPlayer(0);
				WindowManager.getInstance().getWorldJPanel().repaint();
				//if (!alreadyFrozen()) _sleep(300, speed);
				_sleep(300, speed);
				checkPause();
			} else if (activity instanceof DiceCountInReserveChangedActivity) {
				DiceCountInReserveChangedActivity dcr = (DiceCountInReserveChangedActivity)activity;
				WindowManager.getInstance().getInfoJPanel().updateReserve(dcr.getFlag(), dcr.getDiceCount());
			} else if (activity instanceof TotalDiceCountChangedActivity) {
				TotalDiceCountChangedActivity tda = (TotalDiceCountChangedActivity)activity;
				WindowManager.getInstance().getInfoJPanel().updateDiceCount(tda.getFlag(), tda.getTotalDiceCount());
			} else if (activity instanceof MaxConnectedLandsCountChangedActivity) {
				MaxConnectedLandsCountChangedActivity max = (MaxConnectedLandsCountChangedActivity)activity;
				WindowManager.getInstance().getInfoJPanel().updateAreaCount(max.getFlag(), max.getLandsCount());
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
		
		while (gamePlayThread.isAlive()){
			gamePlayThread.kill();
			_sleep(10);
		}
	}
	
	/*private boolean alreadyFrozen() {
		boolean frozen = false;
		while (WindowManager.getInstance().isFrozen()) {
			frozen = true;
			WindowManager.getInstance().fire();
			_sleep(1000);
		}
		return frozen;
	}*/
	
	private void _sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
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
			this.notifyAll();
		}
	}
}
