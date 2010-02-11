package ru.icl.dicewars.gui.manager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import ru.icl.dicewars.gui.util.ImageUtil;
import ru.icl.dicewars.gui.util.TransparencyUtil;

public class ImageManager {

	/*protected static Image getImageFromResource(String path) {
        URL imageURL = ImageManager.class.getResource(path);
        BufferedImage image = null;

        try {
            image = ImageIO.read(imageURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }
    
	protected static BufferedImage getBufferedImageFromResource(String path) {
        URL imageURL = ImageManager.class.getResource(path);
        BufferedImage image = null;

        try {
            image = ImageIO.read(imageURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }
    
	protected static BufferedImage getBufferedImageFromDisk(String path) {
        BufferedImage image = null;

        try {
    		image = ImageIO.read(new File(path));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

        return image;
    }
    
    protected static BufferedImage getBufferedImageFromDisk(String path, Rectangle rec) {
        BufferedImage image = null;
        BufferedImage resized = null;
        
        try {
    		image = ImageIO.read(new File(path));
    		
    		if (rec == null) {
                 return image;
    		}

    		resized = new BufferedImage((int)rec.getWidth(), (int)rec.getHeight(), BufferedImage.TYPE_INT_ARGB);
    		Graphics2D g = resized.createGraphics();
    		g.drawImage(image, 0, 0, (int)rec.getWidth(), (int)rec.getHeight(), null);
    		g.dispose();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

        return resized;
    }

    protected static Image getImageFromResource(String path, Rectangle rec) {
        URL imageURL = ImageManager.class.getResource(path);
        BufferedImage image = null;
        Image resized = null;

        try {
            image = ImageIO.read(imageURL);

            if (rec == null)
                return image;

            resized = image.getScaledInstance(rec.width, rec.height, java.awt.Image.SCALE_SMOOTH);
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
            imageCardTransparent = Transparency.makeColorTransparent(image, mask);
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
            imageCardTransparent = Transparency.makeColorTransparent(image, mask);

            resized = imageCardTransparent.getScaledInstance(rec.width, rec.height, java.awt.Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resized;
    }*/
    
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
    		//Image image = getImageFromResource(path);
    		Image image = ImageUtil.getImage(path);
    		Image coloredImage = ImageUtil.createColouredImage(image, color);
    		d.put(color, coloredImage);
    		//d.put(color, image);
    	}
    	
    	return d.get(color);
    }
    
    private static Map<Integer, Map<Color,Image>> dices;
}
