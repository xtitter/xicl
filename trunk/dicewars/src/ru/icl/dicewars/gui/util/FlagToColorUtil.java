package ru.icl.dicewars.gui.util;

import java.awt.Color;

import ru.icl.dicewars.client.Flag;

public class FlagToColorUtil {
	public static Color getColorByFlag(Flag f, int alpha) {
		//return new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
		switch (f) {
		case WHITE:
			return Color.white;
		case YELLOW:
			return new Color(255, 255, 0, alpha);
		case BLUE:
			return new Color(0, 70, 200, alpha);
		case CYAN:
			return Color.cyan;
		case GREEN:
			return new Color(0, 150, 0, alpha);
		case MAGENTA:
			return Color.magenta;
		case ORANGE:
			return new Color(255, 127, 0, alpha);
		case RED:
			return new Color(255, 30, 30, alpha);

		}
		return Color.black;
	}
}
