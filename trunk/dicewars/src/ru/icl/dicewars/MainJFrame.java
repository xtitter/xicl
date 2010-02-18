package ru.icl.dicewars;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.MatteBorder;

import ru.icl.dicewars.gui.InfoJPanel;
import ru.icl.dicewars.gui.PlayersJFrame;
import ru.icl.dicewars.gui.TopMenuMenuBar;
import ru.icl.dicewars.gui.WorldJPanel;
import ru.icl.dicewars.gui.component.Command;
import ru.icl.dicewars.gui.component.HoverButton;
import ru.icl.dicewars.gui.manager.ImageManager;
import ru.icl.dicewars.gui.manager.WindowManager;
import ru.icl.dicewars.gui.thread.UIGameThread;
import ru.icl.dicewars.gui.util.ImageUtil;

public class MainJFrame extends JFrame {
	static final long serialVersionUID = -6592937624280635427L;
	
    JPanel contentPane;
    JLayeredPane jLayeredPane;
    JMenuBar jMenuBar;
    UIGameThread uiGameThread;
    
    private HoverButton normalSpeed;
    private HoverButton fastSpeed;
    private HoverButton inatickSpeed;
    
    Image diceIconImage;
    
    ComponentAdapter resizeListener = new ComponentAdapter(){
		@Override
		public void componentResized(ComponentEvent e) {
			final int screenWidth = WindowManager.getInstance().getScreenWidth();
			final int screenHeight = WindowManager.getInstance().getScreenHeight();
			
			final JScrollPane jScrollPane = WindowManager.getInstance().getJScrollPane();
			jScrollPane.setBounds(10, 30, screenWidth - 240, screenHeight - 120);
			jScrollPane.revalidate();
			
			final InfoJPanel infoJPanel = WindowManager.getInstance().getInfoJPanel();
	        infoJPanel.setBounds(screenWidth - 220, 30, 200, screenHeight - 120);
	        infoJPanel.revalidate();
		}
    };
    
