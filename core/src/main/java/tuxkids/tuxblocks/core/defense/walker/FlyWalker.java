package tuxkids.tuxblocks.core.defense.walker;

import pythagoras.f.FloatMath;
import pythagoras.i.Point;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.Pathing;

public class FlyWalker extends BasicWalker {
	
	public FlyWalker(int maxHp, int walkCellTime) {
		super(maxHp, walkCellTime);
	}
	
	@Override
	public Walker place(Grid grid, Point coordinates, Point destination, float depth) {
		super.place(grid, coordinates, destination, depth);
		refreshPath();
		return this;
	}

	@Override
	public int exp() {
		return super.exp() * 3 / 2;
	}
	
	@Override
	public void refreshPath() {
		path = Pathing.getPath(grid, coordinates, destination, true);
		path.remove(0);
	}

	@Override
	protected void updateMovement(float perc) {
		float x = lerp(lastCoordinates.y * grid.cellSize(), 
				coordinates.y * grid.cellSize(), perc);
		float y = lerp(lastCoordinates.x * grid.cellSize(), 
				coordinates.x * grid.cellSize(), perc);
		float offX = FloatMath.cos(perc * 2 * FloatMath.PI) * grid.cellSize() / 4;
		float offY = FloatMath.sin(perc * 2 * FloatMath.PI) * grid.cellSize() / 4;
		layer.setTranslation(x + offX, y + offY);
	}

	@Override
	public Walker copy() {
		return new FlyWalker(maxHp, walkCellTime);
	}
}
