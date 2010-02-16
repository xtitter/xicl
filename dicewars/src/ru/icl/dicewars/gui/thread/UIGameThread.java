package ru.icl.dicewars.gui.thread;

import ru.icl.dicewars.core.ActivityQueue;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.GamePlayThread;
import ru.icl.dicewars.core.SimpleConfigurationImpl;
import ru.icl.dicewars.core.activity.DiceCountInReserveChangedActivity;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.LandUpdatedActivity;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivity;
import ru.icl.dicewars.core.activity.WorldCreatedActivity;
import ru.icl.dicewars.gui.manager.WindowManager;

public class UIGameThread extends Thread {

	boolean t = true;
	
	@Override
	public void run() {
		GamePlayThread gamePlayThread = new GamePlayThread(new SimpleConfigurationImpl());
		
		final ActivityQueue activityQueue = gamePlayThread.getActivityQueue();
		gamePlayThread.start();
		
		while (t) {
			DiceWarsActivity activity = activityQueue.poll();
			
			if (activity instanceof WorldCreatedActivity) {
				FullWorld world = ((WorldCreatedActivity) activity).getFullWorld();
				WindowManager.getManager().getWorld().update(world);
				WindowManager.getManager().getInfoPanel().addPlayers(world.getFlags());
			} else if (activity instanceof LandUpdatedActivity) {
				FullLand land = ((LandUpdatedActivity) activity).getFullLand();
				WindowManager.getManager().getWorld().update(land);
			} else if (activity instanceof SimplePlayerAttackActivity) {
				SimplePlayerAttackActivity pa = ((SimplePlayerAttackActivity) activity);
				WindowManager.getManager().getWorld().updateAttackingPlayer(pa.getFromLandId());
				WindowManager.getManager().getJLayeredPane().repaint();
				if (!alreadyFrozen()) _sleep(700);
				WindowManager.getManager().getWorld().updateDefendingPlayer(pa.getToLandId());
				//Arrow arrow = WindowManager.getManager().getArrow(pa, ArrowType.BEZIER);
				//WindowManager.getManager().getJLayeredPane().add(arrow, JLayeredPane.MODAL_LAYER, 1);
				WindowManager.getManager().getJLayeredPane().repaint();
				if (!alreadyFrozen()) _sleep(1000);
				//WindowManager.getManager().getJLayeredPane().remove(arrow);
				WindowManager.getManager().getWorld().updateAttackingPlayer(0);
				WindowManager.getManager().getWorld().updateDefendingPlayer(0);
				WindowManager.getManager().getJLayeredPane().repaint();
				if (!alreadyFrozen()) _sleep(300);
			} else if (activity instanceof DiceCountInReserveChangedActivity) {
				DiceCountInReserveChangedActivity dcr = (DiceCountInReserveChangedActivity)activity;
				WindowManager.getManager().getInfoPanel().updateReserve(dcr.getFlag(), dcr.getDiceCount());
			}
			
			_sleep(10);
		}
		
		while (gamePlayThread.isAlive()){
			gamePlayThread.kill();
			_sleep(10);
		}
	}
	
	private boolean alreadyFrozen() {
		boolean frozen = false;
		while (WindowManager.getManager().isFrozen()) {
			frozen = true;
			WindowManager.getManager().fire();
			_sleep(1000);
		}
		return frozen;
	}
	
	private void _sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	public void kill() {
		this.t = false;
	}
}
