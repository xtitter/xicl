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
	
	private Map<Flag, PlayerJPanel> playerJPanelMap = new HashMap<Flag, PlayerJPanel>();
	private Flag winner;
	private int winnerCount;
	
	public InfoJPanel() {
		setLayout(null);
	}
	
	public void initPlayers(FlagDistributedActivity flagDistributedActivity) {
		int yoffset = 10;
		for (PlayerJPanel playerPanel : playerJPanelMap.values()){
			this.remove(playerPanel);
		}
		playerJPanelMap.clear();
		for (Flag flag : flagDistributedActivity.getFlags()) {
			PlayerJPanel player = new PlayerJPanel(flag, flagDistributedActivity.getNameByFlag(flag));
			player.setBounds(10, yoffset, WIDTH, HEIGHT);
			playerJPanelMap.put(flag, player);
			add(player);
			yoffset += HEIGHT + 10;
		}
		winner = null;
		winnerCount = 0;
		revalidate();
		repaint();
	}
	
	public void updateReserve(Flag flag, int diceCount) {
		if (playerJPanelMap.containsKey(flag)) {
			playerJPanelMap.get(flag).setReserveCount(diceCount);
			playerJPanelMap.get(flag).repaint();
		}
	}

	public void updateDiceCount(Flag flag, int totalDiceCount) {
		if (playerJPanelMap.containsKey(flag)) {
			playerJPanelMap.get(flag).setTotalDiceCount(totalDiceCount);
			if (winner != null) {
				if (!flag.equals(winner)) {
					if (totalDiceCount > winnerCount) {
						winnerCount = totalDiceCount;
						changeWinnerTo(winner, flag);
						for (PlayerJPanel playerJPanel : playerJPanelMap.values()){
							playerJPanel.setWinnerTotalDiceCount(winnerCount);
							playerJPanel.repaint();
						}
					}	
				} else if (totalDiceCount > winnerCount) {
					winnerCount = totalDiceCount;
					for (PlayerJPanel playerJPanel : playerJPanelMap.values()){
						playerJPanel.setWinnerTotalDiceCount(winnerCount);
						playerJPanel.repaint();
					}
				} else { // check if another player became a winner
					winnerCount = totalDiceCount;
					Flag previousWinner = winner;
					for (PlayerJPanel player : playerJPanelMap.values()) {
						if (!player.getFlag().equals(winner) && player.getTotalDiceCount() > winnerCount) {
							winner = player.getFlag();
							winnerCount = player.getTotalDiceCount(); 
						}
					}
					// winner has been changed
					if (!previousWinner.equals(winner)) {
						changeWinnerTo(previousWinner, winner);
						for (PlayerJPanel playerJPanel : playerJPanelMap.values()){
							playerJPanel.setWinnerTotalDiceCount(winnerCount);
							playerJPanel.repaint();
						}
					}
				}
			} else {
				winner = flag;
				winnerCount = totalDiceCount;
				playerJPanelMap.get(flag).setWinner(true);
			}
			playerJPanelMap.get(flag).repaint();
		}
	}
	
	private void changeWinnerTo(Flag from, Flag to) {
		playerJPanelMap.get(from).setWinner(false);
		playerJPanelMap.get(from).repaint();
		playerJPanelMap.get(to).setWinner(true);
		playerJPanelMap.get(to).repaint();
		winner = to;
	}
	
	public void updateAreaCount(Flag flag, int count) {
		if (playerJPanelMap.containsKey(flag)) {
			playerJPanelMap.get(flag).setAreaCount(count);
			playerJPanelMap.get(flag).repaint();
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
