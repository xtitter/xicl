package ru.icl.dicewars.gui.arrow;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class LineArrowWithArrowHead extends LineArrow {

	final static float arrowSize = 5.0f;
	private boolean inverted = false;

	protected LineArrowWithArrowHead(int from) {
		super(from);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		if (inverted) {
			drawArrowHead(g2D, x2, y2, x1, y1);
		} else {
			drawArrowHead(g2D, x1, y1, x2, y2);
		}
	}

	public void drawArrowHead(Graphics2D g2d, int xCenter, int yCenter, int x, int y) {
		double aDir = Math.atan2(xCenter - x, yCenter - y);
		g2d.setStroke(new BasicStroke(1f));
		Polygon tmpPoly = new Polygon();
		Polygon tmpPoly2 = new Polygon();
		int i1 = 12 + (int) (arrowSize * 2);
		int i2 = 6 + (int) arrowSize;
		tmpPoly.addPoint(x, y);
		tmpPoly2.addPoint(x+1, y+2);
		tmpPoly.addPoint(x + xCor(i1, aDir + .5), y + yCor(i1, aDir + .5));
		tmpPoly2.addPoint(x + xCor(i1, aDir + .5)+1, y + yCor(i1, aDir + .5)+2);
		//Rectangle r1 = new Rectangle(x, y, x + xCor(i1, aDir + .5), y + yCor(i1, aDir + .5));
		tmpPoly.addPoint(x + xCor(i2, aDir), y + yCor(i2, aDir));
		tmpPoly.addPoint(x + xCor(i1, aDir - .5), y + yCor(i1, aDir - .5));
		tmpPoly.addPoint(x, y);
		
		tmpPoly2.addPoint(x + xCor(i2, aDir)+1, y + yCor(i2, aDir)+2);
		tmpPoly2.addPoint(x + xCor(i1, aDir - .5)+1, y + yCor(i1, aDir - .5)+2);
		tmpPoly2.addPoint(x+1, y+2);
		//Rectangle r2 = new Rectangle(x + xCor(i1, aDir - .5), y + yCor(i1, aDir - .5), x, y);
		
		g2d.setColor(LineArrow.shadowColor);
		
		g2d.drawPolygon(tmpPoly2);
		g2d.fillPolygon(tmpPoly2);
		
		g2d.setColor(super.color);
		
		g2d.drawPolygon(tmpPoly);
		g2d.fillPolygon(tmpPoly);
		
		//g2d.setColor(LineArrow.shadowColor);
		//g2d.drawLine(r1.x, r1.y, r1.width, r1.height);
		//g2d.drawLine(r2.x, r2.y, r2.width, r2.height);
	}

	private static int yCor(int len, double dir) {
		return (int) (len * Math.cos(dir));
	}

	private static int xCor(int len, double dir) {
		return (int) (len * Math.sin(dir));
	}

	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}
	
	private static final long serialVersionUID = 1L;
}
