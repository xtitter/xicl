package ru.icl.dicewars.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.Point;
import ru.icl.dicewars.gui.util.FlagToColorUtil;

public class LandPainter {

	private static final long serialVersionUID = 1854613245059250352L;
	
	private HashMap<Flag,ColoredLand> coloredLands = new HashMap<Flag, ColoredLand>();
	private ColoredLand background;
	final static BasicStroke stroke = new BasicStroke(2.0f);
	
	private static final int MIN_X = -1;
	private static final int MIN_Y = -1;
	private static final int MAX_X = 68;
	private static final int MAX_Y = 55;

	public LandPainter(FullLand land, List<Flag> flags) {
		int rowOffset = 0;
		int correction = 4;
		
		int minX = 10000;
		int minY = 10000;
		int maxX = 0;
		int maxY = 0;
		for (Point p : land.getPoints()) {
			rowOffset = p.getY() % 2 == 0 ? 9 : 0;
			int _x = WorldJPanel.X_OFFSET + p.getX()*19 + rowOffset;
			int _y = WorldJPanel.Y_OFFSET + p.getY()*(20 - correction);
			if (_x < minX) minX = _x;
			if (_y < minY) minY = _y;
			if (_x > maxX) maxX = _x;
			if (_y > maxY) maxY = _y;
		}
		
		minX -= 10;
		minY -= 10;
		maxX -= minX;
		maxY -= minY;
		
		if (maxX > 0 && maxY > 0) {
			int size = land.getPoints().size();
			java.awt.Point center = null;
			for (Flag flag : flags) {
				ColoredLand coloredLand = new ColoredLand();
				coloredLand.image = new BufferedImage(maxX + 20, maxY + 20, BufferedImage.TYPE_INT_ARGB);
				coloredLand.size = new Dimension(maxX + 20, maxY + 20);
				coloredLand.x = minX;
				coloredLand.y = minY;
				Graphics2D g2d = (Graphics2D) coloredLand.image.getGraphics();

				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Color color = FlagToColorUtil.getColorByFlag(flag, 165);
				g2d.setColor(color);
				g2d.setStroke(stroke);

				int x = 0;
				int y = 0;
				
				for (Point p : land.getPoints()) {
					rowOffset = p.getY() % 2 == 0 ? 9 : 0;
					int _x = WorldJPanel.X_OFFSET + p.getX() * 19 + rowOffset;
					int _y = WorldJPanel.Y_OFFSET + p.getY() * (20 - correction);
					x += _x;
					y += _y;
				}
				
				if (center == null && size > 0) {
					center = new java.awt.Point(x / size, y / size);
				}
				
				coloredLand.center = center;		
				
				java.awt.Point c = new java.awt.Point(maxX*2, maxY);
				
				for (Point p : land.getPoints()) {
					rowOffset = p.getY() % 2 == 0 ? 9 : 0;
					int _x = WorldJPanel.X_OFFSET + p.getX() * 19 + rowOffset;
					int _y = WorldJPanel.Y_OFFSET + p.getY() * (20 - correction);
					Polygon pol = getHexagon(_x - minX, _y - minY, 10);
					
					//float d = maxX + maxY;
					
					RadialGradientPaint gradient = new RadialGradientPaint(c, (float)maxX*2, new float[]{0.15f, 0.9f}, new Color[]{Color.black, color});
					
					g2d.setPaint(gradient);
					
					g2d.fillPolygon(pol);
					
					drawBorder(g2d, land, p, pol);
				}

				g2d.dispose();

				coloredLands.put(flag, coloredLand);
			}
		} else {
			System.err.println("not proper land, land id:" + String.valueOf(land.getLandId()) + ", maxX:" + maxX + ", maxY:" + maxY);
		}
	}
	
