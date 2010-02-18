package ru.icl.dicewars.gui.manager;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import ru.icl.dicewars.gui.util.ImageUtil;
import ru.icl.dicewars.gui.util.TransparencyUtil;

public class ImageManager {

	protected static Image getImageFromResource(String path) {
        URL imageURL = ImageManager.class.getResource(path);
        BufferedImage image = null;

        try {
            image = ImageIO.read(imageURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }
    protected static Image getImageFromResource(String path, Rectangle rec) {
        URL imageURL = ImageManager.class.getResource(path);
        BufferedImage image = null;
        Image resized = null;

        try {
            image = ImageIO.read(imageURL);

            if (rec == null)
                return image;

            resized = image.getScaledInstance(rec.width, rec.height, java.awt.Image.SCALE_AREA_AVERAGING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resized;
    }

    protected static Image getImageFromResourceTransparent(String path, Color mask) {
        BufferedImage image = null;
        Image imageCardTransparent = null;

        URL imageURL = ImageManager.class.getResource(path);

        if (imageURL == null) {
            return null;
        }

        try {
            image = ImageIO.read(imageURL);
            imageCardTransparent = TransparencyUtil.makeColorTransparent(image, mask);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageCardTransparent;
    }

    protected static Image getImageFromResourceTransparent(String path, Color mask, Rectangle rec) {
        BufferedImage image = null;
        Image imageCardTransparent = null;
        Image resized = null;

        URL imageURL = ImageManager.class.getResource(path);

        try {
            image = ImageIO.read(imageURL);
            imageCardTransparent = TransparencyUtil.makeColorTransparent(image, mask);

            resized = imageCardTransparent.getScaledInstance(rec.width, rec.height, java.awt.Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resized;
    }
    
    public static Image getDice(int num, Color color) {
    	if (dices == null) {
    		dices = new HashMap<Integer, Map<Color,Image>>();
    	}
    	Integer index = Integer.valueOf(num);
    	
    	if (dices.get(num) == null){
    		dices.put(num, new HashMap<Color, Image>());
    	}
    	
    	Map<Color,Image> d = dices.get(num);
    	
    	if (!d.containsKey(color)) {
    		String path = "/resources/dice/g" + index + ".png";
    		Image image = getImageFromResource(path, new Rectangle(0,0,62,75));
    		//Image image = ImageUtil.getImage(path);
    		Image coloredImage = ImageUtil.createColouredImage(image, color);
    		d.put(color, coloredImage);
    		//d.put(color, image);
    	}
    	
    	return d.get(color);
    }
    
    public static Image getNormalSpeedImage() {
    	if (normalSpeedImage == null) {
    		normalSpeedImage = getImageFromResource("/resources/speed/normal.png");
    	}
    	return normalSpeedImage;
    }
    
    public static Image getNormalSpeedImageSelected() {
    	if (normalSpeedImageSelected == null) {
    		normalSpeedImageSelected = getImageFromResource("/resources/speed/normal_s.png");
    	}
    	return normalSpeedImageSelected;
    }
    
    public static Image getNormalSpeedImageHovered() {
    	if (normalSpeedImageHovered == null) {
    		normalSpeedImageHovered = getImageFromResource("/resources/speed/normal_h.png");
    	}
    	return normalSpeedImageHovered;
    }
    
    public static Image getNormalSpeedImageHoveredSelected() {
    	if (normalSpeedImageHoveredSelected == null) {
    		normalSpeedImageHoveredSelected = getImageFromResource("/resources/speed/normal_sh.png");
    	}
    	return normalSpeedImageHoveredSelected;
    }
    
    public static Image getFastSpeedImage() {
    	if (fastSpeedImage == null) {
    		fastSpeedImage = getImageFromResource("/resources/speed/fast.png");
    	}
    	return fastSpeedImage;
    }
    
    public static Image getFastSpeedImageSelected() {
    	if (fastSpeedImageSelected == null) {
    		fastSpeedImageSelected = getImageFromResource("/resources/speed/fast_s.png");
    	}
    	return fastSpeedImageSelected;
    }
    
    public static Image getFastSpeedImageHovered() {
    	if (fastSpeedImageHovered == null) {
    		fastSpeedImageHovered = getImageFromResource("/resources/speed/fast_h.png");
    	}
    	return fastSpeedImageHovered;
    }
    
    public static Image getFastSpeedImageHoveredSelected() {
    	if (fastSpeedImageHoveredSelected == null) {
    		fastSpeedImageHoveredSelected = getImageFromResource("/resources/speed/fast_sh.png");
    	}
    	return fastSpeedImageHoveredSelected;
    }
    
    public static Image getInatickSpeedImage() {
    	if (inatickSpeedImage == null) {
    		inatickSpeedImage = getImageFromResource("/resources/speed/inatick.png");
    	}
    	return inatickSpeedImage;
    }
    
    public static Image getInatickSpeedImageSelected() {
    	if (inatickSpeedImageSelected == null) {
    		inatickSpeedImageSelected = getImageFromResource("/resources/speed/inatick_s.png");
    	}
    	return inatickSpeedImageSelected;
    }
    
    public static Image getInatickSpeedImageHovered() {
    	if (inatickSpeedImageHovered == null) {
    		inatickSpeedImageHovered = getImageFromResource("/resources/speed/inatick_h.png");
    	}
    	return inatickSpeedImageHovered;
    }
    
    public static Image getInatickSpeedImageHoveredSelected() {
    	if (inatickSpeedImageHoveredSelected == null) {
    		inatickSpeedImageHoveredSelected = getImageFromResource("/resources/speed/inatick_sh.png");
    	}
    	return inatickSpeedImageHoveredSelected;
    }
       
    private static Map<Integer, Map<Color,Image>> dices;
    
    private static Image normalSpeedImage;
    private static Image normalSpeedImageSelected;
    private static Image normalSpeedImageHovered;
    private static Image normalSpeedImageHoveredSelected;
    
    private static Image fastSpeedImage;
    private static Image fastSpeedImageSelected;
    private static Image fastSpeedImageHovered;
    private static Image fastSpeedImageHoveredSelected;

    private static Image inatickSpeedImage;
    private static Image inatickSpeedImageSelected;
    private static Image inatickSpeedImageHovered;
    private static Image inatickSpeedImageHoveredSelected;
}
