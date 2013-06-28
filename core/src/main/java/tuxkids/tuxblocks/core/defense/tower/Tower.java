package tuxkids.tuxblocks.core.defense.tower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.ImageLayerTintable;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.DiscreteGridObject;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.GridObject;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public abstract class Tower extends DiscreteGridObject {
	
	protected ImageLayerTintable layer;
	
	private int fireTimer;
	private Vector position = new Vector();
	private int id;
	private Walker lastTarget;
	
	public abstract int rows();
	public abstract int cols();
	public abstract float damage();
	public abstract int fireRate();
	public abstract float range();
	public abstract Projectile createProjectile();
	public abstract Tower copy();
	public abstract String name();
	public abstract int cost();
	public abstract int commonness();
	
	private final static List<TowerType> towerBag;
	static {
		towerBag = new ArrayList<TowerType>();
		for (TowerType type : TowerType.values()) 
			for (int i = 0; i < type.instance().commonness(); i++) 
				towerBag.add(type);
	}

	public static TowerType getTypeByIndex(int index) {
		return TowerType.values()[index];
	}
	
	public float splashRadius() {
		return 0;
	}

	public float baseWidth() {
		return cols() * grid.cellSize();
	}
	
	public float baseHeight() {
		return rows() * grid.cellSize();
	}
	
	public float width() {
		return layer.width();
		
	}
	
	public float height() {
		return layer.height();
		
	}
	
	public ImageLayerTintable layer() {
		return layer;
	}
	
	public Layer layerAddable() {
		return layer.layer();
	}
	
	public Vector position() {
		return position;
	}
	
	public Vector projectileStart() {
		return position;
	}
	
	public int id() {
		return id;
	}

	public static TowerType randomTower() {
		return towerBag.get((int)(Math.random() * towerBag.size()));
	}
	
	public static int towerCount() {
		return TowerType.values().length;
	}
	
	public void addBuffs(Walker walker) {
		
	}
	
	public Walker lastTarget() {
		return lastTarget;
	}
	
	public void setLastTarget(Walker target) {
		lastTarget = target;
	}
	
	public void setCoordinates(Point coordinates) {
		setCoordinates(coordinates.x, coordinates.y);
	}
	
	public void setCoordinates(int row, int col) {
		coordinates.setLocation(row, col);
		layer.setTranslation(col * grid.cellSize() + (baseWidth() - width()) / 2,
				row * grid.cellSize() + (baseHeight() - height()));				
		
//		layer.setTranslation((col + 0.5f) * grid.cellSize() - width() / 2, 
//				(row + 1) * grid.cellSize() - height());
		position.set(col * grid.cellSize() + width() / 2, 
				row * grid.cellSize() + height() / 2);
//		position.set((col + 0.5f) * grid.cellSize(),
//				(row + 0.5f) * grid.cellSize());
	}
	
	@Override
	protected void setDepth(float depth) {
		layer.setDepth(depth);
	}
	
	public Tower preview(Grid grid) {
		if (this.grid != null) return this;
		this.grid = grid;
		layer = new ImageLayerTintable(
				createImage(grid.cellSize(), 
						Colors.WHITE));
		layer.setTint(grid.towerColor());
		return this;
	}
	
	public Tower place(Grid grid, Point coordinates) {
		place(grid, 0);
		setCoordinates(coordinates);
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
		int fireRate = fireRate();
		if (fireRate > 0 && fireTimer > fireRate) {
			if (fire()) {
				fireTimer -= fireRate;
			} else {
				fireTimer = fireRate;
			}
		}
//		float perc = FloatMath.pow((float)fireTimer / fireRate(), 0.5f);
//		perc = Math.min(perc, 1);
//		layer.setTint(grid.towerColor(), Colors.GRAY, perc);
		return false;
	}
	
	@Override
	public void paint(Clock clock) {
		fireTimer += clock.dt();
	}
	
	protected boolean fire() {
		return grid.fireProjectile(this);
	}
	
	public boolean canPlace(int row, int col) {
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < cols(); j++) {
				if (!grid.getPassability()[i + row][j + col]) return false;
			}
		}
		return true;
	}
	
	public Image createRadiusImage() {
		if (range() == 0) return null;
		float rad = range() * grid.cellSize();
		int color = Color.rgb(255, 0, 100);
		return CanvasUtils.createCircle(rad, Color.withAlpha(color, 50), 1, color);
	}
	
	public Image createImage(float cellSize, int color) {
		int width = (int)(cellSize * cols()), height = (int)(cellSize * rows());
		int padding = (int)(cellSize * 0.1f); 
		int rad = (int)(Math.min(width, height) * 0.1f);
		CanvasImage image = PlayN.graphics().createImage(width, height);
		image.canvas().setFillColor(color);
		image.canvas().fillRoundRect(padding, padding, 
				image.width() - padding * 2, image.height() - padding * 2, rad);
		image.canvas().setStrokeColor(Colors.BLACK);
		image.canvas().strokeRoundRect(padding, padding, 
				image.width() - padding * 2 - 1, image.height() - padding * 2 - 1, rad);
		return image;
	}
}
