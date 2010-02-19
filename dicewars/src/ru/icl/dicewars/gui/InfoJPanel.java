package ru.icl.dicewars.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.activity.FlagDistributedActivity;

public class InfoJPanel extends JPanel {

	private static final long serialVersionUID = -222523072240558572L;

	private static final int WIDTH = 180;
	private static final int HEIGHT = 80;
	
	private Map<Flag, PlayerJPanel> players = new HashMap<Flag, PlayerJPanel>();
	private Flag winner;
	private int winnerCount;
	
	public InfoJPanel() {
		setLayout(null);
	}
	
	public void initPlayers(FlagDistributedActivity fda) {
		int yoffset = 10;
		for (PlayerJPanel playerPanel : players.values()){
			this.remove(playerPanel);
		}
		players.clear();
		for (Flag flag : fda.getFlags()) {
			PlayerJPanel player = new PlayerJPanel(flag, fda.getNameByFlag(flag));
			player.setBounds(10, yoffset, WIDTH, HEIGHT);
			players.put(flag, player);
			add(player);
			yoffset += HEIGHT + 10;
		}
		revalidate();
		repaint();
	}
	
	public void updateReserve(Flag flag, int diceCount) {
		if (players.containsKey(flag)) {
			players.get(flag).setReserveCount(diceCount);
			players.get(flag).repaint();
		}
	}

	public void updateDiceCount(Flag flag, int count) {
		if (players.containsKey(flag)) {
			players.get(flag).setDiceOverallCount(count);
			if (winner != null) {
				if (!flag.equals(winner)) {
					if (count > winnerCount) {
						winnerCount = count;
						changeWinnerTo(winner, flag);
					}	
				} else if (count > winnerCount) {
					winnerCount = count;
				} else { // check if another player became a winner
					winnerCount = count;
					Flag previousWinner = winner;
					for (PlayerJPanel player : players.values()) {
						if (!player.getFlag().equals(winner) && player.getDiceOverallCount() > winnerCount) {
							winner = player.getFlag();
							winnerCount = player.getDiceOverallCount(); 
						}
					}
					// winner has been changed
					if (!previousWinner.equals(winner)) {
						changeWinnerTo(previousWinner, winner);
					}
				}
			} else {
				winner = flag;
				winnerCount = count;
				players.get(flag).setWinner(true);
			}
			players.get(flag).repaint();
		}
	}
	
	private void changeWinnerTo(Flag from, Flag to) {
		players.get(from).setWinner(false);
		players.get(from).repaint();
		players.get(to).setWinner(true);
		players.get(to).repaint();
		winner = to;
	}
	
	public void updateAreaCount(Flag flag, int count) {
		if (players.containsKey(flag)) {
			players.get(flag).setAreaCount(count);
			players.get(flag).repaint();
		}
	}
	
	/*public void updateDiceCount(Map<Flag, Integer> diceOverallCount) {
		for (Flag flag : diceOverallCount.keySet()) {
			if (players.containsKey(flag)) {
				players.get(flag).setDiceOverallCount(diceOverallCount.get(flag));
				players.get(flag).repaint();
			}
		}
		
		/*Flag toremove = null;
		for (PlayerJPanel player : players.values()) {
			if (!diceOverallCount.containsKey(player.getFlag())) {
				toremove = player.getFlag();
				break;
			}
		}
		if (toremove != null) {
			PlayerJPanel player = players.remove(toremove);
			player.setBorder(BorderFactory.createEtchedBorder( FlagToColorUtil.getColorByFlag(player.getFlag(), 100), Color.gray));
			player.setOutOfTheGame(true);
			player.setDiceOverallCount(0);
			player.setRank(diceOverallCount.size());
			player.repaint();
		}*/
	//}
	
	/*public void sortPlayers() {
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
	}*/
}
