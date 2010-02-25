package ru.icl.dicewars.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.gui.arrow.Arrow;
import ru.icl.dicewars.gui.arrow.ArrowFactory;
import ru.icl.dicewars.gui.arrow.ArrowFactory.ArrowType;
import ru.icl.dicewars.gui.manager.ImageManager;

public class WorldJPanel extends JPanel {

	private FullWorld world;

	private int width;
	private int height;
	private static final long serialVersionUID = -3234906592754761865L;

	public static final int X_OFFSET = 35;
	public static final int Y_OFFSET = 30;

	public static final int DICE_X_OFFSET = -30;
	public static final int DICE_Y_OFFSET = -70;
	
	public static final int MIN_X = -1;
	public static final int MIN_Y = -1;
	public static final int MAX_X = 68;
	public static final int MAX_Y = 55;
	
	//private static Font diceFont = new Font("Calibri", Font.BOLD, (int) (30 /** aspectRatio*/));;
	//private static Font idFont = new Font("Calibri", Font.BOLD, (int) (12 /** aspectRatio*/));
	
	private BufferedImage doubleBuffer = null;

	//Bug with concurrent modification fix. This is slowest method. World object should be wrapped.
	private Object flag = new Object();	
	private Object flag2 = new Object(); 
	
	private int attackingPlayer = 0;
	private int defendingPlayer = 0;
	
	private int speed = 1;
	
	public WorldJPanel() {
		setPreferredSize(new Dimension(1350,930));
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
			
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				/**
				 * Draw white-gray background
				 */
				ColoredLand l = LandFactory.getBackground();
				if (l != null) {
					g2d.drawImage(l.image, l.x, l.y, l.size.width, l.size.height, this);
				}
				
				/**
				 * Draw lands
				 */
				Point p1 = null; Point p2 = null;
				for (FullLand land : landsTmp) {
					boolean battle = land.getLandId() == defendingLandId || land.getLandId() == attackingLandId;
					l = LandFactory.getLand(land.getLandId(), land.getFlag());
					if (l != null) {
						if (!battle) {
							g2d.drawImage(l.image, l.x, l.y, l.size.width, l.size.height, this);
						} else {
							int offsetX = 2;
							int offsetY = 5;
							
							BufferedImage doubleBuffer2 = new BufferedImage(l.size.width, l.size.height, BufferedImage.TYPE_INT_ARGB);
							Graphics2D gd = (Graphics2D) doubleBuffer2.getGraphics();
							gd.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
							gd.drawImage(l.image, 0, 0, l.size.width, l.size.height, this);
							g2d.drawImage(doubleBuffer2, l.x - offsetX, l.y - offsetY, l.size.width, l.size.height, this);
							gd.dispose();
							
							if (p1 == null) p1 = l.center; else p2 = l.center;
						}
					}
				}
				
				/**
				 * Draw dices on the map
				 */
				for (FullLand land : landsTmp) {
					boolean battle = land.getLandId() == defendingLandId || land.getLandId() == attackingLandId;
					l = LandFactory.getLand(land.getLandId(), land.getFlag());
					if (l != null) {
						g2d.drawImage(ImageManager.getDice(land.getDiceCount(), 
								getDiceColorByFlag(land.getFlag(), battle ? 130 : 255 )), 
								l.center.x + DICE_X_OFFSET, l.center.y + DICE_Y_OFFSET, this);
					}
				}
				
				/**
				 * Draw bezier arrow
				 */
				if (p1 != null && p2 != null && speed == 1) {
					Arrow arrow = ArrowFactory.getArrow(0, ArrowType.BEZIER);
					arrow.setVisible(true);
					arrow.setOpaque(false);
					arrow.setBounds(0, 0, width, height);
					arrow.setCoordinates(p1.x, p1.y, p2.x, p2.y);
					
					BufferedImage arrowImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					Graphics2D arrowG2D = arrowImage.createGraphics();
					//arrowG2D.drawLine(p1.x, p1.y, p2.x, p2.y);
			        arrow.paintComponent(arrowG2D);
			        
			        g2d.drawImage(arrowImage, 0, 0, width, height, this);
				}

				g2d.dispose();
			}
			g.drawImage(this.doubleBuffer, 0, 0, width, height, this);
		}
	    g.dispose();
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

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
