package tuxkids.tuxblocks.core.defense.walker;

import java.util.ArrayList;
import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.ImageLayerTintable;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.GridObject;
import tuxkids.tuxblocks.core.defense.Pathing;
import tuxkids.tuxblocks.core.utils.Debug;

public abstract class Walker extends GridObject {
	
	protected Grid grid;
	protected List<Point> path;
	protected Point coordinates, lastCoordinates, destination;
	protected ImageLayerTintable layer;
	protected int hp;
	protected float alpha = 1;
	
	private float walkingMs;
	private boolean placed;
	private Vector position = new Vector();
	
	protected abstract void updateMovement(float perc);
	public abstract int getMaxHp();
	public abstract int walkCellTime();
	public abstract Walker copy();
	
	public Layer layerAddable() {
		return layer.layer();
	}
	
	public ImageLayerTintable layer() {
		return layer;
	}
	
	public Point coordinates() {
		return coordinates;
	}
	
	public Vector position() {
		return position;
	}
	
	public float width() {
		return grid.getCellSize();
	}
	
	public float height() {
		return grid.getCellSize();
	}
	
	public boolean isAlive() {
		return hp > 0 && !destroyed();
	}
	
	public boolean destroyed() {
		return layer.layer().destroyed();
	}
	
	public Walker place(Grid grid, Point coordinates, Point desitnation) {
		this.grid = grid;
		this.destination = desitnation;
		this.coordinates = lastCoordinates = coordinates;
		this.walkingMs = walkCellTime();
		this.hp = getMaxHp();
		placed = true;
		path = new ArrayList<Point>();
		path.addAll(grid.currentPath());
		path.remove(0);
		createSprite();
		update(0);
		return this;
	}
	
	private void createSprite() {
		CanvasImage image = graphics().createImage(grid.getCellSize(), grid.getCellSize());
		image.canvas().setFillColor(Colors.WHITE);
		image.canvas().setStrokeColor(Colors.BLACK);
		int border = (int)(grid.getCellSize() * 0.1f);
		image.canvas().fillRect(border, border, image.width() - border * 2, image.height() - border * 2);
		image.canvas().strokeRect(border, border, image.width() - 1 - border * 2, 
				image.height() - 1 - border * 2);
		layer = new ImageLayerTintable(image);
		update(0);
	}
	
	public void refreshPath() {
		path = Pathing.getPath(grid, coordinates, destination);
		path.remove(0);
	}

	public boolean update(int delta) {
		if (walkingMs >= walkCellTime()) {
			walkingMs -= walkCellTime();
			if (path.size() > 0) {
				lastCoordinates = coordinates;
				Point nLoc = path.remove(0);
				if (!grid.getPassability()[nLoc.x][nLoc.y]) {
					refreshPath();
					nLoc = path.remove(0);
				}
				coordinates = nLoc;
			} else {
				layer.destroy();
				return true;
			}
		}
		layer.setTint(Colors.BLACK, grid.towerColor(), (float)hp / getMaxHp());
		layer.setAlpha(alpha);
		return false;
	}
	
	public void paint(Clock clock) {
		walkingMs += clock.dt();
		float perc = (float)walkingMs / walkCellTime();
		position.set((lerp(coordinates.y, lastCoordinates.y, 1 - perc) + 0.5f) * grid.getCellSize(),
				(lerp(coordinates.x, lastCoordinates.x, 1 - perc) + 0.5f) * grid.getCellSize());
		updateMovement(perc);		
	}
	public void damage(int damage) {
		hp -= damage;
		hp = Math.max(hp, 0);
	}
	
}
