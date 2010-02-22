package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.gui.component.RoundedBorder;
import ru.icl.dicewars.gui.manager.ImageManager;
import ru.icl.dicewars.gui.util.FlagToColorUtil;

public class PlayerJPanel extends JPanel {

	private static final long serialVersionUID = 8066379108638626622L;

	private Flag flag;
	private Color color;
	private String playerName;
	private int totalDiceCount = 0;

	private int areaCount = 0;
	private int reserveCount = 0;
	private int winnerTotalDiceCount = 0;
	private int rank = 0;
	private boolean winner = false;
		
	private final static int radius = 12; 
	private final static int alpha = 90; 
	
	public PlayerJPanel(Flag flag, String playerName) {
		this.color = FlagToColorUtil.getColorByFlag(flag, alpha);
		this.flag = flag;
		this.playerName = playerName;
		setBorder(new RoundedBorder(this.color, this.color, this.color, radius, 0));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(FlagToColorUtil.getColorByFlag(flag, alpha));
		g.fillRect(radius, radius, getWidth() - radius*2, getHeight() - radius*2);
		
		g.setColor(Color.black);
		g.drawString(playerName, 67, 20);
		g.drawString("Dice count: " + String.valueOf(totalDiceCount), 67, 41);
		g.drawString("Dice per turn: " + String.valueOf(areaCount), 67, 55);
		g.drawString("Reserve: " + String.valueOf(reserveCount), 67, 69);
	}
	
	@Override
	public void paintBorder(Graphics g) {
		super.paintBorder(g);
		
		Image avatar = null;	
		if (winner) {
			g.drawImage(ImageManager.getTrophy(), 147, 2, 48, 48, this);
			avatar = ImageManager.getAvatar(flag, Emotion.HAPPY.value());
		}
		if (winnerTotalDiceCount * 2 / 3 > totalDiceCount){
			avatar = ImageManager.getAvatar(flag, Emotion.CALM.value());
		}
		if (winnerTotalDiceCount * 1 / 3 > totalDiceCount){
			avatar = ImageManager.getAvatar(flag, Emotion.SAD.value());
		}
		if (totalDiceCount == 0){
			avatar = ImageManager.getAvatar(flag, Emotion.EVIL.value());
		}
		if (avatar == null){
			avatar = ImageManager.getAvatar(flag, Emotion.SMILING.value());
		}
		if (avatar != null) g.drawImage(avatar, radius - 12, radius - 4, 64, 64, this);
	}

	public void setAreaCount(int areaCount) {
		this.areaCount = areaCount;
	}

	public int getAreaCount() {
		return areaCount;
	}
	
	public void setReserveCount(int reserveCount) {
		this.reserveCount = reserveCount;
	}
	
	public int getReserveCount() {
		return reserveCount;
	}
	
	public int getTotalDiceCount() {
		return totalDiceCount;
	}
	
	public void setTotalDiceCount(int totalDiceCount) {
		this.totalDiceCount = totalDiceCount;
	}
	
	public Flag getFlag() {
		return flag;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void setWinner(boolean winner) {
		this.winner = winner;
	}
	
	public boolean isWinner() {
		return winner;
	}
	
	public void setWinnerTotalDiceCount(int winnerTotalDiceCount) {
		this.winnerTotalDiceCount = winnerTotalDiceCount;
	}
	
	public int getWinnerTotalDiceCount() {
		return winnerTotalDiceCount;
	}
	
	private static enum Emotion{
		HAPPY(1), SMILING(2), CALM(3), SAD(4), EVIL(5);
		
		int i;
		
		private Emotion(int i){
			this.i = i;
		}
		
		public int value(){
			return i;
		}
	}
}
