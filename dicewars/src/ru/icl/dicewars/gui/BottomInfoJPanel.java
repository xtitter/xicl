package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.gui.manager.ImageManager;

public class BottomInfoJPanel extends JPanel {

	private static final long serialVersionUID = 4253340434381302756L;
	private static final Font TEXT_FONT = new Font(Font.SANS_SERIF,
			Font.BOLD, 10);
	private static final Font TURN_FONT = new Font(Font.SANS_SERIF,
			Font.ITALIC, 30);
	private static final Font DICE_SUM_FONT = new Font(Font.SANS_SERIF,
			Font.ITALIC, 20);
	
	@SuppressWarnings("serial")
	private static final FontMetrics DICE_SUM_FONT_METRIC = new FontMetrics(DICE_SUM_FONT){};
	
	private int turnNumber;
	private List<Integer> playerDicesList;
	private Flag playerFlag;
	private Flag opponentFlag;
	private List<Integer> opponentDicesList;

	private final Object sync = new Object();

	public void init() {
		this.turnNumber = 0;
		this.playerDicesList = null;
		this.playerFlag = null;
		this.opponentFlag = null;
		this.opponentDicesList = null;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		if (turnNumber > 0) {
			g2d.setFont(TEXT_FONT);
			g2d.drawString("Turn: ", 10, 10);
			g2d.setFont(TURN_FONT);
			g2d.drawString(String.valueOf(turnNumber), 10, 38);
		}
		g2d.setColor(Color.DARK_GRAY);
		g2d.setFont(DICE_SUM_FONT);
		synchronized (sync) {
			if (opponentDicesList != null && playerDicesList != null && playerFlag != null && opponentFlag != null) {
				int j = 0;
				int sum = 0;
				for (Integer i : playerDicesList){
					Image diceImage = ImageManager.getRolledDiceImage(playerFlag, i.intValue());
					g2d.drawImage(diceImage, 15 + getWidth()/2 - 30 - j * 35, 7, this);
					sum += i;
					j++;
				}
				
				Rectangle2D rectangle2d = DICE_SUM_FONT_METRIC.getStringBounds(String.valueOf(sum) + " =", g2d);
				
				g2d.drawString(String.valueOf(sum) + " =", 15 + getWidth()/2 - 30 - (j-1) * 35 - 7 - (int)rectangle2d.getWidth(), 30);
				Image arrowImage = ImageManager.getArrowImage();
				if (arrowImage != null)
					g2d.drawImage(arrowImage, 23 + getWidth()/2, 11, this);
				j = 0;
				sum = 0;
				for (Integer i : opponentDicesList){
					Image diceImage = ImageManager.getRolledDiceImage(opponentFlag, i.intValue());
					g2d.drawImage(diceImage, 15 + getWidth()/2 + 30 + j * 35 + 7, 7, this);
					sum += i;
					j++;
				}
				g2d.drawString("= " + String.valueOf(sum), 15 + getWidth()/2 + 30 + (j) * 35 + 14, 30);
			}
		}
	}

	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}

	public void updateTurnNumber(int turnNumber) {
		setTurnNumber(turnNumber);
		repaint();
	}

	public void setPlayerDicesList(List<Integer> playerDicesList) {
		this.playerDicesList = playerDicesList;
	}

	public void setOpponentDicesList(List<Integer> opponentDicesList) {
		this.opponentDicesList = opponentDicesList;
	}

	public void setPlayerFlag(Flag playerFlag) {
		this.playerFlag = playerFlag;
	}
	
	public void setOpponentFlag(Flag opponentFlag) {
		this.opponentFlag = opponentFlag;
	}
	
	public void updateDices(Flag playerFlag, Flag opponentFlag, List<Integer> playerDicesList,
			List<Integer> opponentDicesList) {
		synchronized (sync) {
			setPlayerDicesList(playerDicesList);
			setOpponentDicesList(opponentDicesList);
			setOpponentFlag(opponentFlag);
			setPlayerFlag(playerFlag);
		}
		repaint();
	}
}
