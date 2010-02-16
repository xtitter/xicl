package ru.icl.dicewars.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullLandImpl;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.Point;
import ru.icl.dicewars.gui.manager.ImageManager;
import ru.icl.dicewars.gui.manager.WindowManager;
import ru.icl.dicewars.gui.util.FlagToColorUtil;

public class World extends JPanel {

	private FullWorld world;

	private int width;
	private int height;
	private static final long serialVersionUID = -3234906592754761865L;

	public static final int X_OFFSET = 50;
	public static final int Y_OFFSET = 20;
	final static BasicStroke stroke = new BasicStroke(2.0f);
	
	private static final int MIN_X = -1;
	private static final int MIN_Y = -1;
	private static final int MAX_X = 70;
	private static final int MAX_Y = 70;
	
	private static Font diceFont = new Font("Calibri", Font.BOLD, (int) (30 /** aspectRatio*/));;
	private static Font idFont = new Font("Calibri", Font.BOLD, (int) (12 /** aspectRatio*/));
	
	private BufferedImage doubleBuffer = null;
	
	//private static Color darkColor = new Color(50,50,50,100);

	//Bug with concurrent modification fix. This is slowest method. World object should be wrapped.
	private Object flag = new Object();	
	private Object flag2 = new Object(); 
	
	private int attackingPlayer = 0;
	private int defendingPlayer = 0;
	
	private Map<Flag, Integer> diceOverallCount = new HashMap<Flag, Integer>();
	
	public World() {
	}
	
	public void update(FullWorld world) {
		this.world = world;
		width = getWidth();
		height = getHeight();
		synchronized (flag2) {
			this.doubleBuffer = null;	
		}
		repaint();
	}
	
