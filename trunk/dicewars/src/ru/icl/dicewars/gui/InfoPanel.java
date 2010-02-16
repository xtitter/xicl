package ru.icl.dicewars.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.gui.util.FlagToColorUtil;

public class InfoPanel extends JPanel {

	private static final long serialVersionUID = -222523072240558572L;

	private static final int WIDTH = 170;
	private static final int HEIGHT = 80;
	
	private Map<Flag, PlayerPanel> players = new HashMap<Flag, PlayerPanel>();
	
	public InfoPanel() {
		setLayout(null);
	}
	
	public void addPlayers(List<Flag> flags) {
		int yoffset = 10;
		for (Flag flag : flags) {
			PlayerPanel player = new PlayerPanel(flag);
			player.setBounds(10, yoffset, WIDTH, HEIGHT);
			player.setBorder(BorderFactory.createLineBorder(FlagToColorUtil.getColorByFlag(flag, 165)));
			players.put(flag, player);
			add(player);
			yoffset += HEIGHT + 10;
		}
		revalidate();
	}
	
	public void updateReserve(Flag flag, int diceCount) {
		if (players.containsKey(flag)) {
			players.get(flag).setReserveCount(diceCount);
			players.get(flag).repaint();
		}
	}
	
	public void updateDiceCount(Map<Flag, Integer> diceOverallCount) {
		for (Flag flag : diceOverallCount.keySet()) {
			if (players.containsKey(flag)) {
				players.get(flag).setDiceOverallCount(diceOverallCount.get(flag));
				players.get(flag).repaint();
			}
		}
	}
	
	public void sortPlayers() {
		ArrayList<PlayerPanel> list = new ArrayList<PlayerPanel>();
		list.addAll(players.values());
		Collections.sort(list, new Comparator<PlayerPanel>() {
			@Override
			public int compare(PlayerPanel arg0, PlayerPanel arg1) {
				return Integer.valueOf(arg1.getDiceOverallCount()).compareTo(Integer.valueOf(arg0.getDiceOverallCount()));
			}
		});
		int yoffset = 10;
		for (PlayerPanel player : list) {
			player.setBounds(10, yoffset, WIDTH, HEIGHT);
			yoffset += HEIGHT + 10;
		}
		revalidate();
	}
}
