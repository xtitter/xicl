package ru.icl.dicewars;

import java.util.HashMap;
import java.util.Map;

import ru.icl.dicewars.core.ActivityQueue;
import ru.icl.dicewars.core.GamePlay;
import ru.icl.dicewars.core.SimpleConfigurationImpl;

public class Start {
	public static void main(String[] args) throws Exception {
		final Map<String, Integer> stat = new HashMap<String, Integer>();
		for (int i = 0; i < 20; i++) {
			final GamePlay gamePlay = new GamePlay(
					new SimpleConfigurationImpl());
			final ActivityQueue activityQueue = gamePlay.getActivityQueue();
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					gamePlay.play();
					String name = gamePlay.getWinnerPlayer().getName();
					Integer d = stat.get(name);
					if (d == null) {
						stat.put(name, 1);
					} else {
						stat.put(name, d + 1);
					}
				}
			});
			t.start();
			t.join();
		}
		
		System.out.println();
		for (String name : stat.keySet()) {
			System.out.println("" + name + ": " + stat.get(name));
		}
	}
}