	public LandPainter(FullLand land, Color color) {
		int rowOffset = 0;
		int correction = 4;
		
		int minX = 10000;
		int minY = 10000;
		int maxX = 0;
		int maxY = 0;
		for (Point p : land.getPoints()) {
			rowOffset = p.getY() % 2 == 0 ? 9 : 0;
			int _x = WorldJPanel.X_OFFSET + p.getX()*19 + rowOffset;
			int _y = WorldJPanel.Y_OFFSET + p.getY()*(20 - correction);
			if (_x < minX) minX = _x;
			if (_y < minY) minY = _y;
			if (_x > maxX) maxX = _x;
			if (_y > maxY) maxY = _y;
		}
		
		minX -= 10;
		minY -= 10;
		maxX -= minX;
		maxY -= minY;
		
		if (maxX > 0 && maxY > 0) {
			background = new ColoredLand();
			background.image = new BufferedImage(maxX + 20, maxY + 20, BufferedImage.TYPE_INT_ARGB);
			background.size = new Dimension(maxX + 20, maxY + 20);
			background.x = minX;
			background.y = minY;
			Graphics2D g2d = (Graphics2D) background.image.getGraphics();

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.setColor(color);
			g2d.setStroke(stroke);

			for (Point p : land.getPoints()) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = WorldJPanel.X_OFFSET + p.getX() * 19 + rowOffset;
				int _y = WorldJPanel.Y_OFFSET + p.getY() * (20 - correction);
				Polygon pol = getHexagon(_x - minX, _y - minY, 10);
				g2d.fillPolygon(pol);
				drawBorder(g2d, land, p, pol);
			}

			g2d.dispose();
		} else {
			System.err.println("not proper land, land id:" + String.valueOf(land.getLandId()) + ", maxX:" + maxX + ", maxY:" + maxY);
		}
	}
	
	private Polygon getHexagon(int x, int y, int h) {
		Polygon hexagon = new Polygon();

		double a;
		for (int i = 0; i < 6; i++) {
			a = Math.PI / 3.0 * i;
			hexagon.addPoint(x + (int) (Math.round(Math.sin(a) * h)), y + (int) (Math.round(Math.cos(a) * h * 1)));
		}
		hexagon.ypoints[3] = 2*y - hexagon.ypoints[0];
		return hexagon;
	}
	
	private void drawBorder(Graphics2D g2d, FullLand land, Point p, Polygon pol) {
		Color color = g2d.getColor();
		g2d.setColor(Color.black);

		Set<Integer> skip = new HashSet<Integer>();
		Point p1 = new Point(p.getX()+1,p.getY());
		if (p.getX() == MAX_X || land.getPoints().contains(p1)) {
			skip.add(2);
		}
		p1 = new Point(p.getX()-1,p.getY());
		if (p.getX() == MIN_X || land.getPoints().contains(p1)) {
			skip.add(5);
		}
		p1 = new Point(p.getX() + (p.getY() % 2 != 0 ? 0 : 1),p.getY()-1);
		if (p.getX() == MAX_X || p.getY() == MIN_Y || land.getPoints().contains(p1)) {
			skip.add(3);
		}
		p1 = new Point(p.getX() + (p.getY() % 2 != 0 ? 0 : 1),p.getY()+1);
		if (p.getX() == MAX_X || p.getY() == MAX_Y || land.getPoints().contains(p1)) {
			skip.add(1);
		}
		p1 = new Point(p.getX() - (p.getY() % 2 == 0 ? 0 : 1),p.getY()-1);
		if (p.getX() == MIN_X || p.getY() == MIN_Y || land.getPoints().contains(p1)) {
			skip.add(4);
		}
		p1 = new Point(p.getX() - (p.getY() % 2 == 0 ? 0 : 1),p.getY()+1);
		if (p.getX() == MIN_X || p.getY() == MAX_Y || land.getPoints().contains(p1)) {
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
	
	public ColoredLand getLand(Flag flag) {
		return coloredLands.get(flag);
	}
	
	public ColoredLand getBackground() {
		return background;
	}
}
