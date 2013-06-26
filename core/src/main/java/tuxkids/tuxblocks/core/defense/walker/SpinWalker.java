package tuxkids.tuxblocks.core.defense.walker;

import pythagoras.f.FloatMath;

public abstract class SpinWalker extends Walker {

	@Override
	protected void updateMovement(float perc) {
		float x = lerp(lastCoordinates.y * grid.cellSize(), 
				coordinates.y * grid.cellSize(), perc);
		float y = lerp(lastCoordinates.x * grid.cellSize(), 
				coordinates.x * grid.cellSize(), perc);
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
		layer.setTranslation(x + layer.originX(), y + layer.originY());
		layer.setRotation(perc * FloatMath.PI);
	}
}
