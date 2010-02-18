package ru.icl.dicewars.gui.manager;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.gui.util.ImageUtil;

public class ImageManager {
	
	private ImageManager(){
		
	}

	private static Image getImageFromResource(String path) {
		return ImageUtil.getImage(path);
    }

	private static Image getImageFromResource(String path, Rectangle rec) {
        Image image = getImageFromResource(path);
        
        if (rec == null && image == null)
             return image;

        Image resized = image.getScaledInstance(rec.width, rec.height, java.awt.Image.SCALE_AREA_AVERAGING);
        
        return resized;
    }

    /*private static Image getImageFromResourceTransparent(String path, Color mask) {
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

    private static Image getImageFromResourceTransparent(String path, Color mask, Rectangle rec) {
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
       
    public static Image getDiceIconImage(){
		if (diceIconImage == null){
			diceIconImage = getImageFromResource("/resources/icon/dice.png");
		}
		return diceIconImage;
	}
    
    public static Icon getStartNewGameIcon() {
		if (startNewGameIcon == null) {
			String path = "/resources/icon/start.png";
			Image image = getImageFromResource(path);
			if (image != null) {
				startNewGameIcon = new ImageIcon(image);
			}
		}
		return startNewGameIcon;
	}

    public static Icon getExitIcon() {
		if (exitIcon == null) {
			String path = "/resources/icon/exit.png";
			Image image = getImageFromResource(path);
			if (image != null)
				exitIcon = new ImageIcon(image);
		}
		return exitIcon;
	}

    public static Icon getPlayersIcon() {
		if (playersIcon == null) {
			String path = "/resources/icon/players.png";
			Image image = getImageFromResource(path);
			if (image != null)
				playersIcon = new ImageIcon(image);
		}
		return playersIcon;
	}

    public static Icon getStopGameIcon() {
		if (stopGameIcon == null) {
			String path = "/resources/icon/stop.png";
			Image image = getImageFromResource(path);
			if (image != null)
				stopGameIcon = new ImageIcon(image);
		}
		return stopGameIcon;
	}
    
    public static Icon getUpArrowIcon() {
		if (upArrowIcon == null) {
			String path = "/resources/icon/uparrow.png";
			Image image = getImageFromResource(path);
			if (image != null) {
				upArrowIcon = new ImageIcon(image);
			}
		}
		return upArrowIcon;
	}
	
    public static Icon getCancelIcon() {
		if (cancelIcon == null) {
			String path = "/resources/icon/cancel.png";
			Image image = getImageFromResource(path);
			if (image != null) {
				cancelIcon = new ImageIcon(image);
			}
		}
		return cancelIcon;
	}

    public static Icon getDownArrowIcon() {
		if (downArrowIcon == null) {
			String path = "/resources/icon/downarrow.png";
			Image image = getImageFromResource(path);
			if (image != null) {
				downArrowIcon = new ImageIcon(image);
			}
		}
		return downArrowIcon;
	}
	
    public static Icon getOkIcon() {
		if (okIcon == null) {
			String path = "/resources/icon/ok.png";
			Image image = getImageFromResource(path);
			if (image != null) {
				okIcon = new ImageIcon(image);
			}
		}
		return okIcon;
	}
	
    public static Image getPlayersImage(){
		if (playersImage == null){
			String path = "/resources/icon/players.png";
			playersImage = getImageFromResource(path);
		}
		return playersImage;
	}
    
    synchronized public static Image getAvatar(Flag flag) {
    	if (!avatars.containsKey(flag)) {
    		Random randomGenerator = new Random();
    		Integer index = randomGenerator.nextInt(6) + 1;
    		int attempts = 10;
    		Image avatar = getImageFromResource("/resources/avatars/avatar" + String.valueOf(index) + ".png");
    		while (avatarUsed.contains(index) && attempts > 0) {
    			index = randomGenerator.nextInt(6) + 1;
    			avatar = getImageFromResource("/resources/avatars/avatar" + String.valueOf(index) + ".png");
    			attempts--;
    		}
    		avatars.put(flag, avatar);
    		avatarUsed.add(index);
    	}
    	return avatars.get(flag);
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
    
    private static Image diceIconImage;
    
    private static Icon startNewGameIcon;
    private static Icon exitIcon;
    private static Icon playersIcon;
    private static Icon stopGameIcon ;
    
    private static Icon upArrowIcon;
    private static Icon downArrowIcon;
    private static Icon okIcon;
    private static Icon cancelIcon;
    private static Image playersImage;

    private static Map<Flag, Image> avatars = new HashMap<Flag, Image>();
    private static Set<Integer> avatarUsed = new HashSet<Integer>();
}
