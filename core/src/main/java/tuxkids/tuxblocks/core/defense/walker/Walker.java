package tuxkids.tuxblocks.core.defense.walker;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.Layer;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.Cache.Key;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.DiscreteGridObject;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.Pathing;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.buff.Buff;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.utils.HashCode;

/**
 * Represents an enemy that traverses the {@link Grid} from one side
 * to the other, while the player's {@link Tower}s try to shoot it down.
 * This class contains most of the logic for all Walkers, and its various
 * subclasses implement its movement animation.
 */
public abstract class Walker extends DiscreteGridObject {
	
	protected List<Point> path; // list of coordinates that comprise the path to the goal
	// last coordinates of this Walker, the Walker's destination's coordinates
	protected Point lastCoordinates, destination;
	protected ImageLayerTintable layer;
	protected float hp; // ranges from [0,maxHp()]
	protected float alpha = 1;
	protected int level; // symbolic level indicating difficulty of this Walker (changes its color)
	
	private float walkingMs; // ms spent so far going from lastCoordinates to coordinates
	private boolean placed; // is it on the Grid
	private Vector position = new Vector(); // continuous position on the Grid

	protected List<Buff> buffs = new ArrayList<Buff>(); // Walker's current Buffs
	
	/** Updates the Walker's movement animation */
	protected abstract void updateMovement(float perc);
	/** Base maxHp, before considering the game's difficulty */
	protected abstract int maxHpBase();
	/** The amount of time (ms) it take this Walker to traverse one {@link Grid} cell */
	public abstract int walkCellTime();
	/** Returns a basic copy of this Walker without any fields set */
	public abstract Walker copy();

	/** Sets the Walkers perceived difficulty level */
	public Walker setLevel(int level) {
		this.level = level;
		return this;
	}
	
	/** The Walker's max Hp, adjusted for the game's difficulty */
	public float maxHp() {
		if (grid == null) return maxHpBase();
		return maxHpBase() * grid.gameState().difficulty().getWalkerHpMultiplier();
	}
	
	public Layer layerAddable() {
		return layer.layerAddable();
	}
	
	public ImageLayerTintable layer() {
		return layer;
	}
	
	/** Returns true if this Walker has been placed on a {@link Grid} */
	public boolean placed() {
		return placed;
	}
	
	/** 
	 * The amount of points this Walker should give for destroying it.
	 * This is also a good indication of the Walker's difficulty, as it
	 * is proportional to its maxHp and speed.
	 */
	public int exp() {
		return Math.round(maxHp() / 10 * 500 / walkCellTime() * 10) * 5;
	}
	
	/** The continuous position of the Walker on the {@link Grid} */
	public Vector position() {
		return position;
	}
	
	/** The width (in pixels) of this Walker on the {@link Grid} */
	public float width() {
		return grid.cellSize();
	}
	
	/** The height (in pixels) of this Walker on the {@link Grid} */
	public float height() {
		return grid.cellSize();
	}
	
	/** Returns true if this Walker is alive and kickin' */
	public boolean isAlive() {
		return hp > 0 && !destroyed();
	}
	
	/** Returns true if this Walker has been destroyed and its resources released */
	public boolean destroyed() {
		return layer.layerAddable().destroyed();
	}
	
	@Override
	protected void setDepth(float depth) {
		layer.setDepth(depth);
	}
	
	/** Places this Walker on the given Grid and prepares to walk to the given destination */
	public Walker place(Grid grid, Point coordinates, Point desitnation, float depth) {
		place(grid, depth);
		this.destination = desitnation;
		this.coordinates = lastCoordinates = coordinates;
		this.walkingMs = walkCellTime();
		this.hp = maxHp();
		placed = true;
		path = new ArrayList<Point>();
		path.addAll(grid.currentPath());
		path.remove(0);
		createSprite();
		update(0);
		return this;
	}
	
