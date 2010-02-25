package ru.icl.dicewars.gui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.Timer;

import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.gui.arrow.Arrow;
import ru.icl.dicewars.gui.arrow.ArrowFactory;
import ru.icl.dicewars.gui.arrow.BezierArrow;
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
	
	private BufferedImage doubleBuffer = null;

	//Bug with concurrent modification fix. This is slowest method. World object should be wrapped.
	private Object flag = new Object();	
	private Object flag2 = new Object(); 
	private Object flag3 = new Object(); 
	
	private int attackingPlayerLandId = 0;
	private int defendingPlayerLandId = 0;
	
	private int arrowFromLandId = 0;
	private int arrowToLandId = 0;
	
	private AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.75f);
	
	private boolean drawArrow = true;
	private int arrowState = 0;
	
	private LandFactory landFactory = new LandFactory();
	
	private ArrayList<Point> points = null;
	
	private final ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (WorldJPanel.this.points == null || WorldJPanel.this.arrowState >= WorldJPanel.this.points.size() - 1){
				synchronized (flag3) {
					flag3.notifyAll();	
				}
			}else{
				synchronized (flag2) {
					doubleBuffer = null;
					arrowState++;
				}
				repaint();
			}
		}
	};

	private Timer t = new Timer(1, actionListener);
	
	public WorldJPanel() {
		setPreferredSize(new Dimension(1350,930));
	}
	
	public void update(FullWorld world) {
		synchronized (flag2) {
			this.world = world;
			landFactory = new LandFactory();
			landFactory.buildTheWorld(world);
			landFactory.buildBackground(world);
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
	
	public void stopDrawArrow(){
		synchronized (flag3) {
			flag3.notifyAll();
		}
		t.stop();
	}
	
	public void eraseArrow(){
		synchronized (flag2) {
			this.drawArrow = false;
			this.points = null;
			this.arrowFromLandId = 0;
			this.arrowToLandId = 0;
			this.arrowState = 0;
			this.doubleBuffer = null;
		}
		repaint();
	}
	
	public void drawArrow(Integer fromLandId, Integer toLandId){
		synchronized (flag2) {
			this.drawArrow = true;
			this.points = null;
			this.arrowFromLandId = fromLandId.intValue();
			this.arrowToLandId = toLandId.intValue();
			this.doubleBuffer = null;
			this.arrowState = 0;
			this.doubleBuffer = null;
		}
		
		Set<FullLand> landsTmp;
		
		synchronized (flag) {
			landsTmp = new HashSet<FullLand>(world.getFullLands());	
		}
		
		Point p1 = null; Point p2 = null;		
		for (FullLand land : landsTmp) {
			ColoredLand l = landFactory.getLand(land.getLandId(), land.getFlag());
			if (land.getLandId() == arrowFromLandId){
				p1 = l.center;
			}else if (land.getLandId() == arrowToLandId){
				p2 = l.center;
			}
		}	
		
		Arrow arrow = ArrowFactory.getArrow(0, ArrowType.BEZIER);
		arrow.setVisible(true);
		arrow.setOpaque(false);
		arrow.setBounds(0, 0, width, height);
		arrow.setCoordinates(p1.x, p1.y, p2.x, p2.y);
        
        this.points = ((BezierArrow)arrow).getAllPoints();
		
        t.start();
		synchronized (flag3) {
			try{
				flag3.wait();
			}catch (InterruptedException e) {
			}
		}
		t.stop();
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
				
				/*
				 * Draw white-gray background
				 */
				ColoredLand l = landFactory.getBackground();
				if (l != null) {
					g2d.drawImage(l.image, l.x, l.y, l.size.width, l.size.height, this);
				}
				
				/*
				 * Draw lands
				 */
				Point p1 = null; Point p2 = null;
				
				int offsetX = 2;
				int offsetY = 5;
				
				for (FullLand land : landsTmp) {
					boolean battle = land.getLandId() == defendingLandId || land.getLandId() == attackingLandId;
					l = landFactory.getLand(land.getLandId(), land.getFlag());
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
			
				/*
				 * Draw dices on the map
				 */
				for (FullLand land : landsTmp) {
					l = landFactory.getLand(land.getLandId(), land.getFlag());
					if (l != null) {
						Image diceImage = ImageManager.getDice(land.getDiceCount(), land.getFlag());
						if (diceImage != null){
							g2d.drawImage(diceImage, l.center.x + DICE_X_OFFSET, l.center.y + DICE_Y_OFFSET, this);
						}
					}
				}
				
				/*
				 * Draw bezier arrow
				 */
				if (p1 != null && p2 != null && drawArrow && this.points != null && this.arrowState <= this.points.size() - 1) {
					p2 = this.points.get(this.arrowState);

					Arrow arrow = ArrowFactory.getArrow(0, ArrowType.BEZIER);
					arrow.setVisible(true);
					arrow.setOpaque(false);
					arrow.setBounds(0, 0, width, height);
					arrow.setCoordinates(p1.x, p1.y, p2.x, p2.y);

					BufferedImage arrowImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					Graphics2D arrowG2D = arrowImage.createGraphics();
					arrow.paintComponent(arrowG2D);

					g2d.drawImage(arrowImage, 0, 0, width, height, this);

				}

				g2d.dispose();
			}
			g.drawImage(this.doubleBuffer, 0, 0, width, height, this);
		}
	    g.dispose();
	}
}
