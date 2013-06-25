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
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.GridObject;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public abstract class Tower extends GridObject {
	
	protected Grid grid;
	protected Point coordinates = new Point();
	protected ImageLayerTintable layer;
	
	private int fireTimer;
	private Vector position = new Vector();
	private int id;
	
	public abstract int rows();
	public abstract int cols();
	public abstract int damage();
	public abstract int fireRate();
	public abstract float range();
	public abstract Projectile createProjectile();
	public abstract Tower copy();
	public abstract String name();
	public abstract int cost();
	public abstract int commonness();

	private static int nextTowerId;
	private static HashMap<Class<?>, Integer> towerIds =
			new HashMap<Class<?>, Integer>();
	
	private final static Tower[] towers = new Tower[] {
		new PeaShooter(),
		new BigShooter(),
		new VerticalWall(),
		new HorizontalWall(),
	};
	private final static List<Tower> towerBag;
	static {
		towerBag = new ArrayList<Tower>();
		for (Tower tower : towers) 
			for (int i = 0; i < tower.commonness(); i++) 
				towerBag.add(tower);
	}
	
	public static Tower[] towers() {
		return towers;
	}

	public float width() {
		return cols() * grid.getCellSize();
	}
	
	public float height() {
		return rows() * grid.getCellSize();
	}
	
	public ImageLayerTintable layer() {
		return layer;
	}
	
	public Layer layerAddable() {
		return layer.layer();
	}
	
	public Point coordinates() {
		return coordinates;
	}
	
	public Vector position() {
		return position;
	}
	
	public int id() {
		return id;
	}
	
	public static Tower getTowerById(int id) {
		return towers[id];
	}

	public static Tower randomTower() {
		return towerBag.get((int)(Math.random() * towerBag.size()));
	}
	
	public static int towerCount() {
		return towers.length;
	}
	
	public Tower() {
		Integer id = towerIds.get(getClass()); 
		if (id == null) {
			id = nextTowerId++;
			towerIds.put(getClass(), id);
		}
		this.id = id;
	}
	
	public void setCoordinates(Point coordinates) {
		setCoordinates(coordinates.x, coordinates.y);
	}
	
	public void setCoordinates(int row, int col) {
		coordinates.setLocation(row, col);
		layer.setTranslation(col * grid.getCellSize(), row * grid.getCellSize());
		position.set(col * grid.getCellSize() + width() / 2, 
				row * grid.getCellSize() + height() / 2);
	}
	
	public Tower preview(Grid grid) {
		this.grid = grid;
		layer = new ImageLayerTintable(
				createImage(grid.getCellSize(), 
						Colors.WHITE));
		layer.setTint(grid.towerColor());
		return this;
	}
	
	public Tower place(Grid grid, Point coordinates) {
		preview(grid);
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
		int fireRate = fireRate();
		if (fireRate > 0 && fireTimer > fireRate) {
			if (fire()) {
				fireTimer -= fireRate;
			} else {
				fireTimer = fireRate;
			}
		}
		float perc = FloatMath.pow((float)fireTimer / fireRate(), 0.5f);
		perc = Math.min(perc, 1);
		layer.setTint(grid.towerColor(), Colors.GRAY, perc);
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
		float rad = range() * grid.getCellSize();
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
