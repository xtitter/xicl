package ru.icl.dicewars.gui.arrow;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Arrow {
	private static final long serialVersionUID = 1L;

	protected int x1, y1, x2, y2;

	Arrow() {
		super();
		x1 = 0;
		y1 = 0;
		x2 = 0;
		y2 = 0;
	}

	Arrow(int x1, int y1, int x2, int y2) {
		super();
		setCoordinates(x1, y1, x2, y2);
	}

	Arrow(Rectangle rec) {
		super();
		setCoordinates(rec);
	}

	public abstract void paint(Graphics g);

	public void setCoordinates(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public void setCoordinates(Rectangle rec) {
		setCoordinates(rec.x, rec.y, rec.width, rec.height);
	}

	public Rectangle getCoordinates() {
		return new Rectangle(x1, y1, x2, y2);
	}
}
