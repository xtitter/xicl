package ru.icl.dicewars.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.activity.FlagChosenActivity;
import ru.icl.dicewars.core.activity.PlayersLoadedActivity;

public final class InfoJPanel extends JPanel {

	private static final long serialVersionUID = -222523072240558572L;

	private static final int WIDTH = 180;
	private static final int HEIGHT = 80;
	
	private Map<Flag, PlayerJPanel> flagToPlayerJPanelMap = new HashMap<Flag, PlayerJPanel>();
	private Map<Integer, PlayerJPanel> positionToplayerJPanelMap = new HashMap<Integer, PlayerJPanel>();
	private Flag winnerFlag;
	private int winnerTotalDiceCount;
	
	public InfoJPanel() {
		setLayout(null);
	}
	
	public void initPlayers(PlayersLoadedActivity playersLoadedActivity) {
		int yoffset = 10;
		for (PlayerJPanel playerPanel : positionToplayerJPanelMap.values()){
			this.remove(playerPanel);
		}
		flagToPlayerJPanelMap.clear();
		positionToplayerJPanelMap.clear();
		
		int i = 0;
		for (String playerName : playersLoadedActivity.getPlayerNames()) {
			PlayerJPanel player = new PlayerJPanel(playerName);
			player.setBounds(10, yoffset, WIDTH, HEIGHT);
			add(player);
			positionToplayerJPanelMap.put(i++, player);
			yoffset += HEIGHT + 10;
		}
		winnerFlag = null;
		winnerTotalDiceCount = 0;
		revalidate();
		repaint();
	}
	
	public void addPlayer(FlagChosenActivity flagDistributedActivity){
		PlayerJPanel playerJPanel = positionToplayerJPanelMap.get(flagDistributedActivity.getPosition());
		if (playerJPanel == null) throw new IllegalStateException();
		flagToPlayerJPanelMap.put(flagDistributedActivity.getFlag(), playerJPanel);
		playerJPanel.setFlag(flagDistributedActivity.getFlag());
		playerJPanel.repaint();
	}
	
	public void clearPlayers(){
		for (PlayerJPanel playerPanel : flagToPlayerJPanelMap.values()){
			this.remove(playerPanel);
		}
		flagToPlayerJPanelMap.clear();
		positionToplayerJPanelMap.clear();
		winnerFlag = null;
		winnerTotalDiceCount = 0;
		revalidate();
		repaint();
	}
	
	public void update(Flag flag, int totalDiceCount, int maxConnectedLandsCount, int diceCountInReserve) {
		if (flagToPlayerJPanelMap.containsKey(flag)) {
			flagToPlayerJPanelMap.get(flag).setDiceCountInReserve(diceCountInReserve);
		}
		
		if (flagToPlayerJPanelMap.containsKey(flag)) {
			flagToPlayerJPanelMap.get(flag).setDicePerTurnCount(maxConnectedLandsCount);
		}

		if (flagToPlayerJPanelMap.containsKey(flag)) {
			flagToPlayerJPanelMap.get(flag).setTotalDiceCount(totalDiceCount);
			if (winnerFlag != null) {
				if (!flag.equals(winnerFlag)) {
					if (totalDiceCount > winnerTotalDiceCount) {
						winnerTotalDiceCount = totalDiceCount;
						changeWinnerTo(winnerFlag, flag);
						for (PlayerJPanel playerJPanel : flagToPlayerJPanelMap.values()){
							playerJPanel.setWinnerTotalDiceCount(winnerTotalDiceCount);
							playerJPanel.repaint();
						}
					}	
				} else if (totalDiceCount > winnerTotalDiceCount) {
					winnerTotalDiceCount = totalDiceCount;
					for (PlayerJPanel playerJPanel : flagToPlayerJPanelMap.values()){
						playerJPanel.setWinnerTotalDiceCount(winnerTotalDiceCount);
						playerJPanel.repaint();
					}
				} else { // check if another player became a winner
					winnerTotalDiceCount = totalDiceCount;
					Flag previousWinner = winnerFlag;
					for (PlayerJPanel player : flagToPlayerJPanelMap.values()) {
						if (!player.getFlag().equals(winnerFlag) && player.getTotalDiceCount() > winnerTotalDiceCount) {
							winnerFlag = player.getFlag();
							winnerTotalDiceCount = player.getTotalDiceCount(); 
						}
					}
					// winner has been changed
					if (!previousWinner.equals(winnerFlag)) {
						changeWinnerTo(previousWinner, winnerFlag);
						for (PlayerJPanel playerJPanel : flagToPlayerJPanelMap.values()){
							playerJPanel.setWinnerTotalDiceCount(winnerTotalDiceCount);
							playerJPanel.repaint();
						}
					}
				}
			} else {
				winnerFlag = flag;
				winnerTotalDiceCount = totalDiceCount;
				flagToPlayerJPanelMap.get(flag).setWinner(true);
			}
			flagToPlayerJPanelMap.get(flag).repaint();
		}
	}
	
	private void changeWinnerTo(Flag from, Flag to) {
		flagToPlayerJPanelMap.get(from).setWinner(false);
		flagToPlayerJPanelMap.get(from).repaint();
		flagToPlayerJPanelMap.get(to).setWinner(true);
		flagToPlayerJPanelMap.get(to).repaint();
		winnerFlag = to;
	}
}
