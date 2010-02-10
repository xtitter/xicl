package ru.icl.dicewars;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.PropertyConfigurator;

import ru.icl.dicewars.gui.World;
import ru.icl.dicewars.gui.manager.WindowManager;
import ru.icl.dicewars.gui.thread.UIGameThread;

public class DiceWars extends JFrame {

	private static DiceWars dicewars = null;
	private static final long serialVersionUID = -6592937624280635427L;
	
    private int screenWidth;
    private int screenHeight;
    
    private JPanel contentPane;
    private JLayeredPane jLayeredPane;
    
    private ComponentAdapter resizeListener;
    
	public DiceWars() {
		PropertyConfigurator.configure("log4j.properties");
		
		Rectangle scrnRect = getGraphicsConfiguration().getBounds();
        screenWidth = scrnRect.width;
        screenHeight = scrnRect.height;
        setSize(screenWidth, screenHeight);
        
        /*GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        setUndecorated(true);
        device.setFullScreenWindow(this);*/
        
        setTitle("DiceWars (Version 0.0.1)");
        setResizable(true);
        
        jLayeredPane = new JLayeredPane();
        setLayeredPane(jLayeredPane);
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
        
        resizeListener = new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e) {
				screenWidth = ((DiceWars)e.getSource()).getSize().width;
				screenHeight = ((DiceWars)e.getSource()).getSize().height;
		        // Implement resizing here if needed
			}
        };
        addComponentListener(resizeListener);
        
        World world = WindowManager.getManager().getWorld();
        world.setBounds(20, 10, screenWidth - 40, screenHeight - 150);
        jLayeredPane.add(world, 0);
        
        new Thread(new UIGameThread()).start();
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
