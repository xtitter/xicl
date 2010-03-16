package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	
	private Polygon getLandPolygon(Set<Point> landPoints) {
		if (landPoints == null) throw new IllegalArgumentException();
		
		List<Line> lines = new ArrayList<Line>();
		
		for (Point p : landPoints){
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
			
			int correction = 4; 
			int rowOffset = p.getY() % 2 == 0 ? 9 : 0;
			int _x = p.getX() * 19 + rowOffset;
			int _y = p.getY() * (20 - correction);
			
			Polygon pol = getHexagon(_x - minX, _y - minY, 10);
			
			x1 = pol.xpoints[0];
			y1 = pol.ypoints[0];
			for (int i = 1; i < 6; i++) {
				x2 = x1;
				y2 = y1;
				x1 = pol.xpoints[i];
				y1 = pol.ypoints[i];
	
				if (!skip.contains(Integer.valueOf(i))) {
					Line line = new Line(new java.awt.Point(x1,y1),new java.awt.Point(x2,y2));
					lines.add(line);
				}
			}
			if (!skip.contains(Integer.valueOf(6))) {
				x2 = pol.xpoints[0];
				y2 = pol.ypoints[0];
				Line line = new Line(new java.awt.Point(x1,y1),new java.awt.Point(x2,y2));
				lines.add(line);
			}
		}
		
		Polygon pol = new Polygon();
		
		java.awt.Point curr = null;
		java.awt.Point last = null;
		if (lines.size() > 0){
			curr = lines.get(0).p1;
			last = lines.get(0).p2;
			lines.remove(0);
		}
		boolean t = true;
		while (!(Math.abs(curr.x - last.x) < 2 && Math.abs(curr.y - last.y) < 2 && t)){
			pol.addPoint(curr.x, curr.y);
			t = false;
			for (Line line : lines){
				if (Math.abs(curr.x - line.p1.x) < 2 && Math.abs(curr.y - line.p1.y) < 2){
					curr = line.p2;
					lines.remove(line);
					t = true;
					break;
				}
				if (Math.abs(curr.x - line.p2.x) < 2 && Math.abs(curr.y - line.p2.y) < 2){
					curr = line.p1;
					lines.remove(line);
					t = true;
					break;
				}
			}
		}
		
		pol.addPoint(last.x, last.y);
		return pol;
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
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);

			g2d.setColor(color);
			g2d.setStroke(stroke);

			coloredLand.center = center;

			LinearGradientPaint gradient = new LinearGradientPaint(c2, c1,
					new float[] { 0.15f, 1f },
					new Color[] { Color.BLACK, color });

			Polygon polygon = getLandPolygon(points);
			
			g2d.setPaint(gradient);

			g2d.fillPolygon(polygon);

			for (Point p : points) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = p.getX() * 19 + rowOffset;
				int _y = p.getY() * (20 - correction);
				Polygon pol = getHexagon(_x - minX, _y - minY, 10);

				//g2d.setPaint(gradient);

				//g2d.fillPolygon(pol);

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
	
	private static class Line{
		private java.awt.Point p1;
		private java.awt.Point p2;
		
		public Line(java.awt.Point p1, java.awt.Point p2) {
			if (p1 == null || p2 == null) throw new IllegalArgumentException();
			this.p1 = p1;
			this.p2 = p2;
		}
	}
}
