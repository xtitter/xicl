package ru.icl.dicewars.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

class ColoredLand {
	public Dimension size;
	public int x;
	public int y;
	public BufferedImage image;
	public Point center;

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

}
