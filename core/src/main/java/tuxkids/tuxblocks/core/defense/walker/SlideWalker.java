package tuxkids.tuxblocks.core.defense.walker;

import pythagoras.f.Vector;
import pythagoras.i.Point;
import tuxkids.tuxblocks.core.defense.Grid;

public abstract class SlideWalker extends Walker {

	@Override
	protected void updateMovement(float perc) {
		float x = lerp(lastCoordinates.y * grid.cellSize(), 
				coordinates.y * grid.cellSize(), perc);
		float y = lerp(lastCoordinates.x * grid.cellSize(), 
				coordinates.x * grid.cellSize(), perc);
		layer.setTranslation(x, y);
	}
	
}
