package ru.icl.dicewars.gui;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ru.icl.dicewars.gui.manager.WindowManager;
import ru.icl.dicewars.gui.util.ImageUtil;

public class TopMenu extends JMenuBar {
	JMenu fileMenu = new JMenu("File");

	ActionListener exitActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WindowManager.getManager().getMainFrame().close();
		}
	};
	
	ActionListener startNewGameActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WindowManager.getManager().getMainFrame().startNewGame();
		}
	};

	Icon startNewGameIcon = null;
	Icon exitIcon = null;
	
	public Icon getStartNewGameIcon() {
		if (startNewGameIcon == null){
			String path = "/resources/icon/start.png";
			Image image = ImageUtil.getImage(path);
			if (image != null){
				startNewGameIcon = new ImageIcon(image);
			}
		}
		return startNewGameIcon;
	}
	
	public Icon getExitIcon() {
		if (exitIcon == null){
			String path = "/resources/icon/exit.png";
			Image image = ImageUtil.getImage(path);
			if (image != null)
				exitIcon = new ImageIcon(image);
		}
		return exitIcon;
	}
	
	public TopMenu() {
		JMenuItem startNewGameItem = new JMenuItem("Start new game...");
		startNewGameItem.addActionListener(startNewGameActionListener);
		startNewGameItem.setIcon(getStartNewGameIcon());

		fileMenu.add(startNewGameItem);
		
		fileMenu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(exitActionListener);
		exitMenuItem.setIcon(getExitIcon());

		fileMenu.add(exitMenuItem);

		this.add(fileMenu);
	}
}
