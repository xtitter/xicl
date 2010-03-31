package ru.icl.dicewars.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ru.icl.dicewars.gui.manager.ImageManager;
import ru.icl.dicewars.gui.manager.WindowManager;

public final class TopMenuMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1921209489464172404L;

	private final JMenu fileMenu = new JMenu("File");
	private final JMenu settingsMenu = new JMenu("Settings");

	private final ActionListener exitActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WindowManager.getInstance().getMainFrame().close();
		}
	};

	private final ActionListener startNewGameActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WindowManager.getInstance().getMainFrame().startNewGame();
		}
	};

	private final ActionListener stopGameActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WindowManager.getInstance().getMainFrame().stopGame();
		}
	};
	
	private final ActionListener playersActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
   			PlayersJDialog playersJDialog = WindowManager.getInstance().getPlayersJDialog();
   			WindowManager.getInstance().getMainFrame().setEnabled(false);
   			playersJDialog.update();
   			playersJDialog.setEnabled(true);
   			playersJDialog.setVisible(true);
   			playersJDialog.toFront();
		}
	};
	
	private final ActionListener loadReplayFileActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WindowManager.getInstance().getMainFrame().startNewReplay();
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
		
		JMenuItem loadReplayFileItem = new JMenuItem("Load replay file");
		loadReplayFileItem.addActionListener(loadReplayFileActionListener);
		loadReplayFileItem.setIcon(ImageManager.getLoadReplayIcon());
		fileMenu.add(loadReplayFileItem);

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
