package tuxkids.tuxblocks.core.defense;

import pythagoras.i.Point;

public abstract class DiscreteGridObject extends GridObject {
	protected Point coordinates = new Point();
	protected float baseDepth;
	
	public Point coordinates() {
		return coordinates;
	}
	
	protected float depthRow() {
		return coordinates.x;
	}
	
	protected float depthCol() {
		return coordinates.y;
	}
	
	public void updateDepth() {
		if (grid != null) {
			setDepth(baseDepth + (depthRow() * grid.cols() + depthCol()) * MAX_BASE_DEPTH);
		}
	}
	
	public boolean update(int delta) {
		updateDepth();
		return false;
	}
	
	protected void place(Grid grid, float baseDepth) {
		super.place(grid);
		this.baseDepth = baseDepth;
	}
	
	protected abstract void setDepth(float depth);
}
