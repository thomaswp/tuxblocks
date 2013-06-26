package tuxkids.tuxblocks.core.defense.walker;

import pythagoras.f.FloatMath;
import pythagoras.i.Point;

public abstract class ShrinkWalker extends Walker {

	@Override
	protected void updateMovement(float perc) {
		Point coords;
		if (perc < 0.5f) {
			coords = lastCoordinates;
		} else {
			coords = coordinates;
		}
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
		layer.setScale(Math.abs(FloatMath.cos(FloatMath.PI * perc)));
		layer.setTranslation(coords.y * grid.cellSize() + layer.originX(), 
				coords.x * grid.cellSize() + layer.originY());
	}

}
