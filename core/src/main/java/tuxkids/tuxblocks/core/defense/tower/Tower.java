package tuxkids.tuxblocks.core.defense.tower;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Image;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Lang;
import tuxkids.tuxblocks.core.defense.DiscreteGridObject;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.defense.walker.buff.Buff;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

/**
 * Represents a tower the player can place on the {@link Grid}
 * to shoot at {@link Walker}s and create a maze.
 */
public abstract class Tower extends DiscreteGridObject {

	// when you destory a tower you get points as a refund equal
	// to the cost() of the tower time this multiplier
	private static final int SELL_MULTIPLIER = 100;
	// used to make the whole game easier/harder if necessary
	private static final float DAMAGE_MULTIPLIER = 1f;
	
	protected ImageLayerTintable layer;
	
	private int fireTimer;
	private Vector position = new Vector();
	private Walker lastTarget; // last Walker we shot at
	protected boolean destroyed;
	protected int upgradeLevel = 1;
	
	/** The number of rows this Tower takes up in the {@link Grid} */
	public abstract int rows();
	/** The number of columns this Tower takes up in the {@link Grid} */
	public abstract int cols();
	/** The rate of fire (ms/shot) for this Tower */
	public abstract int fireRate();
	/** The range (in {@link Grid} units) of this Tower */
	public abstract float range();
	/** Creates this Towers {@link Projectile} when firing at a {@link Walker} */
	public abstract Projectile createProjectile();
	/** Creates a basic copy of this Tower, without copying fields */
	public abstract Tower copy();
	/** Essentially a quantifier of the goodness of this tower, used in game calculations */
	public abstract int cost();
	/** The cost in upgrade points to upgrade this Tower */
	public abstract int upgradeCost();
	/** The {@link TowerType} label for this Tower */
	public abstract TowerType type();
	/** Base damage for this Tower before factoring its level */
	protected abstract float baseDamage();
	/** Damage increase per level for this Tower */
	protected abstract float damagePerLevel();
	/** Key for the localized name of this tower */
	protected abstract String nameKey();

	/** The human-readable name for this Tower */
	public final String name() {
		return Lang.getString("tower", nameKey());
	}
	
	/** The level to which this tower has been upgraded */
	public int upgradeLevel() {
		return upgradeLevel;
	}

	/** Returns true if this tower has been destroyed and its resources released */
	public boolean destroyed() {
		return destroyed;
	}
	
	/** Returns the {@link TowerType} associated with the given index */
	public static TowerType getTypeByIndex(int index) {
		return TowerType.values()[index];
	}
	
	/** The splash radius (in {@link Grid} units) of this Tower's projectiles */
	public float splashRadius() {
		return 0;
	}

	/** 
	 * After this Tower has been placed, the width of its cells on the {@link Grid}.
	 * The Tower's image may extend outside of it's base area, so check the image's
	 * height with {@link Tower#width()} for the actual size of the image.
	 */
	public float baseWidth() {
		return cols() * grid.cellSize();
	}
	
	/** 
	 * After this Tower has been placed, the height of its cells on the {@link Grid}.
	 * The Tower's image may extend outside of it's base area, so check the image's
	 * height with {@link Tower#height()} for the actual size of the image.
	 */
	public float baseHeight() {
		return rows() * grid.cellSize();
	}
	
	/**
	 * Returns the width in pixels of this Tower's image. This may not exactly
	 * correlate to size it "take up" on the {@link Grid} is the image has a 2.5d
	 * image.
	 */
	public float width() {
		return layer.width();
		
	}
	
	/**
	 * Returns the height in pixels of this Tower's image. This may not exactly
	 * correlate to size it "take up" on the {@link Grid} is the image has a 2.5d
	 * image.
	 */
	public float height() {
		return layer.height();
		
	}
	
	/** The {@link ImageLayerTintable} for this Sprite */
	public ImageLayerTintable layer() {
		return layer;
	}
	
	/** The {@link ImageLayerTintable#layerAddable()} for this Sprite */
	public Layer layerAddable() {
		return layer.layerAddable();
	}
	
	/** The center (in pixels) of the center of this Tower, relative to the {@link Grid}. */
	public Vector position() {
		return position;
	}
	
	/** The location where projectiles spawned by this Tower should start */
	public Vector projectileStart() {
		return position;
	}
	
	/** Returns true if this Tower can be upgraded further */
	public boolean canUpgrade() {
		return upgradeLevel < 3;
	}
	
	/** Returns the amount of damage this Tower's projectiles do */
	public float damage() {
		// upgrade levels start at 1, so subtract off 1 for most calculations
		return (baseDamage() + damagePerLevel() * (upgradeLevel - 1)) * DAMAGE_MULTIPLIER;
	}
	
	/** The number of TowerTypes there are */
	public static int towerCount() {
		return TowerType.values().length;
	}
	
	/** Add the {@link Buff}s this Tower inflicts to the given {@link Walker} upon a hit */
	public void addBuffs(Walker walker) {
		
	}
	
	/** The last Walker this Tower targeted */
	public Walker lastTarget() {
		return lastTarget;
	}
	
	/** Sets the last Walker this Tower targeted */
	public void setLastTarget(Walker target) {
		lastTarget = target;
	}
	
