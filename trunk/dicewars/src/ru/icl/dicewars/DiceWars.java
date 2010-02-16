package ru.icl.dicewars;

import java.awt.BorderLayout;
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

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import ru.icl.dicewars.gui.TopMenuMenuBar;
import ru.icl.dicewars.gui.World;
import ru.icl.dicewars.gui.manager.WindowManager;
import ru.icl.dicewars.gui.thread.UIGameThread;
import ru.icl.dicewars.gui.util.ImageUtil;

public class DiceWars extends JFrame {

	static DiceWars dicewars = null;
	static final long serialVersionUID = -6592937624280635427L;
	
	int screenWidth;
    int screenHeight;
    
    JPanel contentPane;
    JLayeredPane jLayeredPane;
    JMenuBar jMenuBar;
    
    ComponentAdapter resizeListener;
    
    UIGameThread uiGameThread;
    
    Image diceIconImage = null;
    
	WindowListener windowListener = new WindowAdapter() {
		public void windowClosing(WindowEvent w) {
			close();
		}
	};
	
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
		this.setVisible(false);
		this.dispose();
	}
	
	private void startGame(){
		uiGameThread = new UIGameThread(); 
		uiGameThread.start();
	}
	
	public void startNewGame(){
		stopGame();
		startGame();
	}
    
	private Image getDiceIconImage(){
		if (diceIconImage == null){
			String path = "/resources/icon/dice.png";
			diceIconImage = ImageUtil.getImage(path);
		}
		return diceIconImage;
	}
	
	public DiceWars() {
		Rectangle scrnRect = getGraphicsConfiguration().getBounds();
        screenWidth = scrnRect.width;
        screenHeight = scrnRect.height;
        setSize(screenWidth, screenHeight);
        WindowManager.getManager().setMainFrame(this);
        WindowManager.getManager().setScreenWidth(screenWidth);
        WindowManager.getManager().setScreenHeight(screenHeight);
        
        /*GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        setUndecorated(true);
        device.setFullScreenWindow(this);*/
        
        setTitle("DiceWars (Version 0.0.1)");
        
        this.setIconImage(getDiceIconImage());
        
        setResizable(true);
        
        jLayeredPane = new JLayeredPane();
        setLayeredPane(jLayeredPane);
        WindowManager.getManager().setJLayeredPane(jLayeredPane);
        
        jMenuBar = new TopMenuMenuBar();
        setJMenuBar(jMenuBar);
        
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        
        setVisible(true);
        
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(laf.getName())) {
					UIManager.setLookAndFeel(laf.getClassName());
				}
			}
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
        }
        
        World world = WindowManager.getManager().getWorld();
        world.setPreferredSize(new Dimension(1500,1200));
        final JScrollPane scroll = WindowManager.getManager().getScrollPane(world);
        scroll.setBounds(20, 30, screenWidth - 240, screenHeight - 120);
        jLayeredPane.add(scroll, 0);
        
        final JPanel infoPanel = WindowManager.getManager().getInfoPanel();
        infoPanel.setBounds(screenWidth - 210, 30, 190, screenHeight - 120);
        jLayeredPane.add(infoPanel, 0);
        
        resizeListener = new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e) {
				screenWidth = ((DiceWars)e.getSource()).getSize().width;
				screenHeight = ((DiceWars)e.getSource()).getSize().height;
				WindowManager.getManager().setScreenWidth(screenWidth);
		        WindowManager.getManager().setScreenHeight(screenHeight);
		        // Implement resizing here if needed
		        scroll.setBounds(20, 30, screenWidth - 240, screenHeight - 120);
		        scroll.revalidate();
		        infoPanel.setBounds(screenWidth - 210, 30, 190, screenHeight - 120);
		        infoPanel.revalidate();
			}
        };
        addComponentListener(resizeListener);
        
        AdjustmentListener scrollListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				WindowManager.getManager().freeze();
			}
		};
		scroll.getHorizontalScrollBar().addAdjustmentListener(scrollListener);
		scroll.getVerticalScrollBar().addAdjustmentListener(scrollListener);
        
		//FIXME: why is it here?
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(windowListener);
		
	}
	
	private static void createAndShowGUI() {
		dicewars = new DiceWars();
		dicewars.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
	
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
