package ru.icl.dicewars.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

public final class WorldJPanel extends JPanel {
	private static final long serialVersionUID = -3234906592754761865L;
	
	private static final BufferedImage EMPTY_ARROW_DOUBLE_BUFFERED_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	
	private FullWorld world;

	private int width;
	private int height;

	public static final int X_OFFSET = 35;
	public static final int Y_OFFSET = 30;

	public static final int DICE_X_OFFSET = -30;
	public static final int DICE_Y_OFFSET = -70;
	
	public static final int MIN_X = -1;
	public static final int MIN_Y = -1;
	public static final int MAX_X = 68;
	public static final int MAX_Y = 55;
	
	private BufferedImage doubleBuffer = null;
	private BufferedImage arrowDoubleBuffer = null;
	private int arrowDoubleBufferOffsetX = 0;
	private int arrowDoubleBufferOffsetY = 0;
	
    private static final Font landIdFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
	
	//Bug with concurrent modification fix. This is slowest method. World object should be wrapped.
	private final Object flag = new Object();	
	private final Object flag2 = new Object(); 
	private final Object flag3 = new Object();
	private final Object flag4 = new Object();
	
	private int attackingPlayerLandId = 0;
	private int defendingPlayerLandId = 0;
	
	private AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.75f);
	
	private int arrowState = 0;
	
	private LandFactory landFactory = LandFactory.EMPTY_LAND_FACTORY;
	
	private ArrayList<Point> points = null;
	
	private final ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (WorldJPanel.this.points == null || WorldJPanel.this.arrowState >= WorldJPanel.this.points.size() - 1){
				synchronized (flag3) {
					flag3.notifyAll();	
				}
			}else{
				synchronized (flag4) {
					arrowDoubleBuffer = null;
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
	
	public void updateWorld(FullWorld world) {
		synchronized (flag2) {
			this.world = world;
			if (world!=null){
				landFactory = new LandFactory(world);
				width = getWidth();
				height = getHeight();
			}
			this.doubleBuffer = null;	
		}
		repaint();
	}
	
	public void updateAttackingPlayer(int attackingPlayerLandId) {
		if (world == null || width == 0 || height == 0)
			throw new IllegalStateException();

		this.attackingPlayerLandId = attackingPlayerLandId;
		synchronized (flag2) {
			this.doubleBuffer = null;	
		}
		repaint();
	}

	public void updateDefendingPlayerLandId(int defendingPlayerLandId) {
		if (world == null || width == 0 || height == 0)
			throw new IllegalStateException();

		this.defendingPlayerLandId = defendingPlayerLandId;
		synchronized (flag2) {
			this.doubleBuffer = null;	
		}
		repaint();
	}
	
	public void updateLand(FullLand land) {
		if (world == null || width == 0 || height == 0)
			throw new IllegalStateException();

		FullLand l2 = null;
		
		for (FullLand l : world.getFullLands()) {
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
		t.stop();
		synchronized (flag3) {
			eraseArrow();
			flag3.notifyAll();
		}
	}
	
	public void eraseArrow(){
		synchronized (flag4) {
			this.points = null;
			this.arrowState = 0;
			this.arrowDoubleBuffer = EMPTY_ARROW_DOUBLE_BUFFERED_IMAGE;
		}
		repaint();
	}
	
	public void drawArrow(Integer fromLandId, Integer toLandId){
		if (world == null || width == 0 || height == 0)
			throw new IllegalStateException();
		
		synchronized (flag4) {
			this.points = null;
			this.arrowState = 0;
			this.arrowDoubleBuffer = null;

			Set<FullLand> landsTmp;

			synchronized (flag) {
				landsTmp = new HashSet<FullLand>(world.getFullLands());
			}

			Point p1 = null;
			Point p2 = null;
			final int arrowFromLandId = fromLandId.intValue();
			final int arrowToLandId = toLandId.intValue();
			for (FullLand land : landsTmp) {
				ColoredLand l = landFactory.getLand(land.getLandId(), land
						.getFlag());
				if (land.getLandId() == arrowFromLandId) {
					p1 = l.center;
				} else if (land.getLandId() == arrowToLandId) {
					p2 = l.center;
				}
			}

			Arrow arrow = ArrowFactory.getArrow(ArrowType.BEZIER);
			arrow.setCoordinates(p1.x, p1.y, p2.x, p2.y);

			this.points = ((BezierArrow) arrow).getAllPoints();
		}
		t.setDelay(500 / this.points.size());
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
		if (world == null || width == 0 || height == 0)
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
				BufferedImage backgroundImage = landFactory.getBackground();
				if (backgroundImage != null) {
					g2d.drawImage(backgroundImage, X_OFFSET - 29, Y_OFFSET - 26, backgroundImage.getWidth(), backgroundImage.getHeight(), this);
				}

				/*
				 * Draw lands
				 */
				int offsetX = 2;
				int offsetY = 5;

				for (FullLand land : landsTmp) {
					boolean battle = land.getLandId() == defendingLandId || land.getLandId() == attackingLandId;
					ColoredLand l = landFactory.getLand(land.getLandId(), land.getFlag());
					if (l != null) {
						if (!battle) {
							g2d.drawImage(l.image, l.x+X_OFFSET, l.y+Y_OFFSET, l.size.width, l.size.height, this);
						} else {
							BufferedImage doubleBuffer2 = new BufferedImage(l.size.width, l.size.height, BufferedImage.TYPE_INT_ARGB);
							Graphics2D gd = (Graphics2D) doubleBuffer2.getGraphics();
							gd.setComposite(alphaComposite); 
							gd.drawImage(l.image, 0, 0, l.size.width, l.size.height, this);
							g2d.drawImage(doubleBuffer2, l.x - offsetX + X_OFFSET, l.y - offsetY + Y_OFFSET, l.size.width, l.size.height, this);
							gd.dispose();
						}
					}
				}			
			
				/*
				 * Draw dices and land id on the map
				 */
				for (FullLand land : landsTmp) {
					ColoredLand l = landFactory.getLand(land.getLandId(), land.getFlag());
					if (l != null) {
						// Displaying dices
						Image diceImage = ImageManager.getDice(land.getDiceCount(), land.getFlag());
						if (diceImage != null){
							g2d.drawImage(diceImage, l.center.x + DICE_X_OFFSET + X_OFFSET, l.center.y + DICE_Y_OFFSET + Y_OFFSET, this);
						}

						// Displaying land ids
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(landIdFont);
                        g2d.drawString(String.valueOf(land.getLandId()), (int)l.center.x + X_OFFSET + 10, (int)l.center.y + Y_OFFSET + 8);
					}
				}
				
				g2d.dispose();
			}
			g.drawImage(this.doubleBuffer, 0, 0, this.doubleBuffer.getWidth(), this.doubleBuffer.getHeight(), this);
		}
		
		synchronized (flag4) {
			if (arrowDoubleBuffer == null){
				/*
				 * Draw bezier arrow
				 */
				if (this.points != null && this.arrowState <= this.points.size() - 1 && this.arrowState > 0 && this.points.size() > 1) {
					Point p1 = this.points.get(0);
					Point p2 = this.points.get(this.arrowState);

					Arrow arrow = ArrowFactory.getArrow(ArrowType.BEZIER);
					
					int minx = Math.min(p1.x, p2.x);
					int miny = Math.min(p1.y, p2.y);
					
					arrow.setCoordinates(p1.x - minx + 25, p1.y - miny + 25, p2.x - minx + 25, p2.y - miny + 25);

					this.arrowDoubleBuffer = new BufferedImage(Math.abs(p2.x - p1.x) + 50, Math.abs(p2.y - p1.y) + 50, BufferedImage.TYPE_INT_ARGB);
					this.arrowDoubleBufferOffsetX = minx - 25;
					this.arrowDoubleBufferOffsetY = miny - 25;
					
					Graphics2D g2d = (Graphics2D) arrowDoubleBuffer.getGraphics();
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					arrow.paint(g2d);
					g2d.dispose();
				}else{
					this.arrowDoubleBuffer = EMPTY_ARROW_DOUBLE_BUFFERED_IMAGE;
					this.arrowDoubleBufferOffsetX = 0;
					this.arrowDoubleBufferOffsetY = 0;
				}
			}
			g.drawImage(this.arrowDoubleBuffer, this.arrowDoubleBufferOffsetX + X_OFFSET, this.arrowDoubleBufferOffsetY+Y_OFFSET, this.arrowDoubleBuffer.getWidth(), this.arrowDoubleBuffer.getHeight(), this);
		}
		
	    g.dispose();
	}
}
