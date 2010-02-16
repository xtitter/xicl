package ru.icl.dicewars.gui.manager;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import ru.icl.dicewars.DiceWars;
import ru.icl.dicewars.gui.InfoPanel;
import ru.icl.dicewars.gui.World;

public class WindowManager {
	
	private static WindowManager windowManager = null;
	//private int screenWidth;
    //private int screenHeight;
	
    private World world;
	private InfoPanel infoPanel;
	
	private Object sync = new Object();
	private boolean frozen = false;
	
	private Object jLayeredPaneFlag = new Object();
	private Object worldFlag = new Object();
	private Object infoPanelFlag = new Object();
	private Object mainFrameFlag = new Object();
	private static Object windowManagerFlag = new Object();
	
	public static WindowManager getManager() {
		if (windowManager == null) {
			synchronized (windowManagerFlag) {
				if (windowManager == null) {
					windowManager = new WindowManager();
				}
			}
		}
		return windowManager;
	}
	
	public World getWorld() {
		if (world == null) {
			synchronized(worldFlag){
				if (world == null){
					world = new World();
					world.setBorder(BorderFactory.createEtchedBorder());
				}
			}
		}
		return world;
	}
	
	public JScrollPane getScrollPane(JComponent component) {
		JScrollPane scrollPane = new JScrollPane(component);

		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 0, 0)));

		return scrollPane;
	}
	
	public InfoPanel getInfoPanel() {
		if (infoPanel == null) {
			synchronized (infoPanelFlag) {
				if (infoPanel == null) {
					infoPanel = new InfoPanel();
					//infoPanel.setBorder(BorderFactory.createLineBorder(Color.black));
				}
			}
		}
		return infoPanel;
	}
	
	public JLayeredPane getJLayeredPane() {
		if (jLayeredPane == null) {
			synchronized (jLayeredPaneFlag) {
				if (jLayeredPane == null) {
					jLayeredPane = new JLayeredPane();
				}
			}
		}
		return jLayeredPane;
	}
	
	/*public void setMainFrame(DiceWars mainFrame) {
		this.mainFrame = mainFrame;
	}*/
	
	public DiceWars getMainFrame() {
		if (mainFrame == null){
			synchronized (mainFrameFlag) {
				if (mainFrame == null){
					mainFrame = new DiceWars();
				}
			}
		}
		return mainFrame;
	}
	
	/*public Arrow getArrow(SimplePlayerAttackActivity pa, ArrowFactory.ArrowType type) {
		Arrow arrow = ArrowFactory.getArrow(0, type);
		arrow.setVisible(true);
		arrow.setOpaque(false);
		arrow.setBounds(0, 0, this.screenWidth, this.screenHeight);
		
		FullWorld world = getWorld().getRecentWorld();
		FullLand first = null;
		FullLand second = null;
		for (FullLand l : world.getFullLands()) {
			if (l.getLandId() == pa.getFromLandId()) {
				first = l;
				if (second != null) break;
			}
			if (l.getLandId() == pa.getToLandId()) {
				second = l;
				if (first != null) break;
			}
		}
		if (first != null && second != null) {
			int x1 = 0;
			int y1 = 0;
			int correction = 4;
			//TODO: move this code and from World to wrapper
			int rowOffset = 0;
			for (Point p : first.getPoints()) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = World.X_OFFSET + p.getX()*19 + rowOffset;
				int _y = World.Y_OFFSET + p.getY()*(20 - correction);
				x1 += _x;
				y1 += _y;
			}
			int size = first.getPoints().size();
			if (size > 0) {
				x1 /= size;
				y1 /= size;
			}
			
			int x2 = 0;
			int y2 = 0;
			for (Point p : second.getPoints()) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = World.X_OFFSET + p.getX()*19 + rowOffset;
				int _y = World.Y_OFFSET + p.getY()*(20 - correction);
				x2 += _x;
				y2 += _y;
			}
			size = second.getPoints().size();
			if (size > 0) {
				x2 /= size;
				y2 /= size;
			}
			
			arrow.setCoordinates(x1 + 30, y1, x2 + 30, y2);
		} else { // shouldn't happen. if it did, then some bug in our game engine
			return null;
		}
		return arrow;
	}*/
	
	public int getScreenWidth() {
		//return screenWidth;
		return getMainFrame().getSize().width;
	}

	/*public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}*/

	public int getScreenHeight() {
		//return screenHeight;
		return getMainFrame().getSize().height;
	}

	/*public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}*/
	
	public void freeze() {
		synchronized (sync) {
			this.frozen = true;
		}
	}
	
	public void fire() {
		synchronized (sync) {
			this.frozen = false;
		}
	}
	
	public boolean isFrozen() {
		synchronized (sync) {
			return this.frozen;
		}
	}
	
	private JLayeredPane jLayeredPane;
	
	private DiceWars mainFrame;
}
