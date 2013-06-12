package tuxkids.tuxblocks.core.defense;

import java.util.ArrayList;
import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import pythagoras.i.Point;

import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;

public class Walker extends PlayNObject {
	
	private Grid grid;
	private List<Point> path;
	private Point location, lastLocation, destination;
	private float walkingMs;
	private int walkCellTime;
	private ImageLayer sprite;
	
	public ImageLayer getSprite() {
		return sprite;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public Walker(Grid grid, Point location, Point desitnation, int walkCellTime) {
		this.grid = grid;
		this.destination = desitnation;
		this.location = lastLocation = location;
		walkingMs = this.walkCellTime = walkCellTime;
		createSprite();
		refreshPath();
		update(0);
	}
	
	private void createSprite() {
		CanvasImage image = graphics().createImage(grid.getCellSize(), grid.getCellSize());
		image.canvas().setFillColor(Colors.BLUE);
		image.canvas().setStrokeColor(Colors.BLACK);
		image.canvas().fillRect(0, 0, image.width(), image.height());
		image.canvas().strokeRect(0, 0, image.width() - 1, image.height() - 1);
		sprite = graphics().createImageLayer(image);
	}
	
	public void refreshPath() {
		path = Pathing.getPath(grid, location, destination);
		path.remove(0);
	}
	
	public void update(int delta) {
		debug(delta);
		if (walkingMs >= walkCellTime) {
			walkingMs -= walkCellTime;
			if (path.size() > 0) {
				lastLocation = location;
				Point nLoc = path.remove(0);
				if (!grid.getPassability()[nLoc.x][nLoc.y]) {
					refreshPath();
					nLoc = path.remove(0);
				}
				location = nLoc;
			} else {
				//remove
			}
		}
	}
	
	public void paint(Clock clock) {
		walkingMs += clock.dt();
		
		float perc = (float)walkingMs / walkCellTime;
//		float x = lerp(lastLocation.y * grid.getCellSize(), 
//				location.y * grid.getCellSize(), perc);
//		float y = lerp(lastLocation.x * grid.getCellSize(), 
//				location.x * grid.getCellSize(), perc);
		
		int dx = -(location.y - lastLocation.y);
		int dy = -(location.x - lastLocation.x);
		

		float x = Math.max(location.y, lastLocation.y) * grid.getCellSize();
		float y = Math.max(location.x, lastLocation.x) * grid.getCellSize();
		
		sprite.setTranslation(x, y);
		
		float scaleX = dx * (float)Math.cos(Math.PI * perc);
		if (dx == 0) scaleX = 1;
		float scaleY = dy * (float)Math.cos(Math.PI * perc);
		if (dy == 0) scaleY = 1;
		
		debug("%f %f", scaleX, scaleY);
		sprite.setScale(scaleX, scaleY);
		
	}
}
