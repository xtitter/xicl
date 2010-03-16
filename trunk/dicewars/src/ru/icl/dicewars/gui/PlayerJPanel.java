package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.gui.component.RoundedBorder;
import ru.icl.dicewars.gui.manager.ImageManager;
import ru.icl.dicewars.gui.util.FlagToColorUtil;

public final class PlayerJPanel extends JPanel {

	private final static long serialVersionUID = 8066379108638626622L;
	
	private final static Font DICE_PER_TURN_COUNT_FONT = new Font(Font.SANS_SERIF, Font.ITALIC | Font.BOLD, 16);
	private final static Font DICE_IN_RESERVE_COUNT_FONT = new Font(Font.SANS_SERIF, Font.ITALIC | Font.BOLD, 16);
	private final static Font PLAYER_NAME_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 12);

	private final static int radius = 12; 
	private final static int alpha = 90; 

	private Flag flag;
	private final String playerName;
	private int totalDiceCount = -1;

	private int dicePerTurnCount = 0;
	private int diceCountInReserve = 0;
	private int winnerTotalDiceCount = 0;
	private boolean winner = false;
		
	public PlayerJPanel(String playerName) {
		this.playerName = playerName;
		setBorder(new RoundedBorder(Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, radius, 0));
	}
	
	public PlayerJPanel(Flag flag, String playerName) {
		this.flag = flag;
		this.playerName = playerName;
		Color color = FlagToColorUtil.getColorByFlag(flag, alpha);
		setBorder(new RoundedBorder(color, color, color, radius, 0));
	}
	
	public void setFlag(Flag flag) {
		this.flag = flag;
		Color color = FlagToColorUtil.getColorByFlag(flag, alpha);
		setBorder(new RoundedBorder(color, color, color, radius, 0));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (flag != null){
			g.setColor(FlagToColorUtil.getColorByFlag(flag, alpha));
		}else{
			g.setColor(Color.LIGHT_GRAY);
		}
		g.fillRect(radius, radius, getWidth() - radius*2, getHeight() - radius*2);
	}
	
	@Override
	public void paintBorder(Graphics g) {
		super.paintBorder(g);
		if (flag != null){
			Image avatar = null;	
			if (winner) {
				g.drawImage(ImageManager.getTrophyImage(), 151, 9, this);
				avatar = ImageManager.getAvatar(flag, Emotion.HAPPY.value());
			}
			if (winnerTotalDiceCount / 2 > totalDiceCount){
				avatar = ImageManager.getAvatar(flag, Emotion.CALM.value());
			}
			if (winnerTotalDiceCount / 4 > totalDiceCount){
				avatar = ImageManager.getAvatar(flag, Emotion.EVIL.value());
			}
			if (dicePerTurnCount == 0){
				avatar = ImageManager.getAvatar(flag, Emotion.SAD.value());
			}
			if (avatar == null || totalDiceCount == -1){
				avatar = ImageManager.getAvatar(flag, Emotion.SMILING.value());
			}
			if (avatar != null) g.drawImage(avatar, radius - 9, radius - 8, this);
			
			Image dicePerTurnCountImage = ImageManager.getDicePerTurnCountImage(flag);
			if (dicePerTurnCountImage != null)
				g.drawImage(dicePerTurnCountImage, 55, 32, this);
			
			Image diceCountInReserveImage = ImageManager.getDiceCountInReserveImage(flag);
			if (diceCountInReserveImage != null)
				g.drawImage(diceCountInReserveImage, 105, 32, this);
		}
		
		g.setColor(Color.black);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font oldFont = g2d.getFont();
		
		g2d.setFont(PLAYER_NAME_FONT);
		if (playerName != null){
			g2d.drawString(playerName.substring(0, Math.min(playerName.length(), 15)), 67, 20);
		}else{
			g2d.drawString("No name", 67, 20);
		}
		if (flag != null){
			g2d.setFont(DICE_PER_TURN_COUNT_FONT);
			g2d.drawString("- " + String.valueOf(dicePerTurnCount), 75, 45);
			
			g2d.setFont(DICE_IN_RESERVE_COUNT_FONT);
			g2d.drawString("- " + String.valueOf(diceCountInReserve), 125, 45);
		}
		g2d.setFont(oldFont);
	}

	public void setDicePerTurnCount(int areaCount) {
		this.dicePerTurnCount = areaCount;
	}

	public int getDicePerTurnCount() {
		return dicePerTurnCount;
	}
	
	public void setDiceCountInReserve(int diceCountInReserve) {
		this.diceCountInReserve = diceCountInReserve;
	}
	
	public int getDiceCountInReserve() {
		return diceCountInReserve;
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
		HAPPY(1), SMILING(2), CALM(3), EVIL(4), SAD(5);
		
		int i;
		
		private Emotion(int i){
			this.i = i;
		}
		
		public int value(){
			return i;
		}
	}
}
