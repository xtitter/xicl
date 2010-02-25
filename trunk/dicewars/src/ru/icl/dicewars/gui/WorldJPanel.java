package ru.icl.dicewars.gui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.gui.arrow.Arrow;
import ru.icl.dicewars.gui.arrow.ArrowFactory;
import ru.icl.dicewars.gui.arrow.ArrowFactory.ArrowType;
import ru.icl.dicewars.gui.manager.ImageManager;

public class WorldJPanel extends JPanel {

	private FullWorld world;

	/*@SuppressWarnings("serial")
	HashSet<Flag> predefinedDices = new HashSet<Flag>(){{
		add(Flag.BLUE);
		add(Flag.RED);
		add(Flag.GREEN);
		add(Flag.YELLOW);
		add(Flag.CYAN);
		add(Flag.ORANGE);
		add(Flag.MAGENTA);
		add(Flag.GRAY);
	}
	};*/
	
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
	
	private int attackingPlayerLandId = 0;
	private int defendingPlayerLandId = 0;
	
	private int arrowFromLandId = 0;
	private int arrowToLandId = 0;
	
	private AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.75f);
	
	private boolean drawArrow = true;
	
	public WorldJPanel() {
		setPreferredSize(new Dimension(1350,930));
	}
	
	public void update(FullWorld world) {
		synchronized (flag2) {
			this.world = world;
			width = getWidth();
			height = getHeight();
			this.doubleBuffer = null;	
		}
		repaint();
	}
	
	public void updateAttackingPlayer(int attackingPlayerLandId) {
		this.attackingPlayerLandId = attackingPlayerLandId;
		synchronized (flag2) {
			this.doubleBuffer = null;	
		}
		repaint();
	}

	public void updateDefendingPlayerLandId(int defendingPlayerLandId) {
		this.defendingPlayerLandId = defendingPlayerLandId;
		synchronized (flag2) {
			this.doubleBuffer = null;	
		}
		repaint();
	}
	
	public void update(FullLand land) {
		defendingPlayerLandId = 0;
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
	
	public void disableDrawArraw(){
		synchronized (flag2) {
			this.drawArrow = false;
			this.arrowFromLandId = 0;
			this.arrowToLandId = 0;
			this.doubleBuffer = null;	
		}
		repaint();
	}
	
	public void enableDrawArraw(Integer fromLandId, Integer toLandId){
		synchronized (flag2) {
			this.drawArrow = true;
			this.arrowFromLandId = fromLandId.intValue();
			this.arrowToLandId = toLandId.intValue();
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

				final int defendingLandId = defendingPlayerLandId;
				final int attackingLandId = attackingPlayerLandId;

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
				
				int offsetX = 2;
				int offsetY = 5;
				
				for (FullLand land : landsTmp) {
					boolean battle = land.getLandId() == defendingLandId || land.getLandId() == attackingLandId;
					l = LandFactory.getLand(land.getLandId(), land.getFlag());
					if (l != null) {
						if (!battle) {
							g2d.drawImage(l.image, l.x, l.y, l.size.width, l.size.height, this);
						} else {
							BufferedImage doubleBuffer2 = new BufferedImage(l.size.width, l.size.height, BufferedImage.TYPE_INT_ARGB);
							Graphics2D gd = (Graphics2D) doubleBuffer2.getGraphics();
							gd.setComposite(alphaComposite); 
							gd.drawImage(l.image, 0, 0, l.size.width, l.size.height, this);
							g2d.drawImage(doubleBuffer2, l.x - offsetX, l.y - offsetY, l.size.width, l.size.height, this);
							gd.dispose();
						}
						if (land.getLandId() == arrowFromLandId){
							p1 = l.center;
						}else if (land.getLandId() == arrowToLandId){
							p2 = l.center;
						}
					}
				}			
			
				/**
				 * Draw dices on the map
				 */
				for (FullLand land : landsTmp) {
					//boolean battle = land.getLandId() == defendingLandId || land.getLandId() == attackingLandId;
					l = LandFactory.getLand(land.getLandId(), land.getFlag());
					if (l != null) {
						Image diceImage = null;
						//if (predefinedDices.contains(land.getFlag())) {
							diceImage = ImageManager.getDice(land.getDiceCount(), land.getFlag());
						/*} else {
							diceImage = ImageManager.getDice(land.getDiceCount(), getDiceColorByFlag(land.getFlag(), battle ? 130 : 255 ));
						}*/
						if (diceImage != null){
							g2d.drawImage(diceImage, l.center.x + DICE_X_OFFSET, l.center.y + DICE_Y_OFFSET, this);
						}
					}
				}
				
				/**
				 * Draw bezier arrow
				 */
				if (p1 != null && p2 != null && drawArrow) {
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
	
	/*private Color getDiceColorByFlag(Flag f, int alpha){
		switch (f) {
		case YELLOW:
			return new Color(175, 175, 0, alpha);
		case BLUE:
			return new Color(0, 0, 255, alpha);
		case CYAN:
			return new Color(100, 255, 255, alpha);
		case GREEN:
			return new Color(15, 70, 15, alpha);
		case MAGENTA:
			return new Color(255, 100, 255, alpha);
		case ORANGE:
			return new Color(203, 90, 0, alpha);
		case RED:
			return new Color(255, 20, 20, alpha);
		case GRAY: 
			return new Color(150, 150, 150, alpha);
		}
		return Color.black;
	}*/
}
