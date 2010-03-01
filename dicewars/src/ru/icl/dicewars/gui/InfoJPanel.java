package ru.icl.dicewars.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.activity.FlagDistributedActivity;

public final class InfoJPanel extends JPanel {

	private static final long serialVersionUID = -222523072240558572L;

	private static final int WIDTH = 180;
	private static final int HEIGHT = 80;
	
	private Map<Flag, PlayerJPanel> playerJPanelMap = new HashMap<Flag, PlayerJPanel>();
	private Flag winnerFlag;
	private int winnerTotalDiceCount;
	
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
		winnerFlag = null;
		winnerTotalDiceCount = 0;
		revalidate();
		repaint();
	}
	
	public void clearPlayers(){
		for (PlayerJPanel playerPanel : playerJPanelMap.values()){
			this.remove(playerPanel);
		}
		playerJPanelMap.clear();
		winnerFlag = null;
		winnerTotalDiceCount = 0;
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
			if (winnerFlag != null) {
				if (!flag.equals(winnerFlag)) {
					if (totalDiceCount > winnerTotalDiceCount) {
						winnerTotalDiceCount = totalDiceCount;
						changeWinnerTo(winnerFlag, flag);
						for (PlayerJPanel playerJPanel : playerJPanelMap.values()){
							playerJPanel.setWinnerTotalDiceCount(winnerTotalDiceCount);
							playerJPanel.repaint();
						}
					}	
				} else if (totalDiceCount > winnerTotalDiceCount) {
					winnerTotalDiceCount = totalDiceCount;
					for (PlayerJPanel playerJPanel : playerJPanelMap.values()){
						playerJPanel.setWinnerTotalDiceCount(winnerTotalDiceCount);
						playerJPanel.repaint();
					}
				} else { // check if another player became a winner
					winnerTotalDiceCount = totalDiceCount;
					Flag previousWinner = winnerFlag;
					for (PlayerJPanel player : playerJPanelMap.values()) {
						if (!player.getFlag().equals(winnerFlag) && player.getTotalDiceCount() > winnerTotalDiceCount) {
							winnerFlag = player.getFlag();
							winnerTotalDiceCount = player.getTotalDiceCount(); 
						}
					}
					// winner has been changed
					if (!previousWinner.equals(winnerFlag)) {
						changeWinnerTo(previousWinner, winnerFlag);
						for (PlayerJPanel playerJPanel : playerJPanelMap.values()){
							playerJPanel.setWinnerTotalDiceCount(winnerTotalDiceCount);
							playerJPanel.repaint();
						}
					}
				}
			} else {
				winnerFlag = flag;
				winnerTotalDiceCount = totalDiceCount;
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
		winnerFlag = to;
	}
	
	public void updateAreaCount(Flag flag, int count) {
		if (playerJPanelMap.containsKey(flag)) {
			playerJPanelMap.get(flag).setAreaCount(count);
			playerJPanelMap.get(flag).repaint();
		}
	}
}
