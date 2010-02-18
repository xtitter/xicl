package ru.icl.dicewars.gui.component;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

/**
 * Image button with hover.
 * 
 * @author nantuko
 */
@SuppressWarnings("serial")
public class HoverButton extends JPanel implements MouseListener {

	private Image image;
	private Image hoverImage;
	private Image imageSelected;
	private Image hoverImageSelected;
	private Image disabledImage;
	private Rectangle sizeButton;
	private String text;
	private int textOffsetY = 0;

	private boolean isHovered = false;
	private boolean isSelected = false;

	private Command observer = null;

	public HoverButton(String text, Image image, Image imageSelected, Image hover, Image hoverSelected, Image disabled, Rectangle size) {
		this.image = image;
		this.hoverImage = hover;
		this.imageSelected = imageSelected;
		this.hoverImageSelected = hoverSelected;
		this.disabledImage = disabled;
		this.sizeButton = size;
		this.text = text;
		setOpaque(false);
		addMouseListener(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		if (isEnabled()) {
			if (isHovered) {
				g.drawImage(this.isSelected ? hoverImageSelected : hoverImage, 0, 0, sizeButton.width, sizeButton.height, this);
			} else {
				g.drawImage(this.isSelected ? imageSelected : image, 0, 0, sizeButton.width, sizeButton.height, this);
			}
		} else {
			g.drawImage(disabledImage, 0, 0, sizeButton.width, sizeButton.height, this);
		}
		if (text != null) {
			g.drawString(text, 0, this.textOffsetY);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		isHovered = true;
		this.repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		isHovered = false;
		this.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (isEnabled() && observer != null) {
				observer.execute();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public void setObserver(Command observer) {
		this.observer = observer;
	}

	@Override
	public void setBounds(Rectangle r) {
		super.setBounds(r);
		this.textOffsetY = r.height - 2;
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
