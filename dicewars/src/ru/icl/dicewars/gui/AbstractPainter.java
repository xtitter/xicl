package ru.icl.dicewars.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.HashSet;
import java.util.Set;

import ru.icl.dicewars.core.Point;

abstract class AbstractPainter {
	static final int MIN_X = WorldJPanel.MIN_X;
	static final int MIN_Y = WorldJPanel.MIN_Y;
	static final int MAX_X = WorldJPanel.MAX_X;
	static final int MAX_Y = WorldJPanel.MAX_Y;
	
	final static BasicStroke stroke = new BasicStroke(2.0f);
	
	Polygon getHexagon(int x, int y, int h) {
		Polygon hexagon = new Polygon();

		double a;
		for (int i = 0; i < 6; i++) {
			a = Math.PI / 3.0 * i;
			hexagon.addPoint(x + (int) (Math.round(Math.sin(a) * h)), y
					+ (int) (Math.round(Math.cos(a) * h * 1)));
		}
		hexagon.ypoints[3] = 2 * y - hexagon.ypoints[0];
		return hexagon;
	}

	void drawBorder(Graphics2D g2d, Set<Point> landPoints, Point p, Polygon pol) {
		if (landPoints == null) throw new IllegalArgumentException();
		
		Color color = g2d.getColor();
		g2d.setColor(Color.black);

		Set<Integer> skip = new HashSet<Integer>();
		Point p1 = new Point(p.getX() + 1, p.getY());
		if (p.getX() == MAX_X || landPoints.contains(p1)) {
			skip.add(2);
		}
		p1 = new Point(p.getX() - 1, p.getY());
		if (p.getX() == MIN_X || landPoints.contains(p1)) {
			skip.add(5);
		}
		p1 = new Point(p.getX() + (p.getY() % 2 != 0 ? 0 : 1), p.getY() - 1);
		if (p.getX() == MAX_X || p.getY() == MIN_Y
				|| landPoints.contains(p1)) {
			skip.add(3);
		}
		p1 = new Point(p.getX() + (p.getY() % 2 != 0 ? 0 : 1), p.getY() + 1);
		if (p.getX() == MAX_X || p.getY() == MAX_Y
				|| landPoints.contains(p1)) {
			skip.add(1);
		}
		p1 = new Point(p.getX() - (p.getY() % 2 == 0 ? 0 : 1), p.getY() - 1);
		if (p.getX() == MIN_X || p.getY() == MIN_Y
				|| landPoints.contains(p1)) {
			skip.add(4);
		}
		p1 = new Point(p.getX() - (p.getY() % 2 == 0 ? 0 : 1), p.getY() + 1);
		if (p.getX() == MIN_X || p.getY() == MAX_Y
				|| landPoints.contains(p1)) {
			skip.add(6);
		}

		int x1, x2, y1, y2;
		x1 = pol.xpoints[0];
		y1 = pol.ypoints[0];
		for (int i = 1; i < 6; i++) {
			x2 = x1;
			y2 = y1;
			x1 = pol.xpoints[i];
			y1 = pol.ypoints[i];

			if (!skip.contains(Integer.valueOf(i))) {
				g2d.drawLine(x1, y1, x2, y2);
			}
		}
		if (!skip.contains(Integer.valueOf(6))) {
			x2 = pol.xpoints[0];
			y2 = pol.ypoints[0];
			g2d.drawLine(x1, y1, x2, y2);
		}
		g2d.setColor(color);
	}

}
