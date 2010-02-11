package ru.icl.dicewars.gui.thread;

import javax.swing.JLayeredPane;

import ru.icl.dicewars.core.ActivityQueue;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.GamePlay;
import ru.icl.dicewars.core.SimpleConfigurationImpl;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.LandUpdatedActivity;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivity;
import ru.icl.dicewars.core.activity.WorldCreatedActivity;
import ru.icl.dicewars.gui.arrow.Arrow;
import ru.icl.dicewars.gui.arrow.ArrowFactory.ArrowType;
import ru.icl.dicewars.gui.manager.WindowManager;

public class UIGameThread implements Runnable {

	@Override
	public void run() {
		final GamePlay gamePlay = new GamePlay(new SimpleConfigurationImpl());
		final ActivityQueue activityQueue = gamePlay.getActivityQueue();
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				gamePlay.play();
			}
		}).start();
		
		while (true) {
			DiceWarsActivity activity = activityQueue.poll();
			
			
			
			if (activity instanceof WorldCreatedActivity) {
				FullWorld world = ((WorldCreatedActivity) activity).getFullWorld();
				WindowManager.getManager().getWorld().update(world);
			} else if (activity instanceof LandUpdatedActivity) {
				FullLand land = ((LandUpdatedActivity) activity).getFullLand();
				WindowManager.getManager().getWorld().update(land);
			} else if (activity instanceof SimplePlayerAttackActivity) {
				SimplePlayerAttackActivity pa = ((SimplePlayerAttackActivity) activity);
				WindowManager.getManager().getWorld().updateAttackingPlayer(pa.getFromLandId());
				if (!alreadyFrozen()) sleep(700);
				WindowManager.getManager().getWorld().updateDefendingPlayer(pa.getToLandId());
				//Arrow arrow = WindowManager.getManager().getArrow(pa, ArrowType.BEZIER);
				//WindowManager.getManager().getJLayeredPane().add(arrow, JLayeredPane.MODAL_LAYER, 1);
				WindowManager.getManager().getJLayeredPane().repaint();
				if (!alreadyFrozen()) sleep(1000);
				//WindowManager.getManager().getJLayeredPane().remove(arrow);
				WindowManager.getManager().getWorld().updateAttackingPlayer(0);
				WindowManager.getManager().getWorld().updateDefendingPlayer(0);
				WindowManager.getManager().getJLayeredPane().repaint();
				if (!alreadyFrozen()) sleep(300);
			}
			
			sleep(10);
		}
	}
	
	private boolean alreadyFrozen() {
		boolean frozen = false;
		while (WindowManager.getManager().isFrozen()) {
			frozen = true;
			WindowManager.getManager().fire();
			sleep(1000);
		}
		return frozen;
	}
	
	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

}
