package ru.icl.dicewars.gui.util;

import java.awt.Color;

import ru.icl.dicewars.client.Flag;

public class FlagToColorUtil {
	private FlagToColorUtil() {
	}

	public static Color getColorByFlag(Flag f, int alpha) {
		switch (f) {
		case YELLOW:
			return new Color(255, 255, 0, alpha);
		case BLUE:
			return new Color(0, 70, 200, alpha);
		case CYAN:
			return new Color(100, 255, 255, alpha);
		case GREEN:
			return new Color(0, 150, 0, alpha);
		case MAGENTA:
			return new Color(255, 100, 255, alpha);
		case ORANGE:
			return new Color(255, 127, 0, alpha);
		case RED:
			return new Color(255, 30, 30, alpha);
		case GRAY:
			return new Color(150, 150, 150, alpha);
		}
		return Color.black;
	}
}
