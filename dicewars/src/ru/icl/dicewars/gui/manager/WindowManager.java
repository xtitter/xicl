package ru.icl.dicewars.gui.manager;

import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;

import ru.icl.dicewars.MainJFrame;
import ru.icl.dicewars.gui.InfoJPanel;
import ru.icl.dicewars.gui.PlayersJFrame;
import ru.icl.dicewars.gui.WorldJPanel;

public class WindowManager {
	
	private static WindowManager windowManager = null;
	private static final Object windowManagerFlag = new Object();
	
	//private int screenWidth;
    //private int screenHeight;
	
    private WorldJPanel worldJPanel;
	private InfoJPanel infoJPanel;
	private JLayeredPane jLayeredPane;
	private MainJFrame mainJFrame;
	private PlayersJFrame playersJFrame;
	private JScrollPane jScrollPane;

	/*private boolean frozen = false;
	
	private final Object sync = new Object();*/	
	private final Object jLayeredPaneFlag = new Object();
	private final Object worldJPanelFlag = new Object();
	private final Object infoJPanelFlag = new Object();
	private final Object mainJFrameFlag = new Object();
	private final Object playersJFrameFlag = new Object();
	private final Object jScrollPaneFlag = new Object();
	
	//This class should be instantiated.
	private WindowManager() {
	}
	
	public static WindowManager getInstance() {
		if (windowManager == null) {
			synchronized (windowManagerFlag) {
				if (windowManager == null) {
					windowManager = new WindowManager();
				}
			}
		}
		return windowManager;
	}
	
	public WorldJPanel getWorldJPanel() {
		if (worldJPanel == null) {
			synchronized(worldJPanelFlag){
				if (worldJPanel == null){
					worldJPanel = new WorldJPanel();
				}
			}
		}
		return worldJPanel;
	}
	
	public JScrollPane getJScrollPane() {
		if (jScrollPane == null){
			synchronized (jScrollPaneFlag) {
				jScrollPane = new JScrollPane(getWorldJPanel());
			}
		
		}
		return jScrollPane;
	}
	
	public InfoJPanel getInfoJPanel() {
		if (infoJPanel == null) {
			synchronized (infoJPanelFlag) {
				if (infoJPanel == null) {
					infoJPanel = new InfoJPanel();
				}
			}
		}
		return infoJPanel;
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
	
	public MainJFrame getMainFrame() {
		if (mainJFrame == null){
			synchronized (mainJFrameFlag) {
				if (mainJFrame == null){
					mainJFrame = new MainJFrame();
				}
			}
		}
		return mainJFrame;
	}
	
	public PlayersJFrame getPlayersJFrame(){
		if (playersJFrame == null){
			synchronized (playersJFrameFlag) {
				if (playersJFrame == null){
					playersJFrame = new PlayersJFrame();
				}
			}
		}
		return playersJFrame;
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
	
	/*public void freeze() {
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
	}*/
}
