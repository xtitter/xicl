package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.Point;
import ru.icl.dicewars.gui.util.FlagToColorUtil;

class LandPainter extends AbstractPainter{
	private static final long serialVersionUID = 1854613245059250352L;
	
	private HashMap<Flag, ColoredLand> coloredLands = new HashMap<Flag, ColoredLand>();
	
	private Set<Point> points = new HashSet<Point>();

	private int minX = Integer.MAX_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxY = Integer.MIN_VALUE;
	
	private java.awt.Point c1;
	private java.awt.Point c2;
	private java.awt.Point center;
	
	private void init(){
		int rowOffset = 0;
		int correction = 4;

		for (Point p : points) {
			rowOffset = p.getY() % 2 == 0 ? 9 : 0;
			int _x = p.getX() * 19 + rowOffset;
			int _y = p.getY() * (20 - correction);
			if (_x < minX)
				minX = _x;
			if (_y < minY)
				minY = _y;
			if (_x > maxX)
				maxX = _x;
			if (_y > maxY)
				maxY = _y;
		}

		minX -= 10;
		minY -= 10;
		maxX -= minX;
		maxY -= minY;
		
		if (maxX > 0 && maxY > 0) {
			int size = points.size();
			
			int x = 0;
			int y = 0;

			for (Point p : points) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = p.getX() * 19 + rowOffset;
				int _y = p.getY() * (20 - correction);
				x += _x;
				y += _y;
			}

			if (center == null && size > 0) {
				center = new java.awt.Point(x / size, y / size);
			}
			
			c1 = new java.awt.Point(0, minY / 2);
			c2 = new java.awt.Point(maxX * 27 / 10, maxY / 2);
		}else{
			throw new IllegalStateException();
		}
	}
	
	private ColoredLand buildColoredLand(Color color) {
		int rowOffset = 0;
		int correction = 4;

		if (maxX > 0 && maxY > 0) {
			ColoredLand coloredLand = new ColoredLand();
			coloredLand.image = new BufferedImage(maxX + 20, maxY + 20,
					BufferedImage.TYPE_INT_ARGB);
			coloredLand.size = new Dimension(maxX + 20, maxY + 20);
			coloredLand.x = minX;
			coloredLand.y = minY;
			Graphics2D g2d = (Graphics2D) coloredLand.image.getGraphics();

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.setColor(color);
			g2d.setStroke(stroke);

			coloredLand.center = center;

			LinearGradientPaint gradient = new LinearGradientPaint(c2, c1,
					new float[] { 0.1f, 1f },
					new Color[] { Color.BLACK, color });

			for (Point p : points) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = p.getX() * 19 + rowOffset;
				int _y = p.getY() * (20 - correction);
				Polygon pol = getHexagon(_x - minX, _y - minY, 10);

				g2d.setPaint(gradient);

				g2d.fillPolygon(pol);

				drawBorder(g2d, points, p, pol);
			}

			g2d.dispose();

			return coloredLand;
		} else {
			throw new IllegalStateException();
		}
	}
	
	LandPainter(FullLand land) {
		if (land != null)
			this.points.addAll(land.getPoints());
		init();
	}
	
	ColoredLand getLand(Flag flag) {
		ColoredLand coloredLand = coloredLands.get(flag);
		if (coloredLand == null){
			coloredLand = buildColoredLand(FlagToColorUtil.getColorByFlag(flag, 165));
			coloredLands.put(flag, coloredLand);
		}
		return coloredLand;
	}
}
