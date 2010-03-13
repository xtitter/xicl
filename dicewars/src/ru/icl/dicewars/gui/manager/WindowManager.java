package ru.icl.dicewars.gui.manager;

import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;

import ru.icl.dicewars.MainJFrame;
import ru.icl.dicewars.gui.BottomInfoJPanel;
import ru.icl.dicewars.gui.InfoJPanel;
import ru.icl.dicewars.gui.PlayersJDialog;
import ru.icl.dicewars.gui.WorldJPanel;

public class WindowManager {
	
	private static WindowManager windowManager = null;
	private static final Object windowManagerFlag = new Object();
	
    private WorldJPanel worldJPanel;
	private InfoJPanel infoJPanel;
	private JLayeredPane jLayeredPane;
	private MainJFrame mainJFrame;
	private PlayersJDialog playersJDialog;
	private JScrollPane jScrollPane;
	private BottomInfoJPanel bottomInfoJPanel;

	private final Object jLayeredPaneFlag = new Object();
	private final Object worldJPanelFlag = new Object();
	private final Object infoJPanelFlag = new Object();
	private final Object mainJFrameFlag = new Object();
	private final Object playersJDialogFlag = new Object();
	private final Object jScrollPaneFlag = new Object();
	private final Object bottomInfoJPanelFlag = new Object();
	
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
	
	public PlayersJDialog getPlayersJDialog(){
		if (playersJDialog == null){
			synchronized (playersJDialogFlag) {
				if (playersJDialog == null){
					playersJDialog = new PlayersJDialog(getMainFrame());
				}
			}
		}
		return playersJDialog;
	}
	
	public int getScreenWidth() {
		return getMainFrame().getSize().width;
	}

	public int getScreenHeight() {
		return getMainFrame().getSize().height;
	}

	public BottomInfoJPanel getBottomInfoJPanel() {
		if (bottomInfoJPanel == null){
			synchronized (bottomInfoJPanelFlag) {
				if (bottomInfoJPanel == null){
					bottomInfoJPanel = new BottomInfoJPanel();
				}
			}
		}
		return bottomInfoJPanel;
	}

}
