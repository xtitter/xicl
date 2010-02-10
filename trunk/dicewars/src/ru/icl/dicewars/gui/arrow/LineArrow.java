package ru.icl.dicewars.gui.arrow;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class LineArrow extends Arrow {

	final static float dash[] = { 12.0f };
	final static BasicStroke dashed = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	final static Color defaultColor = Color.red;
	protected final static Color shadowColor = new Color(0,0,0,150);
	
	public Color color;
	
	protected LineArrow(int from) {
		super(from);
		color = defaultColor;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setStroke(dashed);
		g2D.setColor(shadowColor);
		g2D.drawLine(x1+1, y1+2, x2+1, y2+2);
		g2D.setColor(color);
		g2D.drawLine(x1, y1, x2, y2);
	}
	
	public void setColor(Color color) {
		this.color = color;
		repaint();
	}

	private static final long serialVersionUID = 1L;
}
