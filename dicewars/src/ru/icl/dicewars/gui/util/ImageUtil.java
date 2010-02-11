package ru.icl.dicewars.gui.util;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.net.URL;


public class ImageUtil {

	/**
	 * Returns an image resource.
	 *
	 * @param filename the filename of the image to load
	 * @return the loaded image
	 */
	public static Image getImage(String filename) {
		URL url = ImageUtil.class.getResource(filename);
		
		if (url == null){
			return null;
		}
			
		try {
			final BufferedImage result = ImageIO.read(url);

			if (result == null) {
				throw new Error();
			}
			return result;
		} catch (IOException e) {
			return null;
		}

	}

	/**
	 * Creates and returns a buffered version of the specified image.
	 *
	 * @param image the image to create a buffered image for
	 * @return a buffered image based on the specified image
	 */
	public static BufferedImage getBufferedImage(Image image) {
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);

		Graphics g = bufferedImage.getGraphics();

		g.drawImage(image, 0, 0, null);

		return bufferedImage;
	}

	/**
	 * Create a copy of image into a coloured image. The colors of the
	 * input image are changed into the input color, but with the same lumination.
	 *
	 * @param img   the source image
	 * @param color the new color that substitutes the old color(s) in the source image
	 * @return a new image
	 */
	public static Image createColouredImage(Image img, Color color) {
		return (color == null)
				? img
				: (img == null)
						? null
						: Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(img.getSource(), new ColorFilter(color)));
	}

	/**
	 * A color filter used for changing colors into another color.
	 *
	 */
	private static class ColorFilter extends RGBImageFilter {
		private final float[] hsl;

		public ColorFilter(Color color) {
			hsl = ColorUtil.fromRGBtoHSL(color.getRed(), color.getGreen(), color.getBlue());
		}

		@Override
		public int filterRGB(int x, int y, int argb) {
			int r = (argb >> 16) & 0xff;
			int g = (argb >> 8) & 0xff;
			int b = argb & 0xff;

			float[] HSL = ColorUtil.fromRGBtoHSL(r, g, b);

			if (HSL[1] > 0) {
				float L = Math.min(1, (hsl[2] + HSL[2]) / 2 + hsl[2] / 7);

				return argb & 0xff000000 | ColorUtil.fromHSLtoRGB(hsl[0], hsl[1], L);
			}
			return argb;
		}
	}
}
