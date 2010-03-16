package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.Point;

class BackgroundPainter extends AbstractPainter{
	private static final long serialVersionUID = 1854613245059250352L;
	
	private BufferedImage background;

	BackgroundPainter(FullLand land, Color color) {
		int rowOffset = 0;
		int correction = 4;

		int minX = 10000;
		int minY = 10000;
		int maxX = 0;
		int maxY = 0;
		for (Point p : land.getPoints()) {
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
			background = new BufferedImage(maxX + 20, maxY + 20,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) background.getGraphics();

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			
			g2d.setColor(color);
			g2d.setStroke(stroke);
			g2d.fillRect(0, 0, maxX+20, maxY+20);

			for (Point p : land.getPoints()) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = p.getX() * 19 + rowOffset;
				int _y = p.getY() * (20 - correction);
				Polygon pol = getHexagon(_x - minX, _y - minY, 10);
				//g2d.fillPolygon(pol);
				drawBorder(g2d, land.getPoints(), p, pol);
			}

			g2d.dispose();
		} else {
			System.err.println("not proper land, land id:"
					+ String.valueOf(land.getLandId()) + ", maxX:" + maxX
					+ ", maxY:" + maxY);
		}
	}

	BufferedImage getBackgroundImage() {
		return background;
	}
}
