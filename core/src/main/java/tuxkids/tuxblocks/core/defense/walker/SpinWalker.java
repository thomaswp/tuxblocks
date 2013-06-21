package tuxkids.tuxblocks.core.defense.walker;

public abstract class SpinWalker extends Walker {

	@Override
	protected void updateMovement(float perc) {
		float x = lerp(lastCoordinates.y * grid.getCellSize(), 
				coordinates.y * grid.getCellSize(), perc);
		float y = lerp(lastCoordinates.x * grid.getCellSize(), 
				coordinates.x * grid.getCellSize(), perc);
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
		layer.setTranslation(x + layer.originX(), y + layer.originY());
		layer.setRotation((float) (perc * Math.PI));
	}
}
