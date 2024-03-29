package ru.icl.dicewars.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JPanel;

import ru.icl.dicewars.client.Flag;
import ru.icl.dicewars.gui.component.RoundedBorder;
import ru.icl.dicewars.gui.manager.ImageManager;
import ru.icl.dicewars.gui.util.FlagToColorUtil;

public final class PlayerJPanel extends JPanel {

	private final static long serialVersionUID = 8066379108638626622L;
	
	private final static int radius = 12; 
	private final static int alpha = 90; 

	private Flag flag;
	private final String playerName;
	private int totalDiceCount = -1;

	private int dicePerTurnCount = 0;
	private int diceCountInReserve = 0;
	private int winnerTotalDiceCount = 0;
	private boolean winner = false;
	
	private int place = 10;
	private boolean isGameOver = false;
	
	private static Font infoFont = null;
	private static Font placeFont = null;
	
	public PlayerJPanel(String playerName) {
		this.playerName = playerName;
		setBorder(new RoundedBorder(Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, radius, 0));
	}
	
	public PlayerJPanel(Flag flag, String playerName) {
		this.flag = flag;
		this.playerName = playerName;
		Color color = FlagToColorUtil.getColorByFlag(flag, alpha);
		setBorder(new RoundedBorder(color, color, color, radius, 0));
	}
	
	public void setFlag(Flag flag) {
		this.flag = flag;
		Color color = FlagToColorUtil.getColorByFlag(flag, alpha);
		setBorder(new RoundedBorder(color, color, color, radius, 0));
	}
	
	public void setPlace(int place) {
		this.place = place;
	}
	
	public void setGameOver(boolean f){
		this.isGameOver = f;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (flag != null){
			g.setColor(FlagToColorUtil.getColorByFlag(flag, alpha));
		}else{
			g.setColor(Color.LIGHT_GRAY);
		}
		g.fillRect(radius, radius, getWidth() - radius*2, getHeight() - radius*2);
	}
	
	@Override
	public void paintBorder(Graphics g) {
		super.paintBorder(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		if (flag != null){
			Image avatar = null;	
			if (winner) {
				g.drawImage(ImageManager.getTrophyImage(), 150, 9, this);
				avatar = ImageManager.getAvatar(flag, Emotion.HAPPY.value());
			}
			if (winnerTotalDiceCount / 2 > totalDiceCount){
				avatar = ImageManager.getAvatar(flag, Emotion.CALM.value());
			}
			if (winnerTotalDiceCount / 4 > totalDiceCount){
				avatar = ImageManager.getAvatar(flag, Emotion.EVIL.value());
			}
			if (dicePerTurnCount == 0){
				avatar = ImageManager.getAvatar(flag, Emotion.SAD.value());
			}
			if (avatar == null || totalDiceCount == -1){
				avatar = ImageManager.getAvatar(flag, Emotion.SMILING.value());
			}
			if (avatar != null) g.drawImage(avatar, radius - 9, radius - 6, this);
			
			if (!isGameOver){
				Image dicePerTurnCountImage = ImageManager.getDicePerTurnCountImage(flag);
				if (dicePerTurnCountImage != null)
					g2d.drawImage(dicePerTurnCountImage, 55, 32, this);
			
				Image diceCountInReserveImage = ImageManager.getDiceCountInReserveImage(flag);
				if (diceCountInReserveImage != null)
					g2d.drawImage(diceCountInReserveImage, 105, 32, this);
			}
		}
		
		g2d.setColor(Color.black);
		
		int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
	    int fontSize = (int)Math.round(12.0 * screenRes / 96.0);
	    Font font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
	    String outString = "No name";
		if (playerName != null)
			outString = playerName.substring(0, Math.min(playerName.length(), 13));
		while (font.getStringBounds(outString, g2d.getFontRenderContext()).getWidth() > 80){
			fontSize--;
			font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
		}
		g2d.setFont(font);
		g2d.drawString(outString, 67, 20);
		
		if (flag != null && !isGameOver){
			font = getInfoFont(g2d);
			g2d.setFont(font);
			g2d.drawString("- " + String.valueOf(dicePerTurnCount), 75, 45);
			g2d.drawString("- " + String.valueOf(diceCountInReserve), 125, 45);
		}else{
			if (flag != null && isGameOver){
				g2d.setColor(Color.DARK_GRAY);
				font = getPlaceFont(g2d);
				g2d.setFont(font);
				g2d.drawString("Place: " + String.valueOf(place), 65, 45);
			}
		}
	}

	private Font getPlaceFont(Graphics2D g2d) {
		if (placeFont != null) return placeFont;
		int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
		int fontSize = (int)Math.round(24.0 * screenRes / 96.0);
	    Font font = new Font(Font.SANS_SERIF, Font.ITALIC | Font.BOLD, fontSize);
	    boolean f = true;
	    while (f){
		    int max = 0;
		    for (int i = 1;i<9;i++){
		    	if (max < font.getStringBounds("Place: " + String.valueOf(i), g2d.getFontRenderContext()).getWidth()){
		    		max = (int)font.getStringBounds("Place: " + String.valueOf(i), g2d.getFontRenderContext()).getWidth() + 1;
		    	}
		    }
		    if (max <= 80) f = false;
			fontSize--;
			font = new Font(Font.SANS_SERIF, Font.ITALIC  , fontSize);
		}
	    placeFont = font;
		return placeFont;
	}

	private Font getInfoFont(Graphics2D g2d) {
		if (infoFont != null) return infoFont;
		int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
		int fontSize = (int)Math.round(18.0 * screenRes / 96.0);
	    Font font = new Font(Font.SANS_SERIF, Font.ITALIC | Font.BOLD, fontSize);
	    boolean f = true;
	    while (f){
		    int max = 0;
		    for (int i = 10;i<99;i++){
		    	if (max < font.getStringBounds("- " + i, g2d.getFontRenderContext()).getWidth()){
		    		max = (int)font.getStringBounds("- " + i, g2d.getFontRenderContext()).getWidth() + 1;
		    	}
		    }
		    if (max <= 28) f = false;
			fontSize--;
			font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
		}
	    infoFont = font;
		return infoFont;
	}
	
	public void setDicePerTurnCount(int areaCount) {
		this.dicePerTurnCount = areaCount;
	}

	public int getDicePerTurnCount() {
		return dicePerTurnCount;
	}
	
	public void setDiceCountInReserve(int diceCountInReserve) {
		this.diceCountInReserve = diceCountInReserve;
	}
	
	public int getDiceCountInReserve() {
		return diceCountInReserve;
	}
	
	public int getTotalDiceCount() {
		return totalDiceCount;
	}
	
	public void setTotalDiceCount(int totalDiceCount) {
		this.totalDiceCount = totalDiceCount;
	}
	
	public Flag getFlag() {
		return flag;
	}
	
	public void setWinner(boolean winner) {
		this.winner = winner;
	}
	
	public boolean isWinner() {
		return winner;
	}
	
	public void setWinnerTotalDiceCount(int winnerTotalDiceCount) {
		this.winnerTotalDiceCount = winnerTotalDiceCount;
	}
	
	public int getWinnerTotalDiceCount() {
		return winnerTotalDiceCount;
	}
	
	private static enum Emotion{
		HAPPY(1), SMILING(2), CALM(3), EVIL(4), SAD(5);
		
		int i;
		
		private Emotion(int i){
			this.i = i;
		}
		
		public int value(){
			return i;
		}
	}
}
