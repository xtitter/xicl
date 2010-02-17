package ru.icl.dicewars.gui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ru.icl.dicewars.gui.manager.WindowManager;
import ru.icl.dicewars.gui.util.ImageUtil;

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
			PlayersJFrame playersJFrame = new PlayersJFrame();
		}
	};

	Icon startNewGameIcon = null;
	Icon exitIcon = null;
	Icon playersIcon = null;
	Icon stopGameIcon = null;

	Icon getStartNewGameIcon() {
		if (startNewGameIcon == null) {
			String path = "/resources/icon/start.png";
			Image image = ImageUtil.getImage(path);
			if (image != null) {
				startNewGameIcon = new ImageIcon(image);
			}
		}
		return startNewGameIcon;
	}

	Icon getExitIcon() {
		if (exitIcon == null) {
			String path = "/resources/icon/exit.png";
			Image image = ImageUtil.getImage(path);
			if (image != null)
				exitIcon = new ImageIcon(image);
		}
		return exitIcon;
	}

	Icon getPlayersIcon() {
		if (playersIcon == null) {
			String path = "/resources/icon/players.png";
			Image image = ImageUtil.getImage(path);
			if (image != null)
				playersIcon = new ImageIcon(image);
		}
		return playersIcon;
	}

	Icon getStopGameIcon() {
		if (stopGameIcon == null) {
			String path = "/resources/icon/stop.png";
			Image image = ImageUtil.getImage(path);
			if (image != null)
				stopGameIcon = new ImageIcon(image);
		}
		return stopGameIcon;
	}

	public TopMenuMenuBar() {
		JMenuItem startNewGameItem = new JMenuItem("Start new game...");
		startNewGameItem.addActionListener(startNewGameActionListener);
		startNewGameItem.setIcon(getStartNewGameIcon());
		fileMenu.add(startNewGameItem);
		
		JMenuItem stopGameItem = new JMenuItem("Stop game");
		stopGameItem.addActionListener(stopGameActionListener);
		stopGameItem.setIcon(getStopGameIcon());
		fileMenu.add(stopGameItem);

		fileMenu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(exitActionListener);
		exitMenuItem.setIcon(getExitIcon());

		fileMenu.add(exitMenuItem);

		this.add(fileMenu);

		JMenuItem playersMenuItem = new JMenuItem("Players");
		playersMenuItem.addActionListener(playersActionListener);
		playersMenuItem.setIcon(getPlayersIcon());
		settingsMenu.add(playersMenuItem);

		this.add(settingsMenu);
	}
}