	/** Sets the {@link Grid} coordinates for this Tower */
	public void setCoordinates(Point coordinates) {
		setCoordinates(coordinates.x, coordinates.y);
	}
	
	/** Sets the {@link Grid} coordinates for this Tower */
	public void setCoordinates(int row, int col) {
		coordinates.setLocation(row, col);
		layer.setTranslation(col * grid.cellSize() + (baseWidth() - width()) / 2,
				row * grid.cellSize() + (baseHeight() - height()));				
		
		position.set(col * grid.cellSize() + width() / 2, 
				row * grid.cellSize() + height() / 2);
	}
	
	@Override
	protected void setDepth(float depth) {
		layer.setDepth(depth);
	}
	
	/** Destroys this Tower, removing it from the {@link Grid} on the next update and releasing its resources. */
	public void destroy() {
		destroyed = true;
		layer.destroy();
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < cols(); j++) {
				grid.getPassability()[i + coordinates.x][j + coordinates.y] = true;
			}
		}
		grid.addPoints((this.cost() + this.upgradeCost() * (upgradeLevel - 1)) * SELL_MULTIPLIER);
	}
	
	/** Attaches this Tower to the given {@link Grid} to show its position as a preview */
	public Tower preview(Grid grid) {
		if (this.grid != null) return this;
		this.grid = grid;
		layer = new ImageLayerTintable(
				createImage(grid.cellSize(), 
						Colors.WHITE));
		updateColor();
		return this;
	}
	
	/** Places this Tower on the given Grid to being firing */
	public Tower place(Grid grid, Point coordinates) {
		preview(grid);
		place(grid, 0);
		setCoordinates(coordinates);
		layer.addListener(new Listener() {
			@Override
			public void onPointerStart(Event event) { }
			
			@Override
			public void onPointerEnd(Event event) {
				Tower.this.grid.showUpgradePanel(Tower.this);
			}
			
			@Override
			public void onPointerDrag(Event event) { }
			
			@Override
			public void onPointerCancel(Event event) { }
		});
		// updates the Grid so no other Towers can be places in this Tower's space
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < cols(); j++) {
				grid.getPassability()[i + coordinates.x][j + coordinates.y] = false;
			}
		}
		return this;
	}
	
	
	@Override
	public boolean update(int delta) {
		super.update(delta);
		if (destroyed) return true;
		
		fireTimer+=delta;
		
		int fireRate = fireRate();
		if (fireRate > 0 && fireTimer > fireRate) {
			if (fire()) {
				fireTimer -= fireRate;
			} else {
				fireTimer = fireRate;
			}
		}
		return false;
	}

	@Override
	public void paint(Clock clock) {
		// in case we ever modify the Tower's image based on this value we
		// update it in the paint method instead of update
		//fireTimer += clock.dt();
	}
	
	/** Sets this tower's upgrade level (usually when loading a saved game) */
	public void setUpgradeLevel(int upgradeLevel) {
		this.upgradeLevel = upgradeLevel;
		if (layer != null) updateColor();
	}
	
	/** Upgrades this tower to its next level. First check {@link Tower#canUpgrade()}. */
	public void upgrade() {
		setUpgradeLevel(upgradeLevel + 1);
	}
	
	// updates the Tower's color, usually when the upgrade level changed
	protected void updateColor() {
		float hue = grid.gameState().themeHue();
		float satDif = 0.3f;
		float valDif = 0.1f;
		// further upgrades are more saturated and darker
		if (upgradeLevel == 1) {
			layer.setTint(CanvasUtils.hsvToRgb(hue, 1 - 2 * satDif, 1));
		} else if (upgradeLevel == 2) {
			layer.setTint(CanvasUtils.hsvToRgb(hue, 1 - satDif, 1 - valDif));
		} else {
			layer.setTint(CanvasUtils.hsvToRgb(hue, 1, 1 - 2 * valDif));
		}
	}
	
	// attempts to fire this Tower and returns true if it can/did fire
	protected boolean fire() {
		return grid.fireProjectile(this);
	}
	
	/** Returns true if this Tower can be placed at the given coordinates on the {@link Grid} */
	public boolean canPlace(int row, int col) {
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < cols(); j++) {
				if (!grid.getPassability()[i + row][j + col]) return false;
			}
		}
		return true;
	}
	
	/** Creates a preview image denoting this Tower's {@link Tower#range()} */
	public Image createRadiusImage() {
		if (range() == 0) return null;
		float rad = range() * grid.cellSize();
		int color = Color.rgb(255, 0, 100);
		return CanvasUtils.createCircleCached(rad, Color.withAlpha(color, 50), 1, color);
	}
	
	/** Returns an Image for this tower for a {@link Grid} with the given {@link Grid#cellSize()} */
	public Image createImage(float cellSize, int color) {
		int width = (int)(cellSize * cols()), height = (int)(cellSize * rows());
		int padding = (int)(cellSize * 0.1f); // the image does not take up the whole grid square
		int rad = (int)(Math.min(width, height) * 0.1f); // rounded rect radius
		CanvasImage image = PlayN.graphics().createImage(width, height);
		Image rect = CanvasUtils.createRoundRectCached(width - padding * 2, height - padding * 2, rad, color, 1, Colors.BLACK);
		image.canvas().drawImage(rect, padding, padding);
		return image;
	}
}
