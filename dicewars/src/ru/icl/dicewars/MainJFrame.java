package ru.icl.dicewars;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.MatteBorder;

import ru.icl.dicewars.core.Configuration;
import ru.icl.dicewars.core.ConfigurationLoader;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.RealFullWorldGeneratorImpl;
import ru.icl.dicewars.core.SimpleConfigurationImpl;
import ru.icl.dicewars.gui.BottomInfoJPanel;
import ru.icl.dicewars.gui.InfoJPanel;
import ru.icl.dicewars.gui.PlayersJDialog;
import ru.icl.dicewars.gui.TopMenuMenuBar;
import ru.icl.dicewars.gui.WorldJPanel;
import ru.icl.dicewars.gui.component.Command;
import ru.icl.dicewars.gui.component.HoverButton;
import ru.icl.dicewars.gui.manager.ImageManager;
import ru.icl.dicewars.gui.manager.WindowManager;
import ru.icl.dicewars.gui.thread.UIGameThread;

public final class MainJFrame extends JFrame {
	private static final long serialVersionUID = -6592937624280635427L;
	
	private static final int MAX_PLAYER_COUNT = 8;
	
	private final JPanel contentPane;
    private final JLayeredPane jLayeredPane;
    private final JMenuBar jMenuBar;
    private UIGameThread uiGameThread;
    
    private final HoverButton pauseSpeed;
    private final HoverButton playSpeed;
    private final HoverButton fastForwardSpeed;
    private final HoverButton forwardSpeed;
    
    private final Command pauseSpeedCommand = new Command() {
        @Override
        public void execute() {
        	pauseSpeed.setSelected(true);
        	pauseSpeed.repaint();
        	playSpeed.setSelected(false);
        	playSpeed.repaint();
        	forwardSpeed.setSelected(false);
        	forwardSpeed.repaint();
        	fastForwardSpeed.setSelected(false);
        	fastForwardSpeed.repaint();
        	if (uiGameThread != null){
        		uiGameThread.setSpeed(-1);
        	}
        }
        private static final long serialVersionUID = 1L;
    };        

    private final Command playSpeedCommand = new Command() {
        @Override
        public void execute() {
        	pauseSpeed.setSelected(false);
        	pauseSpeed.repaint();
        	playSpeed.setSelected(true);
        	playSpeed.repaint();
        	forwardSpeed.setSelected(false);
        	forwardSpeed.repaint();
        	fastForwardSpeed.setSelected(false);
        	fastForwardSpeed.repaint();
        	if (uiGameThread != null){
        		uiGameThread.setSpeed(1);
        	}
        }
        private static final long serialVersionUID = 1L;
    };        
    
    private final Command forwardSpeedCommand = new Command() {
        @Override
        public void execute() {
        	pauseSpeed.setSelected(false);
        	pauseSpeed.repaint();
        	playSpeed.setSelected(false);
        	playSpeed.repaint();
        	forwardSpeed.setSelected(true);
        	forwardSpeed.repaint();
        	fastForwardSpeed.setSelected(false);
        	fastForwardSpeed.repaint();
        	if (uiGameThread != null){
        		uiGameThread.setSpeed(2);
        	}
        }
        private static final long serialVersionUID = 1L;
    };
    
    private final Command fastForwardSpeedCommand = new Command() {
        @Override
        public void execute() {
        	pauseSpeed.setSelected(false);
        	pauseSpeed.repaint();
        	fastForwardSpeed.setSelected(true);
        	fastForwardSpeed.repaint();
        	playSpeed.setSelected(false);
        	playSpeed.repaint();
        	forwardSpeed.setSelected(false);
        	forwardSpeed.repaint();
        	if (uiGameThread != null){
        		uiGameThread.setSpeed(0);
        	}
        }
        private static final long serialVersionUID = 1L;
    };
    
    private final ComponentAdapter resizeListener = new ComponentAdapter(){
		@Override
		public void componentResized(ComponentEvent e) {
			final JScrollPane jScrollPane = WindowManager.getInstance().getJScrollPane();
			jScrollPane.setBounds(10, 30, getWidth() - 240, getHeight() - 120);
			jScrollPane.revalidate();
			
			final InfoJPanel infoJPanel = WindowManager.getInstance().getInfoJPanel();
	        infoJPanel.setBounds(getWidth() - 220, 30, 200, getHeight() - 70);
	        infoJPanel.revalidate();
	        
	        final BottomInfoJPanel bottomInfoJPanel = WindowManager.getInstance().getBottomInfoJPanel();
	        bottomInfoJPanel.setBounds(10, getHeight() - 120 + 30 + 5, getWidth() - 240, 45);
	        
	        final WorldJPanel worldJPanel = WindowManager.getInstance().getWorldJPanel();
	        final int w = getWidth() - 250; 
	        worldJPanel.setPreferredSize(new Dimension(w, WorldJPanel.MAIN_IMAGE_HEIGHT * w/WorldJPanel.MAIN_IMAGE_WIDTH));
	        worldJPanel.revalidate();
	        worldJPanel.clearBuffers();
	        
	        Rectangle buttonSize = new Rectangle(32, 32);
	        int x = (getWidth() - 220) / 2 - buttonSize.width * 2 - 12 - 24;
	        int y = (int)(getHeight() - 150);
	        x += buttonSize.width + 24; 
	        pauseSpeed.setLocation(x, y);
	        pauseSpeed.repaint();
	        x += buttonSize.width + 24;
	        playSpeed.setLocation(x, y);
	        playSpeed.repaint();
	        x += buttonSize.width + 24; 
	        forwardSpeed.setLocation(x, y);
	        forwardSpeed.repaint();
	        x += buttonSize.width + 24; 
	        fastForwardSpeed.setLocation(x, y);
	        fastForwardSpeed.repaint();
		}
    };
    
