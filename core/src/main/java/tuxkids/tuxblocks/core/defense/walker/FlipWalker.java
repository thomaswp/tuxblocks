package tuxkids.tuxblocks.core.defense.walker;

import pythagoras.f.FloatMath;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tuxkids.tuxblocks.core.defense.Grid;

public abstract class FlipWalker extends Walker {
	@Override
	protected void updateMovement(float perc) {
		int dx = -(coordinates.y - lastCoordinates.y);
		int dy = -(coordinates.x - lastCoordinates.x);

		float x = Math.max(coordinates.y, lastCoordinates.y) * grid.getCellSize();
		float y = Math.max(coordinates.x, lastCoordinates.x) * grid.getCellSize();
		
		layer.setTranslation(x, y);
		
		float scaleX = dx * FloatMath.cos(FloatMath.PI * perc);
		if (dx == 0) scaleX = 1;
		float scaleY = dy * FloatMath.cos(FloatMath.PI * perc);
		if (dy == 0) scaleY = 1;
		
		layer.setScale(scaleX, scaleY);
	}

}