    AdjustmentListener scrollListener = new AdjustmentListener() {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			WindowManager.getInstance().freeze();
		}
	};
	
	WindowListener windowListener = new WindowAdapter() {
		public void windowClosing(WindowEvent w) {
			close();
		}
	};
	
	Image getDiceIconImage(){
		if (diceIconImage == null){
			String path = "/resources/icon/dice.png";
			diceIconImage = ImageUtil.getImage(path);
		}
		return diceIconImage;
	}
	
	public void stopGame(){
		while (uiGameThread != null && uiGameThread.isAlive()){
			uiGameThread.kill();
			try{
				Thread.sleep(10);
			}catch (InterruptedException e) {
			}
		}
	}
	
	public void close(){
		stopGame();
		setVisible(false);
		PlayersJFrame playersJFrame = WindowManager.getInstance().getPlayersJFrame();
		playersJFrame.setVisible(false);
		playersJFrame.dispose();
		dispose();
	}
	
	private void startGame(){
		uiGameThread = new UIGameThread(); 
		uiGameThread.start();
	}
	
	public void startNewGame(){
		stopGame();
		startGame();
	}
    
	public MainJFrame() {
		Rectangle scrnRect = getGraphicsConfiguration().getBounds();
        setSize(scrnRect.width, scrnRect.height);
        
        /*GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        setUndecorated(true);
        device.setFullScreenWindow(this);*/
        
        setTitle("DiceWars (Version 0.0.2)");
        
        setIconImage(getDiceIconImage());
        
        setResizable(true);
        
        setVisible(true);
        
        jLayeredPane = WindowManager.getInstance().getJLayeredPane();
        setLayeredPane(jLayeredPane);
        
        jMenuBar = new TopMenuMenuBar();
        setJMenuBar(jMenuBar);
        
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        
        final WorldJPanel world = WindowManager.getInstance().getWorldJPanel();
        world.setPreferredSize(new Dimension(1350,930));
        world.setBorder(BorderFactory.createEtchedBorder());
        
        final JScrollPane jScrollPane = WindowManager.getInstance().getJScrollPane();
        jScrollPane.setBounds(10, 30, scrnRect.width - 240, scrnRect.height - 120);
        jScrollPane.setBorder(new MatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
        jScrollPane.setOpaque(false);
		jScrollPane.getViewport().setOpaque(false);
		jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 0, 0)));
		jScrollPane.getHorizontalScrollBar().addAdjustmentListener(scrollListener);
		jScrollPane.getVerticalScrollBar().addAdjustmentListener(scrollListener);
        
		jLayeredPane.add(jScrollPane, 0);
        
        final JPanel infoJPanel = WindowManager.getInstance().getInfoJPanel();
        infoJPanel.setBounds(scrnRect.width - 220, 30, 200, scrnRect.height - 120);
        infoJPanel.setBorder(new MatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
        jLayeredPane.add(infoJPanel, 0);

        addComponentListener(resizeListener);
		
		//Because windows is closed by event.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(windowListener);
		
        Rectangle buttonSize = new Rectangle(103, 59);
        Rectangle imageSize = new Rectangle(103, 59);
        normalSpeed = new HoverButton("",
        		ImageManager.getNormalSpeedImage(),
        		ImageManager.getNormalSpeedImageSelected(),
        		ImageManager.getNormalSpeedImageHovered(),
        		ImageManager.getNormalSpeedImageHoveredSelected(),
        		ImageManager.getNormalSpeedImage(),
                imageSize);
        jLayeredPane.add(normalSpeed, 0);
        int x = scrnRect.width / 2 - buttonSize.width * 2 - 50;
        int y = (int)(scrnRect.height - 150);
        normalSpeed.setBounds(new Rectangle(x, y, buttonSize.width, buttonSize.height));
        normalSpeed.setEnabled(true);
        normalSpeed.setVisible(true);
        normalSpeed.setSelected(true);
        normalSpeed.setObserver(new Command() {
            @Override
            public void execute() {
            	System.out.println("normal speed");
            	normalSpeed.setSelected(true);
            	normalSpeed.repaint();
            	fastSpeed.setSelected(false);
            	fastSpeed.repaint();
            	inatickSpeed.setSelected(false);
            	inatickSpeed.repaint();
            }
            private static final long serialVersionUID = 1L;
        });
        
        buttonSize = new Rectangle(103, 59);
        imageSize = new Rectangle(103, 59);
        fastSpeed = new HoverButton("",
        		ImageManager.getFastSpeedImage(),
        		ImageManager.getFastSpeedImageSelected(),
        		ImageManager.getFastSpeedImageHovered(),
        		ImageManager.getFastSpeedImageHoveredSelected(),
        		ImageManager.getFastSpeedImage(),
                imageSize);
        jLayeredPane.add(fastSpeed, 0);
        x = scrnRect.width / 2 - buttonSize.width * 2 + 75;
        y = (int)(scrnRect.height - 150);
        fastSpeed.setBounds(new Rectangle(x, y, buttonSize.width, buttonSize.height));
        fastSpeed.setEnabled(true);
        fastSpeed.setVisible(true);
        fastSpeed.setObserver(new Command() {
            @Override
            public void execute() {
            	System.out.println("fast speed");
            	fastSpeed.setSelected(true);
            	fastSpeed.repaint();
            	normalSpeed.setSelected(false);
            	normalSpeed.repaint();
            	inatickSpeed.setSelected(false);
            	inatickSpeed.repaint();
            }
            private static final long serialVersionUID = 1L;
        });
        
        buttonSize = new Rectangle(103, 59);
        imageSize = new Rectangle(103, 59);
        inatickSpeed = new HoverButton("",
        		ImageManager.getInatickSpeedImage(),
        		ImageManager.getInatickSpeedImageSelected(),
        		ImageManager.getInatickSpeedImageHovered(),
        		ImageManager.getInatickSpeedImageHoveredSelected(),
        		ImageManager.getInatickSpeedImage(),
                imageSize);
        jLayeredPane.add(inatickSpeed, 0);
        x = scrnRect.width / 2 - buttonSize.width * 2 + 185;
        y = (int)(scrnRect.height - 150);
        inatickSpeed.setBounds(new Rectangle(x, y, buttonSize.width, buttonSize.height));
        inatickSpeed.setEnabled(true);
        inatickSpeed.setVisible(true);
        inatickSpeed.setObserver(new Command() {
            @Override
            public void execute() {
            	System.out.println("very fast speed");
            	inatickSpeed.setSelected(true);
            	inatickSpeed.repaint();
            	normalSpeed.setSelected(false);
            	normalSpeed.repaint();
            	fastSpeed.setSelected(false);
            	fastSpeed.repaint();
            }
            private static final long serialVersionUID = 1L;
        });
	}
	
	private static void createAndShowGUI() {
		MainJFrame mainJFrame = WindowManager.getInstance().getMainFrame();
		PlayersJFrame playersJFrame = WindowManager.getInstance().getPlayersJFrame();
		playersJFrame.setLocationRelativeTo(mainJFrame);
		
		try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(laf.getName())) {
					UIManager.setLookAndFeel(laf.getClassName());
				}
			}
            SwingUtilities.updateComponentTreeUI(mainJFrame);
            SwingUtilities.updateComponentTreeUI(playersJFrame);
        } catch (Exception e) {
        }

    }
	
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
