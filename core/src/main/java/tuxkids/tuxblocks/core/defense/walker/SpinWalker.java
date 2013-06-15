package tuxkids.tuxblocks.core.defense.walker;

public abstract class SpinWalker extends Walker {

	@Override
	protected void updateMovement(float perc) {
		float x = lerp(lastCoordinates.y * grid.getCellSize(), 
				coordinates.y * grid.getCellSize(), perc);
		float y = lerp(lastCoordinates.x * grid.getCellSize(), 
				coordinates.x * grid.getCellSize(), perc);
		sprite.setOrigin(sprite.width() / 2, sprite.height() / 2);
		sprite.setTranslation(x + sprite.originX(), y + sprite.originY());
		sprite.setRotation((float) (perc * Math.PI));
	}
}