    /*AdjustmentListener scrollListener = new AdjustmentListener() {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			if (uiGameThread != null){
				uiGameThread.freeze();
			}
		}
	};*/
	
    private final WindowListener windowListener = new WindowAdapter() {
		public void windowClosing(WindowEvent w) {
			close();
		}
	};
	
	private void speedButtonSetVisible(boolean visible){
		pauseSpeed.setVisible(visible);
		playSpeed.setVisible(visible);
		forwardSpeed.setVisible(visible);
		fastForwardSpeed.setVisible(visible);
	}
	
	public void stopGame(){
		while (uiGameThread != null && uiGameThread.isAlive()){
			uiGameThread.kill();
			try{
				Thread.sleep(10);
			}catch (InterruptedException e) {
			}
		}
		
		WindowManager.getInstance().getWorldJPanel().updateWorld(null);
		WindowManager.getInstance().getInfoJPanel().clearPlayers();
		
		speedButtonSetVisible(false);
	}
	
	public void notifyThatGameIsEnded(){
		speedButtonSetVisible(false);
	}
	
	public void close(){
		stopGame();

		PlayersJDialog playersJDialog = WindowManager.getInstance().getPlayersJDialog();
		playersJDialog.setVisible(false);
		playersJDialog.dispose();
		
		setVisible(false);
		dispose();
	}
	
	private void startGame(Configuration configuration){
		uiGameThread = new UIGameThread(configuration); 
		uiGameThread.start();
		pauseSpeed.setSelected(false);
    	pauseSpeed.repaint();
    	playSpeed.setSelected(true);
    	playSpeed.repaint();
    	forwardSpeed.setSelected(false);
    	forwardSpeed.repaint();
    	fastForwardSpeed.setSelected(false);
    	fastForwardSpeed.repaint();
    	speedButtonSetVisible(true);
	}
	
	public void startNewGame(){
		stopGame();
		ConfigurationLoader configurationLoader = ConfigurationLoader.getInstance();
		synchronized (ConfigurationLoader.getInstance()) {
			configurationLoader.load();
			int playersCount = configurationLoader.getPlayerClasses().length;

			if (playersCount > MAX_PLAYER_COUNT || playersCount < 2) {
				JOptionPane
						.showMessageDialog(
								this,
								"Game settings are invalid. Please, configure you settings. Choose 2-8 players to play against each other.",
								"Settings are invalid",
								JOptionPane.WARNING_MESSAGE, ImageManager
										.getWarningIcon());
			} else {
				RealFullWorldGeneratorImpl realFullWorldGeneratorImpl = new RealFullWorldGeneratorImpl();
				realFullWorldGeneratorImpl.setPlayersCount(playersCount);
				FullWorld fullWorld = realFullWorldGeneratorImpl.generate();
				Configuration configuration = new SimpleConfigurationImpl(fullWorld, configurationLoader.getPlayerClasses(),
						configurationLoader.getMaxDiceCountInReserve(), configurationLoader.getClassLoader());
				startGame(configuration);
			}
		}
	}
    
