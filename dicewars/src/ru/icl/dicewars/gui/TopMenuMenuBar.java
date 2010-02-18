package ru.icl.dicewars.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ru.icl.dicewars.gui.manager.ImageManager;
import ru.icl.dicewars.gui.manager.WindowManager;

public class TopMenuMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1921209489464172404L;

	JMenu fileMenu = new JMenu("File");
	JMenu settingsMenu = new JMenu("Settings");

	ActionListener exitActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WindowManager.getInstance().getMainFrame().close();
		}
	};

	ActionListener startNewGameActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WindowManager.getInstance().getMainFrame().startNewGame();
		}
	};

	ActionListener stopGameActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WindowManager.getInstance().getMainFrame().stopGame();
		}
	};
	
	ActionListener playersActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
   			PlayersJFrame playersJFrame = WindowManager.getInstance().getPlayersJFrame();
   			playersJFrame.setVisible(true);
   			playersJFrame.setEnabled(true);
   			playersJFrame.setState(JFrame.NORMAL);
   			playersJFrame.toFront();
		}
	};

	public TopMenuMenuBar() {
		JMenuItem startNewGameItem = new JMenuItem("Start new game...");
		startNewGameItem.addActionListener(startNewGameActionListener);
		startNewGameItem.setIcon(ImageManager.getStartNewGameIcon());
		fileMenu.add(startNewGameItem);
		
		JMenuItem stopGameItem = new JMenuItem("Stop game");
		stopGameItem.addActionListener(stopGameActionListener);
		stopGameItem.setIcon(ImageManager.getStopGameIcon());
		fileMenu.add(stopGameItem);

		fileMenu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(exitActionListener);
		exitMenuItem.setIcon(ImageManager.getExitIcon());

		fileMenu.add(exitMenuItem);

		this.add(fileMenu);

		JMenuItem playersMenuItem = new JMenuItem("Players");
		playersMenuItem.addActionListener(playersActionListener);
		playersMenuItem.setIcon(ImageManager.getPlayersIcon());
		settingsMenu.add(playersMenuItem);

		this.add(settingsMenu);
	}
}
