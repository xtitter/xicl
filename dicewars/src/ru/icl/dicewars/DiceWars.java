package ru.icl.dicewars;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

import org.apache.log4j.PropertyConfigurator;

import ru.icl.dicewars.gui.TopMenu;
import ru.icl.dicewars.gui.World;
import ru.icl.dicewars.gui.manager.WindowManager;
import ru.icl.dicewars.gui.thread.UIGameThread;

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
    
	WindowListener windowListener = new WindowAdapter() {
		public void windowClosing(WindowEvent w) {
			close();
		}
	};
	
	public void close(){
		while (uiGameThread.isAlive()){
			uiGameThread.kill();
			try{
				Thread.sleep(10);
			}catch (InterruptedException e) {
			}
		}
		this.setVisible(false);
		this.dispose();
	}
    
	public DiceWars() {
		PropertyConfigurator.configure("log4j.properties");
		
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
        setResizable(true);
        
        jLayeredPane = new JLayeredPane();
        setLayeredPane(jLayeredPane);
        WindowManager.getManager().setJLayeredPane(jLayeredPane);
        
        jMenuBar = new TopMenu();
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
        //world.setBounds(20, 10, screenWidth - 40, screenHeight - 150);
        final JScrollPane scroll = WindowManager.getManager().getScrollPane(world);
        scroll.setBounds(20, 30, screenWidth - 40, screenHeight - 150);
        jLayeredPane.add(scroll, 0);
        //jLayeredPane.add(world, 0);
        
        resizeListener = new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e) {
				screenWidth = ((DiceWars)e.getSource()).getSize().width;
				screenHeight = ((DiceWars)e.getSource()).getSize().height;
				WindowManager.getManager().setScreenWidth(screenWidth);
		        WindowManager.getManager().setScreenHeight(screenHeight);
		        // Implement resizing here if needed
		        scroll.setBounds(20, 30, screenWidth - 40, screenHeight - 150);
		        scroll.revalidate();
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
        
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(windowListener);
		
		uiGameThread = new UIGameThread(); 
		
		uiGameThread.start();
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
