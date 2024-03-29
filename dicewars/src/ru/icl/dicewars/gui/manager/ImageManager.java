package ru.icl.dicewars.gui.manager;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

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
        
        if (rec == null || image == null)
             return image;

        Image resized = image.getScaledInstance(rec.width, rec.height, java.awt.Image.SCALE_AREA_AVERAGING);
        
        return resized;
    }
	
	public static Image getDice(int num, Flag flag) {
		if (num < 1 || num > 8) throw new IllegalArgumentException(); 
				
		if (dices == null) {
    		dices = new HashMap<Integer, Map<Flag,Image>>();
    	}
    	Integer index = Integer.valueOf(num);
    	
    	if (dices.get(num) == null){
    		dices.put(num, new HashMap<Flag, Image>());
    	}
    	
    	Map<Flag,Image> d = dices.get(num);
    	
    	if (!d.containsKey(flag)) {
    		String fileName = null;
    		switch (flag) {
				case BLUE:	fileName = "blue";break;
				case RED:	fileName = "red";break;
				case CYAN:	fileName = "cyan";break;
				case YELLOW:	fileName = "yellow";break;
				case GREEN:	fileName = "green";break;
				case ORANGE:	fileName = "orange";break;
				case GRAY:	fileName = "gray";break;
				case MAGENTA: fileName = "magenta";break;
				default:
					throw new IllegalStateException();
			}
    		String path = "/resources/dice/" + fileName + index + ".png";
    		Image image = getImageFromResource(path);
    		d.put(flag, image);
    	}
    	
    	return d.get(flag);
	}
    
    public static Image getPauseSpeedImage() {
    	if (pauseSpeedImage == null) {
    		pauseSpeedImage = getImageFromResource("/resources/speed/pause.png", new Rectangle(32,32));
    	}
    	return pauseSpeedImage;
    }
    
    public static Image getPauseSpeedImageSelected() {
    	if (pauseSpeedImageSelected == null) {
    		pauseSpeedImageSelected = getImageFromResource("/resources/speed/pause_s.png", new Rectangle(32,32));
    	}
    	return pauseSpeedImageSelected;
    }
    
    public static Image getPauseSpeedImageHovered() {
    	if (pauseSpeedImageHovered == null) {
    		pauseSpeedImageHovered = getImageFromResource("/resources/speed/pause_h.png", new Rectangle(32,32));
    	}
    	return pauseSpeedImageHovered;
    }
    
    public static Image getPauseSpeedImageHoveredSelected() {
    	if (pauseSpeedImageHoveredSelected == null) {
    		pauseSpeedImageHoveredSelected = getImageFromResource("/resources/speed/pause_sh.png", new Rectangle(32,32));
    	}
    	return pauseSpeedImageHoveredSelected;
    }

    public static Image getPlaySpeedImage() {
    	if (playSpeedImage == null) {
    		playSpeedImage = getImageFromResource("/resources/speed/play.png", new Rectangle(32,32));
    	}
    	return playSpeedImage;
    }
    
    public static Image getPlaySpeedImageSelected() {
    	if (playSpeedImageSelected == null) {
    		playSpeedImageSelected = getImageFromResource("/resources/speed/play_s.png", new Rectangle(32,32));
    	}
    	return playSpeedImageSelected;
    }
    
    public static Image getPlaySpeedImageHovered() {
    	if (playSpeedImageHovered == null) {
    		playSpeedImageHovered = getImageFromResource("/resources/speed/play_h.png", new Rectangle(32,32));
    	}
    	return playSpeedImageHovered;
    }
    
    public static Image getPlaySpeedImageHoveredSelected() {
    	if (playSpeedImageHoveredSelected == null) {
    		playSpeedImageHoveredSelected = getImageFromResource("/resources/speed/play_sh.png", new Rectangle(32,32));
    	}
    	return playSpeedImageHoveredSelected;
    }
    
    public static Image getForwardSpeedImage() {
    	if (forwardSpeedImage == null) {
    		forwardSpeedImage = getImageFromResource("/resources/speed/forward.png", new Rectangle(32,32));
    	}
    	return forwardSpeedImage;
    }
    
    public static Image getForwardSpeedImageSelected() {
    	if (forwardSpeedImageSelected == null) {
    		forwardSpeedImageSelected = getImageFromResource("/resources/speed/forward_s.png", new Rectangle(32,32));
    	}
    	return forwardSpeedImageSelected;
    }
    
    public static Image getForwardSpeedImageHovered() {
    	if (forwardSpeedImageHovered == null) {
    		forwardSpeedImageHovered = getImageFromResource("/resources/speed/forward_h.png", new Rectangle(32,32));
    	}
    	return forwardSpeedImageHovered;
    }
    
    public static Image getForwardSpeedImageHoveredSelected() {
    	if (forwardSpeedImageHoveredSelected == null) {
    		forwardSpeedImageHoveredSelected = getImageFromResource("/resources/speed/forward_sh.png", new Rectangle(32,32));
    	}
    	return forwardSpeedImageHoveredSelected;
    }
    
    public static Image getFastForwardSpeedImage() {
    	if (fastForwardSpeedImage == null) {
    		fastForwardSpeedImage = getImageFromResource("/resources/speed/fastforward.png", new Rectangle(32,32));
    	}
    	return fastForwardSpeedImage;
    }
    
    public static Image getFastForwardSpeedImageSelected() {
    	if (fastForwardSpeedImageSelected == null) {
    		fastForwardSpeedImageSelected = getImageFromResource("/resources/speed/fastforward_s.png", new Rectangle(32,32));
    	}
    	return fastForwardSpeedImageSelected;
    }
    
    public static Image getFastForwardSpeedImageHovered() {
    	if (fastForwardSpeedImageHovered == null) {
    		fastForwardSpeedImageHovered = getImageFromResource("/resources/speed/fastforward_h.png", new Rectangle(32,32));
    	}
    	return fastForwardSpeedImageHovered;
    }
    
    public static Image getFastForwardSpeedImageHoveredSelected() {
    	if (fastForwardSpeedImageHoveredSelected == null) {
    		fastForwardSpeedImageHoveredSelected = getImageFromResource("/resources/speed/fastforward_sh.png", new Rectangle(32,32));
    	}
    	return fastForwardSpeedImageHoveredSelected;
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

    public static Icon getLoadReplayIcon() {
		if (loadReplayIcon == null) {
			String path = "/resources/icon/loadreplay.png";
			Image image = getImageFromResource(path);
			if (image != null)
				loadReplayIcon = new ImageIcon(image);
		}
		return loadReplayIcon;
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
    
    public static Icon getWarningIcon() {
		if (warningIcon == null) {
			String path = "/resources/icon/warning.png";
			Image image = getImageFromResource(path);
			if (image != null) {
				warningIcon = new ImageIcon(image);
			}
		}
		return warningIcon;
	}
	
    public static Image getPlayersImage(){
		if (playersImage == null){
			String path = "/resources/icon/players.png";
			playersImage = getImageFromResource(path);
		}
		return playersImage;
	}
    
    public static Image getAvatar(Flag flag, int emotion) {
    	if (!avatars.containsKey(flag)){
    		avatars.put(flag, new HashMap<Integer, Image>());
    	}
    	Map<Integer, Image> map = avatars.get(flag);
    	
    	if (!map.containsKey(Integer.valueOf(emotion))){
    		String fileName = null;
    		
    		switch (flag) {
				case BLUE:	fileName = "blue";break;
				case RED:	fileName = "red";break;
				case CYAN:	fileName = "cyan";break;
				case YELLOW:	fileName = "yellow";break;
				case GREEN:	fileName = "green";break;
				case ORANGE:	fileName = "orange";break;
				case GRAY:	fileName = "gray";break;
				case MAGENTA: fileName = "magenta";break;
				default:
					throw new IllegalStateException();
			}
    		
			Image avatar = getImageFromResource("/resources/avatars/"
					+ fileName + String.valueOf(emotion) + ".png",
					new Rectangle(0, 0, 48, 48));
    		map.put(emotion, avatar);
    	}
    	
    	return map.get(Integer.valueOf(emotion));
    }
    
    public static Image getTrophyImage() {
    	if (trophyImage == null) {
			if (trophyImage == null) {
				trophyImage = getImageFromResource("/resources/info/trophy.png", new Rectangle(24, 24));
			}
    	}
    	return trophyImage;
    }
    
    public static Image getArrowImage() {
    	if (arrowImage == null) {
			if (arrowImage == null) {
				arrowImage = getImageFromResource("/resources/info/arrow.png");
			}
    	}
    	return arrowImage;
    }

    
    public static Image getDicePerTurnCountImage(Flag flag) {
    	if (dicePerTurnCountImages == null)
    		dicePerTurnCountImages = new HashMap<Flag, Image>();
    	Image ret = dicePerTurnCountImages.get(flag);
    	if (ret == null) {
    		String fileName = null;
			switch (flag) {
				case BLUE:	fileName = "blue_land"; break;
				case RED:	fileName = "red_land"; break;
				case CYAN:	fileName = "cyan_land"; break;
				case YELLOW:	fileName = "yellow_land"; break;
				case GREEN:	fileName = "green_land"; break;
				case ORANGE:	fileName = "orange_land"; break;
				case GRAY:	fileName = "gray_land"; break;
				case MAGENTA: fileName = "magenta_land"; break;
				default:
					throw new IllegalStateException();
			}
			ret = getImageFromResource("/resources/info/"+fileName+".png", new Rectangle(16,16));
			dicePerTurnCountImages.put(flag, ret);
    	}
    	return ret;
    }
    
    public static Image getDiceCountInReserveImage(Flag flag) {
    	if (diceCountInReserveImages == null)
    		diceCountInReserveImages = new HashMap<Flag, Image>();
    	Image ret = diceCountInReserveImages.get(flag);
    	if (ret == null) {
    		String fileName = null;
			switch (flag) {
				case BLUE:	fileName = "blue_dice"; break;
				case RED:	fileName = "red_dice"; break;
				case CYAN:	fileName = "cyan_dice"; break;
				case YELLOW:	fileName = "yellow_dice"; break;
				case GREEN:	fileName = "green_dice"; break;
				case ORANGE:	fileName = "orange_dice"; break;
				case GRAY:	fileName = "gray_dice"; break;
				case MAGENTA: fileName = "magenta_dice"; break;
				default:
					throw new IllegalStateException();
			}
			ret = getImageFromResource("/resources/info/"+fileName+".png", new Rectangle(16,16));
			diceCountInReserveImages.put(flag, ret);
    	}
    	return ret;
    }    
    
    public static Image getRolledDiceImage(Flag flag, int num) {
		if (num < 1 || num > 8) throw new IllegalArgumentException(); 
		
		if (roledDices == null) {
			roledDices = new HashMap<Integer, Map<Flag,Image>>();
    	}
    	Integer index = Integer.valueOf(num);
    	
    	if (roledDices.get(num) == null){
    		roledDices.put(num, new HashMap<Flag, Image>());
    	}
    	
    	Map<Flag,Image> d = roledDices.get(num);
    	
    	if (!d.containsKey(flag)) {
    		String fileName = null;
    		switch (flag) {
				case BLUE:	fileName = "blue";break;
				case RED:	fileName = "red";break;
				case CYAN:	fileName = "cyan";break;
				case YELLOW:	fileName = "yellow";break;
				case GREEN:	fileName = "green";break;
				case ORANGE:	fileName = "orange";break;
				case GRAY:	fileName = "gray";break;
				case MAGENTA: fileName = "magenta";break;
				default:
					throw new IllegalStateException();
			}
    		String path = "/resources/info/" + fileName + "_dice" +index + ".png";
    		Image image = getImageFromResource(path);
    		d.put(flag, image);
    	}
    	
    	return d.get(flag);
	}
    
    private static Map<Integer, Map<Flag,Image>> dices;
    
    private static Map<Integer, Map<Flag,Image>> roledDices;
    
    private static Map<Flag, Image> dicePerTurnCountImages;
    
    private static Map<Flag, Image> diceCountInReserveImages;
    
    private static Image trophyImage;
    private static Image arrowImage;
    
    private static Image pauseSpeedImage;
    private static Image pauseSpeedImageSelected;
    private static Image pauseSpeedImageHovered;
    private static Image pauseSpeedImageHoveredSelected;
    
    private static Image playSpeedImage;
    private static Image playSpeedImageSelected;
    private static Image playSpeedImageHovered;
    private static Image playSpeedImageHoveredSelected;
    
    private static Image forwardSpeedImage;
    private static Image forwardSpeedImageSelected;
    private static Image forwardSpeedImageHovered;
    private static Image forwardSpeedImageHoveredSelected;

    private static Image fastForwardSpeedImage;
    private static Image fastForwardSpeedImageSelected;
    private static Image fastForwardSpeedImageHovered;
    private static Image fastForwardSpeedImageHoveredSelected;
    
    private static Image diceIconImage;
    
    private static Icon startNewGameIcon;
    private static Icon exitIcon;
    private static Icon loadReplayIcon;
    private static Icon playersIcon;
    private static Icon stopGameIcon;
    
    private static Icon upArrowIcon;
    private static Icon downArrowIcon;
    private static Icon okIcon;
    private static Icon cancelIcon;
    private static Icon warningIcon;
    private static Image playersImage;

    private static Map<Flag, Map<Integer, Image>> avatars = new HashMap<Flag, Map<Integer,Image>>();
}
