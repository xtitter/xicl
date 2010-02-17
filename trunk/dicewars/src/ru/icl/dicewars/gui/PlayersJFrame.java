package ru.icl.dicewars.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import ru.icl.dicewars.gui.util.ImageUtil;

public class PlayersJFrame extends JFrame {
	
	private final List<CheckBoxItem> checkBoxItems = new ArrayList<CheckBoxItem>();
	private final List<String> items = new ArrayList<String>();
	
	private Icon upArrowIcon;
	private Icon downArrowIcon;
	
	private final JList listCheckBox;
	private final JList listDescription;
	
	private ActionListener upButtonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedIndex = listDescription.getSelectedIndex();
			if (selectedIndex > 0 && items.size() > 0){
				Collections.swap(checkBoxItems, selectedIndex, selectedIndex - 1);
				listCheckBox.repaint();
			}
			if (selectedIndex > 0 && items.size() > 0){
				Collections.swap(items, selectedIndex, selectedIndex - 1);
				listDescription.setSelectedIndex(selectedIndex - 1);
				listDescription.repaint();
			}
		}
	}; 
	
	private ActionListener downButtonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedIndex = listDescription.getSelectedIndex();
			if (selectedIndex < checkBoxItems.size() - 1 && items.size() > 0){
				Collections.swap(checkBoxItems, selectedIndex, selectedIndex + 1);
				listCheckBox.repaint();
			}
			if (selectedIndex < items.size() - 1 && items.size() > 0){
				Collections.swap(items, selectedIndex, selectedIndex + 1);
				listDescription.setSelectedIndex(selectedIndex + 1);
				listDescription.repaint();
			}
		}
	}; 
	
	public Icon getUpArrowIcon() {
		if (upArrowIcon == null) {
			String path = "/resources/icon/uparrow.png";
			Image image = ImageUtil.getImage(path);
			if (image != null) {
				upArrowIcon = new ImageIcon(image);
			}
		}
		return upArrowIcon;
	}

	public Icon getDownArrowIcon() {
		if (downArrowIcon == null) {
			String path = "/resources/icon/downarrow.png";
			Image image = ImageUtil.getImage(path);
			if (image != null) {
				downArrowIcon = new ImageIcon(image);
			}
		}
		return downArrowIcon;
	}
	
	public PlayersJFrame() {
		super("AKCheckList");

		String[] listData = { "Apple", "Orange", "Cherry", "Blue Berry",
				"Banana", "Red Plum", "Watermelon" };

		for (int counter = 0; counter < listData.length; counter++) {
			checkBoxItems.add(new CheckBoxItem());
			items.add(listData[counter]);
		}
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Unable to find System Look and Feel");
		}

		// This listbox holds only the checkboxes
		listCheckBox = new JList(buildCheckBoxItemsListModel());

		// This listbox holds the actual descriptions of list items.
		listDescription = new JList(buildItemsListModel());

		listDescription.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		listDescription.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() != 2)
					return;
				int selectedIndex = listDescription.locationToIndex(me
						.getPoint());
				if (selectedIndex < 0)
					return;
				CheckBoxItem item = (CheckBoxItem) listCheckBox.getModel()
						.getElementAt(selectedIndex);
				item.setChecked(!item.isChecked());
				listCheckBox.repaint();

			}
		});

		listCheckBox.setCellRenderer(new CheckBoxRenderer());

		listCheckBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		listCheckBox.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int selectedIndex = listCheckBox.locationToIndex(me.getPoint());
				if (selectedIndex < 0)
					return;
				CheckBoxItem item = (CheckBoxItem) listCheckBox.getModel()
						.getElementAt(selectedIndex);
				item.setChecked(!item.isChecked());
				listDescription.setSelectedIndex(selectedIndex);
				listCheckBox.repaint();
			}
		});
		
		// Now create a scrollpane;
		JScrollPane scrollPane = new JScrollPane();

		// Make the listBox with Checkboxes look like a rowheader.
		// This will place the component on the left corner of the scrollpane
		scrollPane.setRowHeaderView(listCheckBox);

		// Now, make the listbox with actual descriptions as the main view
		scrollPane.setViewportView(listDescription);

		// Align both the checkbox height and widths
		listDescription.setFixedCellHeight(20);
		listCheckBox.setFixedCellHeight(listDescription.getFixedCellHeight());
		listCheckBox.setFixedCellWidth(20);

		getContentPane().add(scrollPane , BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
		BoxLayout boxLayout = new BoxLayout(buttonPane, BoxLayout.PAGE_AXIS);
		buttonPane.setLayout(boxLayout);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(70, 4, 0, 4));

		getContentPane().add(buttonPane, BorderLayout.LINE_END);
		JButton upButton = new JButton("");
		upButton.setIcon(getUpArrowIcon());
		upButton.addActionListener(upButtonActionListener);		
		buttonPane.add(upButton);
		buttonPane.add(Box.createRigidArea(new Dimension(0, 4)));
		JButton downButton = new JButton("");
		downButton.setIcon(getDownArrowIcon());
		downButton.addActionListener(downButtonActionListener);
		buttonPane.add(downButton);
		
		setSize(450, 250);
		setVisible(true);
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PlayersJFrame playerJFrame = new PlayersJFrame();
			}
		});
	}

	@SuppressWarnings("serial")
	private ListModel buildCheckBoxItemsListModel(){
		return new AbstractListModel() {
			@Override
			public int getSize() {
				return checkBoxItems.size();
			}
			
			@Override
			public Object getElementAt(int index) {
				return checkBoxItems.get(index);
			}
		};
	}
	
	@SuppressWarnings("serial")
	private ListModel buildItemsListModel() {
		return new AbstractListModel() {
			@Override
			public int getSize() {
				return items.size();
			}
			
			@Override
			public Object getElementAt(int index) {
				return items.get(index);
			}
		};
	}

	
	/* Inner class to hold data for JList with checkboxes */
	class CheckBoxItem {
		private boolean isChecked;

		public CheckBoxItem() {
			isChecked = false;
		}

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean value) {
			isChecked = value;
		}
	}

	/* Inner class that renders JCheckBox to JList */
	//@SuppressWarnings("serial")
	class CheckBoxRenderer extends JCheckBox implements ListCellRenderer {
		public CheckBoxRenderer() {
			setBackground(UIManager.getColor("List.textBackground"));
			setForeground(UIManager.getColor("List.textForeground"));
		}

		public Component getListCellRendererComponent(JList listBox,
				Object obj, int currentindex, boolean isChecked,
				boolean hasFocus) {
			setSelected(((CheckBoxItem) obj).isChecked());
			return this;
		}
	}

}
