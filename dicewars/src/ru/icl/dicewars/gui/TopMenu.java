package ru.icl.dicewars.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ru.icl.dicewars.gui.manager.WindowManager;

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

	public TopMenu() {
		JMenuItem startNewGameItem = new JMenuItem("Start new game...");
		startNewGameItem.addActionListener(startNewGameActionListener);
		fileMenu.add(startNewGameItem);
		
		fileMenu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(exitActionListener);
		fileMenu.add(exitMenuItem);
		
		this.add(fileMenu);
	}
}
