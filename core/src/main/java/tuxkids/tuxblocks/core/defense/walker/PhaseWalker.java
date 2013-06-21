package tuxkids.tuxblocks.core.defense.walker;

public abstract class PhaseWalker extends Walker {

	@Override
	protected void updateMovement(float perc) {
		alpha = Math.min(2 - perc * 2, 1);
		layer.setTranslation(lastCoordinates.y * grid.getCellSize(), 
				lastCoordinates.x * grid.getCellSize());
	}
}
