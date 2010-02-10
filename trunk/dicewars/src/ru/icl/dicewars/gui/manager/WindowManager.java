package ru.icl.dicewars.gui.manager;

import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;

import ru.icl.dicewars.core.FullLand;
import ru.icl.dicewars.core.FullWorld;
import ru.icl.dicewars.core.Point;
import ru.icl.dicewars.core.activity.SimplePlayerAttackActivity;
import ru.icl.dicewars.gui.World;
import ru.icl.dicewars.gui.arrow.Arrow;
import ru.icl.dicewars.gui.arrow.ArrowFactory;

public class WindowManager {
	
	private static WindowManager windowManager = null;
	private int screenWidth;
    private int screenHeight;
	private World world;
	
	public static WindowManager getManager() {
		if (windowManager == null) {
			windowManager = new WindowManager();
		}
		return windowManager;
	}
	
	public World getWorld() {
		if (world == null) {
			world = new World();
			world.setBorder(BorderFactory.createEtchedBorder());
		}
		return world;
	}
	
	public Arrow getArrow(SimplePlayerAttackActivity pa) {
		Arrow arrow = ArrowFactory.getArrow(0, ArrowFactory.ArrowType.BEZIER);
		arrow.setVisible(true);
		arrow.setOpaque(false);
		arrow.setBounds(0, 0, this.screenWidth, this.screenHeight);
		
		FullWorld world = getWorld().getRecentWorld();
		FullLand first = null;
		FullLand second = null;
		for (FullLand l : world.getFullLands()) {
			if (l.getLandId() == pa.getFromLandId()) {
				first = l;
				if (second != null) break;
			}
			if (l.getLandId() == pa.getToLandId()) {
				second = l;
				if (first != null) break;
			}
		}
		if (first != null && second != null) {
			int x1 = 0;
			int y1 = 0;
			int correction = 4;
			//TODO: move this code and from World to wrapper
			int rowOffset = 0;
			for (Point p : first.getPoints()) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = World.X_OFFSET + p.getX()*19 + rowOffset;
				int _y = World.Y_OFFSET + p.getY()*(20 - correction);
				x1 += _x;
				y1 += _y;
			}
			int size = first.getPoints().size();
			if (size > 0) {
				x1 /= size;
				y1 /= size;
			}
			
			int x2 = 0;
			int y2 = 0;
			for (Point p : second.getPoints()) {
				rowOffset = p.getY() % 2 == 0 ? 9 : 0;
				int _x = World.X_OFFSET + p.getX()*19 + rowOffset;
				int _y = World.Y_OFFSET + p.getY()*(20 - correction);
				x2 += _x;
				y2 += _y;
			}
			size = second.getPoints().size();
			if (size > 0) {
				x2 /= size;
				y2 /= size;
			}
			
			arrow.setCoordinates(x1 + 30, y1, x2 + 30, y2);
		} else { // shouldn't happen. if it did, then some bug in our game engine
			return null;
		}
		return arrow;
	}
	
	public JLayeredPane getJLayeredPane() {
		return jLayeredPane;
	}

	public void setJLayeredPane(JLayeredPane layeredPane) {
		jLayeredPane = layeredPane;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}
	
	private JLayeredPane jLayeredPane;
}
