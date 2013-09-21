package tuxkids.tuxblocks.core.defense;

import pythagoras.i.Point;

/**
 * {@link GridObject}s that have a discrete position on the grid,
 * ie a specific cell or cells. Also determines depth based on row
 * and column to create a faux-3d effect when objects extend outside
 * of their cells.
 */
public abstract class DiscreteGridObject extends GridObject {
	protected Point coordinates = new Point();
	protected float baseDepth; // base depth to be added to the row/col calculated depth
	
	/** Discrete coordinates of this object */
	public Point coordinates() {
		return coordinates;
	}
	
	/** Row used in depth calculations */
	protected float depthRow() {
		return coordinates.x;
	}
	
	/** Column used in depth calculations */
	protected float depthCol() {
		return coordinates.y;
	}
	
	/** Updates this objects depth based on its row and column */
	private void updateDepth() {
		if (grid != null) {
			setDepth(baseDepth + (depthRow() * grid.cols() + depthCol()) * MAX_BASE_DEPTH);
		}
	}
	
	@Override
	public boolean update(int delta) {
		updateDepth();
		return false;
	}
	
	/** Places and associates this object with the given Grid */
	protected void place(Grid grid, float baseDepth) {
		super.place(grid);
		this.baseDepth = baseDepth;
	}
	
	/** Sets this object's Layer's depth */
	protected abstract void setDepth(float depth);
}