	public MainJFrame() {
		setExtendedState(MAXIMIZED_BOTH);
		
        setMinimumSize(new Dimension(1000,600));
        
        setTitle("DiceWars");
        
        setIconImage(ImageManager.getDiceIconImage());
        
        setResizable(true);
        
        setVisible(true);
        
        jLayeredPane = WindowManager.getInstance().getJLayeredPane();
        setLayeredPane(jLayeredPane);
        
        jMenuBar = new TopMenuMenuBar();
        setJMenuBar(jMenuBar);
        
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        
        final WorldJPanel world = WindowManager.getInstance().getWorldJPanel();
        world.setPreferredSize(new Dimension(2400,2400));
        world.setBorder(BorderFactory.createEtchedBorder());
        
        final JScrollPane jScrollPane = WindowManager.getInstance().getJScrollPane();
        jScrollPane.setBounds(10, 30, getWidth() - 240, getHeight() - 70);
        jScrollPane.setBorder(new MatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
        jScrollPane.setOpaque(false);
		jScrollPane.getViewport().setOpaque(false);
		jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 0, 0)));
		//jScrollPane.getHorizontalScrollBar().addAdjustmentListener(scrollListener);
		//jScrollPane.getVerticalScrollBar().addAdjustmentListener(scrollListener);
        
		jLayeredPane.add(jScrollPane, 0);
        
        final JPanel infoJPanel = WindowManager.getInstance().getInfoJPanel();
        infoJPanel.setBounds(getWidth() - 220, 30, 200, getHeight() - 70);
        infoJPanel.setBorder(new MatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
        jLayeredPane.add(infoJPanel, 0);
        
        BottomInfoJPanel bottomInfoJPanel = WindowManager.getInstance().getBottomInfoJPanel();
        bottomInfoJPanel.setBounds(10, getHeight() - 120 + 30 + 5, getWidth() - 240, 45);
        bottomInfoJPanel.setBorder(new MatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
        jLayeredPane.add(bottomInfoJPanel, 0);

        addComponentListener(resizeListener);
		
        Rectangle buttonSize = new Rectangle(32, 32);
        Rectangle imageSize = new Rectangle(32, 32);
        
        pauseSpeed = new HoverButton("",
        		ImageManager.getPauseSpeedImage(),
        		ImageManager.getPauseSpeedImageSelected(),
        		ImageManager.getPauseSpeedImageHovered(),
        		ImageManager.getPauseSpeedImageHoveredSelected(),
        		ImageManager.getPauseSpeedImage(),
                imageSize);
        int x = (getWidth() - 220) / 2 - buttonSize.width * 2 - 12 - 24;
        int y = (int)(getHeight() - 150);
        pauseSpeed.setBounds(new Rectangle(x, y, buttonSize.width, buttonSize.height));
        pauseSpeed.setEnabled(true);
        pauseSpeed.setObserver(pauseSpeedCommand);
        jLayeredPane.add(pauseSpeed, JLayeredPane.MODAL_LAYER);
        
        playSpeed = new HoverButton("",
        		ImageManager.getPlaySpeedImage(),
        		ImageManager.getPlaySpeedImageSelected(),
        		ImageManager.getPlaySpeedImageHovered(),
        		ImageManager.getPlaySpeedImageHoveredSelected(),
        		ImageManager.getPlaySpeedImage(),
                imageSize);
        jLayeredPane.add(playSpeed, JLayeredPane.MODAL_LAYER);
        x = x + buttonSize.width + 24; 
        playSpeed.setBounds(new Rectangle(x, y, buttonSize.width, buttonSize.height));
        playSpeed.setEnabled(true);
        playSpeed.setSelected(true);
        playSpeed.setObserver(playSpeedCommand);
              
        forwardSpeed = new HoverButton("",
        		ImageManager.getForwardSpeedImage(),
        		ImageManager.getForwardSpeedImageSelected(),
        		ImageManager.getForwardSpeedImageHovered(),
        		ImageManager.getForwardSpeedImageHoveredSelected(),
        		ImageManager.getForwardSpeedImage(),
                imageSize);
        jLayeredPane.add(forwardSpeed, JLayeredPane.MODAL_LAYER);
        x = x + buttonSize.width + 24;
        forwardSpeed.setBounds(new Rectangle(x, y, buttonSize.width, buttonSize.height));
        forwardSpeed.setEnabled(true);
        forwardSpeed.setObserver(forwardSpeedCommand);
        
        fastForwardSpeed = new HoverButton("",
        		ImageManager.getFastForwardSpeedImage(),
        		ImageManager.getFastForwardSpeedImageSelected(),
        		ImageManager.getFastForwardSpeedImageHovered(),
        		ImageManager.getFastForwardSpeedImageHoveredSelected(),
        		ImageManager.getFastForwardSpeedImage(),
                imageSize);
        jLayeredPane.add(fastForwardSpeed, JLayeredPane.MODAL_LAYER);
        x = x + buttonSize.width + 24;
        fastForwardSpeed.setBounds(new Rectangle(x, y, buttonSize.width, buttonSize.height));
        fastForwardSpeed.setEnabled(true);
        fastForwardSpeed.setObserver(fastForwardSpeedCommand);
        
        speedButtonSetVisible(false);
        
		//Because windows is closed by event.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(windowListener);
	}
	
	private static void createAndShowGUI() {
		MainJFrame mainJFrame = WindowManager.getInstance().getMainFrame();
		PlayersJDialog playersJDialog = WindowManager.getInstance().getPlayersJDialog();
		playersJDialog.setLocationRelativeTo(mainJFrame);
		
		try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(laf.getName())) {
					UIManager.setLookAndFeel(laf.getClassName());
				}
			}
            SwingUtilities.updateComponentTreeUI(mainJFrame);
            SwingUtilities.updateComponentTreeUI(playersJDialog);
        } catch (Exception e) {
        }

    }
	
    public static void main(String[] args) {
    	Logger.init();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    /*Work around*/
    /*public int getSpeed() {
    	if (uiGameThread != null){
    		return uiGameThread.getSpeed();
    	}
		return 0;
	}*/
}
