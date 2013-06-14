package tuxkids.tuxblocks.core.defense.tower;

import playn.core.ImageLayer;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.GridObject;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;

public abstract class Tower extends GridObject {
	
	protected Grid grid;
	protected Point coordinates = new Point();
	protected ImageLayer layer;
	
	private int fireTimer;
	private Vector position = new Vector();
	
	public abstract int rows();
	public abstract int cols();
	public abstract int damage();
	public abstract int fireRate();
	public abstract float range();
	public abstract Projectile createProjectile();
	protected abstract ImageLayer createLayer();

	public float width() {
		return cols() * grid.getCellSize();
	}
	
	public float height() {
		return rows() * grid.getCellSize();
	}
	
	public ImageLayer layer() {
		return layer;
	}
	
	public Point coordinates() {
		return coordinates;
	}
	
	public Vector position() {
		return position;
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
	
	public Tower preview(Grid grid, Point coordinates) {
		this.grid = grid;
		if (layer == null) layer = createLayer();
		setCoordinates(coordinates);
		return this;
	}
	
	public Tower place(Grid grid, Point coordinates) {
		preview(grid, coordinates);
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < cols(); j++) {
				grid.getPassability()[i + coordinates.x][j + coordinates.y] = false;
			}
		}
		return this;
	}
	
	public boolean update(int delta) {
		fireTimer += delta;
		if (fireTimer > fireRate()) {
			fireTimer -= fireRate();
			fire();
		}
		return false;
	}
	
	public void paint(Clock clock) {
		
	}
	
	protected void fire() {
		grid.fireProjectile(this);
	}
	
	public boolean canPlace(int row, int col) {
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < cols(); j++) {
				if (!grid.getPassability()[i + row][j + col]) return false;
			}
		}
		return true;
	}
}
