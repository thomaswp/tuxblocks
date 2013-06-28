package tuxkids.tuxblocks.core.defense.walker;

import pythagoras.f.FloatMath;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tuxkids.tuxblocks.core.defense.Grid;

public class FlipWalker extends BasicWalker {
	
	public FlipWalker(int maxHp, int walkCellTime) {
		super(maxHp, walkCellTime);
	}

	@Override
	protected void updateMovement(float perc) {
		int dx = -(coordinates.y - lastCoordinates.y);
		int dy = -(coordinates.x - lastCoordinates.x);

		float x = Math.max(coordinates.y, lastCoordinates.y) * grid.cellSize();
		float y = Math.max(coordinates.x, lastCoordinates.x) * grid.cellSize();
		
		layer.setTranslation(x, y);
		
		float scaleX = dx * FloatMath.cos(FloatMath.PI * perc);
		if (dx == 0) scaleX = 1;
		float scaleY = dy * FloatMath.cos(FloatMath.PI * perc);
		if (dy == 0) scaleY = 1;
		
		layer.setScale(scaleX, scaleY);
	}

	@Override
	public Walker copy() {
		return new FlipWalker(maxHp, walkCellTime);
	}

}