	/*
	 * Store the Walker's Image in the Cache, with its color
	 * and size as keys. This cannot be statically stored because
	 * it will change when the theme color changes.
	 */
	private static SpriteKey key = new SpriteKey();
	private static class SpriteKey extends Key {

		int color;
		float size;
		
		public SpriteKey set(int color, float cellSize) {
			this.color = color;
			this.size = cellSize;
			return this;
		}
		
		@Override
		public void addFields(HashCode hashCode) {
			hashCode.addField(color);
			hashCode.addField(size);
		}

		@Override
		public Key copy() {
			return new SpriteKey().set(color, size);
		} 
		
	}
	
	// based on it's level
	private int getBaseTint() {
		if (level == 1) {
			return grid.gameState().secondaryColor();
		} else if (level == 2) {
			return grid.gameState().ternaryColor();
		} else {
			return Colors.WHITE;
		}
	}
	
	// a really fancy rounded rect
	private void createSprite() {
		int color = Colors.WHITE;
		
		Image cached = Cache.getImage(key.set(color, grid.cellSize()));
		
		if (cached == null) {
			CanvasImage image = graphics().createImage(grid.cellSize(), grid.cellSize());
			image.canvas().setFillColor(color);
			image.canvas().setStrokeColor(Colors.BLACK);
			int border = (int)(grid.cellSize() * 0.1f);
			image.canvas().fillRect(border, border, image.width() - border * 2, image.height() - border * 2);
			image.canvas().strokeRect(border, border, image.width() - 1 - border * 2, 
					image.height() - 1 - border * 2);
			cached = Cache.putImage(key, image);
		}
		
		layer = new ImageLayerTintable(cached);
		update(0);
	}
	
	/** Updates this Walker's path to the destination */
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
			// move the discrete position
			walkingMs -= walkCellTime();
			if (path.size() > 0) {
				// find the next coordinate to go to from the path
				lastCoordinates = coordinates;
				Point nLoc = path.remove(0);
				if (!grid.getPassability()[nLoc.x][nLoc.y]) {
					// if that coordinate is no longer passable (there's a Tower there)
					// refresh our path to go around it
					refreshPath();
					nLoc = path.remove(0);
				}
				coordinates = nLoc;
			} else {
				// we're at the destination
				grid.loseLife();
				layer.destroy();
				return true;
			}
		}
		
		// show damage through tint
		layer.setTint(getBaseTint(), grid.towerColor(), hp / maxHp());
		layer.setAlpha(alpha);
		
		// update and remove buffs as appropriate
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
			// slowing a Walker just makes it update slower
			for (Buff buff : buffs) dt = buff.modifySpeed(dt);
			walkingMs += dt;
		} else {
			// fade out if dead
			layer.setAlpha(lerpTime(layer.alpha(), 0, 0.995f, dt));
		}

		// position is interpolated between last and current position (so we're always a little
		// behind our "actual" position. I.E. the position is the cell we're moving into
		float perc = (float)walkingMs / walkCellTime();
		position.set((lerp(coordinates.y, lastCoordinates.y, 1 - perc) + 0.5f) * grid.cellSize(),
				(lerp(coordinates.x, lastCoordinates.x, 1 - perc) + 0.5f) * grid.cellSize());
		
		// call abstract update animation
		updateMovement(perc);		
	}
	
	/** Deals the given amount of damage to this Walker */
	public void damage(float damage) {
		float oldHp = hp;
		hp -= damage;
		hp = Math.max(hp, 0);
		if (hp == 0 && oldHp != 0) {
			// die
			grid.addPoints(exp());
			Audio.se().play(Constant.SE_DIE);
		}
	}
	
	public void addBuff(Buff buff, boolean replaceIfPresent) {
		if (replaceIfPresent && buffs.contains(buff)) {
			// if we can only have one instance of this buff at a time
			// delete the current instance.
			buffs.remove(buff);
		}
		buffs.add(buff);
	}
	
}
