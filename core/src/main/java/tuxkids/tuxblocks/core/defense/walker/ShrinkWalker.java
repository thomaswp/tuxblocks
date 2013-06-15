package tuxkids.tuxblocks.core.defense.walker;

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
		sprite.setOrigin(sprite.width() / 2, sprite.height() / 2);
		sprite.setScale((float) Math.abs(Math.cos(Math.PI * perc)));
		sprite.setTranslation(coords.y * grid.getCellSize() + sprite.originX(), 
				coords.x * grid.getCellSize() + sprite.originY());
	}

}
