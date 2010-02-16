package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.gui.util.FlagToColorUtil;

public class PlayerPanel extends JPanel {

	private static final long serialVersionUID = 8066379108638626622L;

	private Flag flag;
	private Color color;
	private String playerName;
	private int diceOverallCount = 0;

	private int areaCount = 0;
	private int reserveCount = 0;
	private boolean outOfTheGame = false;
	private int rank = 0;
	
	public PlayerPanel(Flag flag) {
		this.color = FlagToColorUtil.getColorByFlag(flag, 165);
		this.flag = flag;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawString("Dice count: " + String.valueOf(diceOverallCount), 10, 20);
		g.drawString("Reserve: " + String.valueOf(reserveCount), 10, 40);
		
		if (outOfTheGame) {
			g.drawString("OUT", 140, 20);
			if (rank > 0) {
				g.drawString("Place: " + String.valueOf(rank), 10, 60);
			}
		}
	}
	
	public void setAreaCount(int areaCount) {
		this.areaCount = areaCount;
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
