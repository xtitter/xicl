package ru.icl.dicewars.gui.arrow;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;

public class LineArrowWithArrowHead extends LineArrow {
	private static final long serialVersionUID = 1L;

	final static float arrowSize = 7.0f;

	boolean inverted = false;

	LineArrowWithArrowHead() {
		super();
	}
	
	LineArrowWithArrowHead(int x1, int y1, int x2, int y2) {
		super(x1, y1, x2, y2);
	}

	LineArrowWithArrowHead(Rectangle rec) {
		super(rec);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		if (isInverted()) {
			drawArrowHead(g2d, x2, y2, x1, y1);
		} else {
			drawArrowHead(g2d, x1, y1, x2, y2);
		}
	}

	void drawArrowHead(Graphics2D g2d, int xCenter, int yCenter, int x, int y) {
		final Stroke oldStroke = g2d.getStroke();
		final Color oldColor = g2d.getColor();
		
		double aDir = Math.atan2(xCenter - x, yCenter - y);
		g2d.setStroke(new BasicStroke(1f));
		Polygon tmpPoly = new Polygon();
		Polygon tmpPoly2 = new Polygon();
		int i1 = 12 + (int) (arrowSize * 2);
		int i2 = 6 + (int) arrowSize;
		tmpPoly.addPoint(x, y);
		tmpPoly2.addPoint(x + 1, y + 2);
		tmpPoly.addPoint(x + xCor(i1, aDir + .5), y + yCor(i1, aDir + .5));
		tmpPoly2.addPoint(x + xCor(i1, aDir + .5) + 1, y + yCor(i1, aDir + .5)
				+ 2);
		// Rectangle r1 = new Rectangle(x, y, x + xCor(i1, aDir + .5), y +
		// yCor(i1, aDir + .5));
		tmpPoly.addPoint(x + xCor(i2, aDir), y + yCor(i2, aDir));
		tmpPoly.addPoint(x + xCor(i1, aDir - .5), y + yCor(i1, aDir - .5));
		tmpPoly.addPoint(x, y);

		tmpPoly2.addPoint(x + xCor(i2, aDir) + 1, y + yCor(i2, aDir) + 2);
		tmpPoly2.addPoint(x + xCor(i1, aDir - .5) + 1, y + yCor(i1, aDir - .5)
				+ 2);
		tmpPoly2.addPoint(x + 1, y + 2);
		// Rectangle r2 = new Rectangle(x + xCor(i1, aDir - .5), y + yCor(i1,
		// aDir - .5), x, y);

		// g2d.setColor(LineArrow.shadowColor);

		// g2d.drawPolygon(tmpPoly2);
		// g2d.fillPolygon(tmpPoly2);

		g2d.setColor(super.color);
		GradientPaint gradient = new GradientPaint(new java.awt.Point(x1, y1),
				Color.black, new java.awt.Point(x2, y2), super.color);
		g2d.setPaint(gradient);

		g2d.drawPolygon(tmpPoly);
		g2d.fillPolygon(tmpPoly);

		// g2d.setColor(LineArrow.shadowColor);
		// g2d.drawLine(r1.x, r1.y, r1.width, r1.height);
		// g2d.drawLine(r2.x, r2.y, r2.width, r2.height);
		
		g2d.setColor(oldColor);
		g2d.setStroke(oldStroke);
	}

	private static int yCor(int len, double dir) {
		return (int) (len * Math.cos(dir));
	}

	private static int xCor(int len, double dir) {
		return (int) (len * Math.sin(dir));
	}

	public boolean isInverted() {
		return inverted;
	}

	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}
}
