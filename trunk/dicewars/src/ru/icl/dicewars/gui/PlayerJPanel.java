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
	private int diceOverallCount = 0;

	private int areaCount = 0;
	private int reserveCount = 0;
	private boolean outOfTheGame = false;
	private int rank = 0;
	
	private Image avatar;
	
	private final static int radius = 12; 
	private final static int alpha = 90; 
	
	public PlayerJPanel(Flag flag, String playerName) {
		this.color = FlagToColorUtil.getColorByFlag(flag, alpha);
		this.flag = flag;
		this.playerName = playerName;
		setBorder(new RoundedBorder(this.color, this.color, this.color, radius, 0));
		//setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, FlagToColorUtil.getColorByFlag(flag, 100), FlagToColorUtil.getColorByFlag(flag, 100)));
		avatar = ImageManager.getAvatar(flag);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(FlagToColorUtil.getColorByFlag(flag, alpha));
		g.fillRect(radius, radius, getWidth() - radius*2, getHeight() - radius*2);
		
		if (avatar != null) g.drawImage(avatar, radius, radius, 50, 50, this);
		
		g.setColor(Color.black);
		g.drawString(playerName, 75, 23);
		g.drawString("Dice count: " + String.valueOf(diceOverallCount), 75, 41);
		g.drawString("Reserve: " + String.valueOf(reserveCount), 75, 59);
		
		if (outOfTheGame) {
			g.drawString("OUT", 140, 25);
			if (rank > 0) {
				g.drawString("Place: " + String.valueOf(rank), 15, 78);
			}
		}		
	}
	
	public void setAreaCount(int areaCount) {
		this.areaCount = areaCount;
	}

	public int getAreaCount() {
		return areaCount;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setDiceOverallCount(int diceOverallCount) {
		this.diceOverallCount = diceOverallCount;
	}

	public void setReserveCount(int reserveCount) {
		this.reserveCount = reserveCount;
	}
	
	public int getDiceOverallCount() {
		return diceOverallCount;
	}
	
	public Flag getFlag() {
		return flag;
	}
	
	public void setOutOfTheGame(boolean outOfTheGame) {
		this.outOfTheGame = outOfTheGame;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
}