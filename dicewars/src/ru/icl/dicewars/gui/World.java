package ru.icl.dicewars.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullLandImpl;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.Point;

public class World extends JPanel {

	private FullWorld world;
	private int width;
	private int height;
	private static final long serialVersionUID = -3234906592754761865L;

	private static final int X_OFFSET = 50;
	private static final int Y_OFFSET = 20;
	final static BasicStroke stroke = new BasicStroke(2.0f);
	
	private static final int MIN_X = -1;
	private static final int MIN_Y = -1;
	private static final int MAX_X = 70;
	private static final int MAX_Y = 70;
	
	private static Font diceFont;
	
	public World() {
		diceFont = new Font("Calibri", Font.BOLD, (int) (30 /** aspectRatio*/));   
	}
	
	public void update(FullWorld world) {
		this.world = world;
		width = getWidth();
		height = getHeight();
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		if (world == null)
			return;

		BufferedImage doubleBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) doubleBuffer.getGraphics();

		g2d.setStroke(stroke);
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setFont(World.diceFont);
		
		//AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
		//at.shear(-.5, 0);
		//g2d.transform(at);
		    
		int correction = 4;

		FullLand empty = new FullLandImpl(0);
		for (int i = MIN_X; i < MAX_X + 1; i++) {
			for (int j = MIN_Y; j < MAX_Y + 1; j++) {
				empty.getPoints().add(new Point(i,j));
			}
		}
		
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.6f));
		
		int rowOffset = 0;
		double x = 0;
		double y = 0;
		for (FullLand land : world.getFullLands()) {
			g2d.setColor(getColorByFlag(land.getFlag()));
			for (Point p : land.getPoints()) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = X_OFFSET + p.getX()*19 + rowOffset;
				int _y = Y_OFFSET + p.getY()*(20 - correction);
				Polygon pol = getHexagon(_x, _y, 10);
				//g2d.drawPolygon(pol);
				empty.getPoints().remove(p);
				g2d.fillPolygon(pol);
				drawBorder(g2d, land, p, pol);
				x += _x;
				y += _y;
			}
			int size = land.getPoints().size();
			String count = "";
			if (size > 0) {
				x /= size;
				y /= size;
				count = String.valueOf(land.getDiceCount());
				g2d.setColor(Color.black);
				g2d.drawString(count, (int)x+2, (int)y+2);
				g2d.setColor(Color.white);
				g2d.drawString(count, (int)x, (int)y);
			}
		}
		
		g2d.setColor(new Color(240,240,240,150));
        for (Point p : empty.getPoints()) {
             rowOffset = p.getY() % 2 == 0 ? 9 : 0;
             Polygon pol = getHexagon(X_OFFSET + p.getX()*19 + rowOffset, Y_OFFSET + p.getY()*(20 - correction), 10);
             g2d.fillPolygon(pol);
			drawBorder(g2d, empty, p, pol);
		}
				
		g.drawImage(doubleBuffer, 0, 0, width, height, this);
		g2d.dispose();
	    g.dispose();
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
		if (p.getX() == MAX_X || land.getPoints().contains(p1)) {
			skip.add(1);
		}
		p1 = new Point(p.getX() - (p.getY() % 2 == 0 ? 0 : 1),p.getY()-1);
		if (p.getX() == MIN_X || p.getY() == MIN_Y || land.getPoints().contains(p1)) {
			skip.add(4);
		}
		p1 = new Point(p.getX() - (p.getY() % 2 == 0 ? 0 : 1),p.getY()+1);
		if (p.getX() == MIN_X || land.getPoints().contains(p1)) {
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
	
	private Color getColorByFlag(Flag f) {
		//return new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
		
		switch (f) {
			case WHITE: return Color.white;
			case BLUE: return Color.blue;
			case CYAN: return Color.cyan;
			case GREEN: return Color.green;
			case MAGENTA: return Color.magenta;
			case ORANGE: return Color.orange;
			case RED: return Color.red;
		}
		
		return Color.black;
	}

	Polygon getHexagon(int x, int y, int h) {
		Polygon hexagon = new Polygon();

		double a;
		for (int i = 0; i < 6; i++) {
			a = Math.PI / 3.0 * i;
			hexagon.addPoint(x + (int) (Math.round(Math.sin(a) * h)), y + (int) (Math.round(Math.cos(a) * h * 1)));
		}
		hexagon.ypoints[3] = 2*y - hexagon.ypoints[0];
		return hexagon;
	}
}
