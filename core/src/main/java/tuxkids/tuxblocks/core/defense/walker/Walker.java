package tuxkids.tuxblocks.core.defense.walker;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Layer;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.defense.DiscreteGridObject;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.Pathing;
import tuxkids.tuxblocks.core.defense.walker.buff.Buff;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;

public abstract class Walker extends DiscreteGridObject {
	
	protected List<Point> path;
	protected Point lastCoordinates, destination;
	protected ImageLayerTintable layer;
	protected float hp;
	protected float alpha = 1;
	
	private float walkingMs;
	private boolean placed;
	private Vector position = new Vector();
	
	protected abstract void updateMovement(float perc);
	public abstract int getMaxHp();
	public abstract int walkCellTime();
	public abstract Walker copy();
	
	protected List<Buff> buffs = new ArrayList<Buff>();
	
	public Layer layerAddable() {
		return layer.layerAddable();
	}
	
	public ImageLayerTintable layer() {
		return layer;
	}
	
	public Vector position() {
		return position;
	}
	
	public float width() {
		return grid.cellSize();
	}
	
	public float height() {
		return grid.cellSize();
	}
	
	public boolean isAlive() {
		return hp > 0 && !destroyed();
	}
	
	public boolean destroyed() {
		return layer.layerAddable().destroyed();
	}
	
	@Override
	protected void setDepth(float depth) {
		layer.setDepth(depth);
	}
	
	public Walker place(Grid grid, Point coordinates, Point desitnation, float depth) {
		place(grid, depth);
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
		CanvasImage image = graphics().createImage(grid.cellSize(), grid.cellSize());
		image.canvas().setFillColor(Colors.WHITE);
		image.canvas().setStrokeColor(Colors.BLACK);
		int border = (int)(grid.cellSize() * 0.1f);
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

	@Override
	protected float depthRow() {
		return position.y / grid.cellSize();
	}
	
	@Override
	protected float depthCol() {
		return position.x / grid.cellSize();
	}
	
	@Override
	public boolean update(int delta) {
		super.update(delta);

		if (hp == 0) {
			if (layer.alpha() < 0.01f) {
				layer.destroy();
				return true;
			}
			return false;
		}
		
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
		layer.setTint(Colors.WHITE, grid.towerColor(), hp / getMaxHp());
		layer.setAlpha(alpha);
		
		for (int i = 0; i < buffs.size(); i++) {
			Buff buff = buffs.get(i);
			if (buff.update(delta)) {
				buffs.remove(i);
				i--;
				continue;
			}
		}
		
		return false;
	}
	
	@Override
	public void paint(Clock clock) {
		float dt = clock.dt();
		if (hp > 0) {
			for (Buff buff : buffs) dt = buff.modifySpeed(dt);
			walkingMs += dt;
		} else {
			layer.setAlpha(lerpTime(layer.alpha(), 0, 0.995f, dt));
		}
		float perc = (float)walkingMs / walkCellTime();
		position.set((lerp(coordinates.y, lastCoordinates.y, 1 - perc) + 0.5f) * grid.cellSize(),
				(lerp(coordinates.x, lastCoordinates.x, 1 - perc) + 0.5f) * grid.cellSize());
		updateMovement(perc);		
	}
	public void damage(float damage) {
		hp -= damage;
		hp = Math.max(hp, 0);
	}
	
	public void addBuff(Buff buff, boolean replaceIfPresent) {
		if (replaceIfPresent && buffs.contains(buff)) {
			buffs.remove(buff);
		}
		buffs.add(buff);
	}
	
}
