package tuxkids.tuxblocks.core.defense.walker;

import pythagoras.f.Vector;
import pythagoras.i.Point;
import tuxkids.tuxblocks.core.defense.Grid;

public abstract class SlideWalker extends Walker {

	@Override
	protected void updateMovement(float perc) {
		float x = lerp(lastCoordinates.y * grid.getCellSize(), 
				coordinates.y * grid.getCellSize(), perc);
		float y = lerp(lastCoordinates.x * grid.getCellSize(), 
				coordinates.x * grid.getCellSize(), perc);
		sprite.setTranslation(x, y);
	}
	
}
