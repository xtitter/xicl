package ru.icl.dicewars.gui.thread;

import ru.icl.dicewars.core.ActivityQueue;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.GamePlay;
import ru.icl.dicewars.core.SimpleConfigurationImpl;
import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.LandUpdatedActivity;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivity;
import ru.icl.dicewars.core.activity.WorldCreatedActivity;
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
				FullWorld world = ((WorldCreatedActivity)activity).getFullWorld();
				WindowManager.getManager().getWorld().update(world);
			} else if (activity instanceof LandUpdatedActivity) {
				FullLand land = ((LandUpdatedActivity)activity).getFullLand();
				WindowManager.getManager().getWorld().update(land);
			} else if (activity instanceof SimplePlayerAttackActivity) {
				SimplePlayerAttackActivity pl = ((SimplePlayerAttackActivity)activity);
				//System.out.println(pl.getFromLandId() + ":" + pl.getToLandId());
			}
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

}
