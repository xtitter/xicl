package ru.icl.dicewars.gui.arrow;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

public abstract class Arrow extends JPanel {

	/**
	 * TableID of the card which arrow comes from 
	 */
	private int from;
	protected int x1, y1, x2, y2;
	private boolean opponent;

	public Arrow(int from) {
		super();
		x1 = 0;
		y1 = 0;
		x2 = 0;
		y2 = 0;
		opponent = false;
		this.from = from;
	}

	@Override
	abstract public void paintComponent(Graphics g);

	public void setCoordinates(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		repaint();
	}
	
	public void setCoordinates(Rectangle rec) {
		setCoordinates(rec.x, rec.y, rec.width, rec.height);
	}
	
	public Rectangle getCoordinates() {
		return new Rectangle(x1, y1, x2, y2);
	}
	
	public boolean isOpponent() {
		return opponent;
	}

	public void setOpponent(boolean opponent) {
		this.opponent = opponent;
	}

	public int getFrom() {
		return from;
	}

	private static final long serialVersionUID = 1L;
}