	public void update(FullLand land) {
		defendingPlayer = 0;
		width = getWidth();
		height = getHeight();
		FullLand l2 = null;
		
		Set<FullLand> landsTmp;
		synchronized (flag) {
			landsTmp = new HashSet<FullLand>(world.getFullLands());	
		}
		
		for (FullLand l : landsTmp) {
			if (l.getLandId() == land.getLandId()) {
				l2 = l;
				break;
			}
		}
		if (l2 != null) {
			//System.out.println("update:id-" + l2.getLandId() + ":now-" + land.getDiceCount() + ":was-" + l2.getDiceCount());
			synchronized (flag) {
				world.getFullLands().remove(l2);
				//Always should be a another object than in world! This method takes a new object every time.  
				world.getFullLands().add(land);
			}
		}
		
		synchronized (flag2) {
			this.doubleBuffer = null;	
		}
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (world == null)
			return;
		Set<FullLand> landsTmp;

		synchronized (flag2) {
			if (this.doubleBuffer == null){
				final int defendingLandId = defendingPlayer;
				final int attackingLandId = attackingPlayer;

				synchronized (flag) {
					landsTmp = new HashSet<FullLand>(world.getFullLands());	
				}
				
				this.doubleBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
				
				for (FullLand land : landsTmp) {
					boolean battle = land.getLandId() == defendingLandId || land.getLandId() == attackingLandId;
					Color color = FlagToColorUtil.getColorByFlag(land.getFlag(), battle ? 75 : 165);
					g2d.setColor(color);
					
					for (Point p : land.getPoints()) {
						rowOffset = p.getY() % 2 == 0 ? 9 : 0;
						int _x = X_OFFSET + p.getX()*19 + rowOffset;
						int _y = Y_OFFSET + p.getY()*(20 - correction);
						
						if (battle) {
							_x -= 2;
							_y -= 5;
						}
						
						Polygon pol = getHexagon(_x, _y, 10);
						//g2d.drawPolygon(pol);
						empty.getPoints().remove(p);
						
						g2d.fillPolygon(pol);
						/*if (battle) {
							g2d.setColor(World.darkColor);
							g2d.fillPolygon(pol);
							g2d.setColor(color);
						}*/
						drawBorder(g2d, land, p, pol);
					}
				}
				
				g2d.setColor(new Color(240,240,240,150));
		        for (Point p : empty.getPoints()) {
		             rowOffset = p.getY() % 2 == 0 ? 9 : 0;
		             Polygon pol = getHexagon(X_OFFSET + p.getX()*19 + rowOffset, Y_OFFSET + p.getY()*(20 - correction), 10);
		             g2d.fillPolygon(pol);
					 drawBorder(g2d, empty, p, pol);
				}
				
				diceOverallCount.clear();
				for (FullLand land : landsTmp) {
					boolean battle = land.getLandId() == defendingLandId || land.getLandId() == attackingLandId;
					
					int x = 0;
					int y = 0;
					
					for (Point p : land.getPoints()) {
						rowOffset = p.getY() % 2 == 0 ? 9 : 0;
						int _x = X_OFFSET + p.getX()*19 + rowOffset;
						int _y = Y_OFFSET + p.getY()*(20 - correction);
						
						if (battle) {
							_x -= 2;
							_y -= 5;
						}
						x += _x;
						y += _y;
					}
					int size = land.getPoints().size();
					//String count = "";
					if (size > 0) {
						x /= size;
						y /= size;
						int xoffset = -30;
						int yoffset = -70;
						g2d.drawImage(ImageManager.getDice(land.getDiceCount(), getDiceColorByFlag(land.getFlag(), battle ? 130 : 255 )), x + xoffset, y + yoffset, this);
		
						if (!diceOverallCount.containsKey(land.getFlag())) {
							diceOverallCount.put(land.getFlag(), land.getDiceCount());
						} else {
							Integer i = diceOverallCount.get(land.getFlag());
							diceOverallCount.put(land.getFlag(), i + Integer.valueOf(land.getDiceCount()));
						}
						
						/*BufferedImage doubleBuffer2 = new BufferedImage(82, 100, BufferedImage.TYPE_INT_ARGB);
						Graphics2D gd = (Graphics2D) doubleBuffer2.getGraphics();
						gd.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.6f));
						gd.drawImage(ImageManager.getDice(land.getDiceCount(), getColorByFlag(land.getFlag(), 255)), 0, 0, this);
						g2d.drawImage(doubleBuffer2, x + xoffset, y + yoffset, this);
						*/
		
						
						/*count = String.valueOf(land.getDiceCount());
						g2d.setColor(Color.black);
						g2d.drawString(count, (int)x+2, (int)y+2);
						g2d.setColor(Color.white);
						g2d.drawString(count, (int)x, (int)y);
						g2d.setColor(Color.black);
						*/
						
						// Displaying land ids
						g2d.setColor(Color.black);
						g2d.setFont(World.idFont);
						g2d.drawString(land.getLandId()+"", (int)x+10, (int)y+10);
						g2d.setFont(World.diceFont);
						
					}
				}
				
				WindowManager.getManager().getInfoPanel().updateDiceCount(diceOverallCount);
				WindowManager.getManager().getInfoPanel().sortPlayers();
				
		        //g.drawImage(doubleBuffer, 0, 0, width, height, this);
				g2d.dispose();
			}
			g.drawImage(this.doubleBuffer, 0, 0, width, height, this);
		}
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
	
	private Color getDiceColorByFlag(Flag f, int alfa){
		switch (f) {
		case WHITE:
			return Color.white;
		case YELLOW:
			return new Color(175, 175, 0, alfa);
		case BLUE:
			return new Color(0, 0, 255, alfa);
		case CYAN:
			return Color.cyan;
		case GREEN:
			return new Color(15, 70, 15, alfa);
		case MAGENTA:
			return Color.magenta;
		case ORANGE:
			return new Color(203, 90, 0, alfa);
		case RED:
			return new Color(255, 20, 20, alfa);

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
	
	public FullWorld getRecentWorld() {
		return world;
	}
	
	public void updateDefendingPlayer(int defendingPlayer) {
		setDefendingPlayer(defendingPlayer);
		synchronized (flag2) {
			this.doubleBuffer = null;	
		}
	}
	
	public void setDefendingPlayer(int defendingPlayer) {
		this.defendingPlayer = defendingPlayer;
	}
	

	public void updateAttackingPlayer(int attackingPlayer) {
		setAttackingPlayer(attackingPlayer);
		synchronized (flag2) {
			this.doubleBuffer = null;	
		}
	}
	
	public void setAttackingPlayer(int attackingPlayer) {
		this.attackingPlayer = attackingPlayer;
	}

}
