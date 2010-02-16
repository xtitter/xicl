package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.gui.util.FlagToColorUtil;

public class PlayerPanel extends JPanel {

	private static final long serialVersionUID = 8066379108638626622L;

	private Color color;
	private String playerName;
	private int diceOverallCount = 0;

	private int areaCount = 0;
	private int reserveCount = 0;
	
	public PlayerPanel(Flag flag) {
		this.color = FlagToColorUtil.getColorByFlag(flag, 165);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawString("Dice count: " + String.valueOf(diceOverallCount), 10, 20);
		g.drawString("Reserve: " + String.valueOf(reserveCount), 10, 40);
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
}
