package ru.icl.dicewars.gui.arrow;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

public class LineArrow extends Arrow {

	final static float dash[] = { 12.0f };
	final static BasicStroke dashed = new BasicStroke(3.0f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	final static Color defaultColor = Color.red;
	final static Color shadowColor = new Color(0, 0, 0, 150);

	Color color = defaultColor;

	LineArrow() {
		super();
	}

	LineArrow(int x1, int y1, int x2, int y2) {
		super(x1, y1, x2, y2);
	}

	LineArrow(Rectangle rec) {
		super(rec);
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		final Stroke oldStroke = g2d.getStroke();
		final Color oldColor = g2d.getColor();
		
		g2d.setStroke(dashed);
		g2d.setColor(shadowColor);
		g2d.drawLine(x1 + 1, y1 + 2, x2 + 1, y2 + 2);
		g2d.setColor(color);
		g2d.drawLine(x1, y1, x2, y2);
		
		g2d.setColor(oldColor);
		g2d.setStroke(oldStroke);
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}

	private static final long serialVersionUID = 1L;
}
