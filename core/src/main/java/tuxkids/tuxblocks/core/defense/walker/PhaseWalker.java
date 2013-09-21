package tuxkids.tuxblocks.core.defense.walker;

/**
 * Walker that moves from cell to cell by fading out
 * and fading back in at the new cell location.
 */
public class PhaseWalker extends BasicWalker {

	public PhaseWalker(int maxHp, int walkCellTime) {
		super(maxHp, walkCellTime);
	}

	@Override
	protected void updateMovement(float perc) {
		alpha = Math.min(2 - perc * 2, 1);
		layer.setTranslation(lastCoordinates.y * grid.cellSize(), 
				lastCoordinates.x * grid.cellSize());
	}

	@Override
	public Walker copy() {
		return new PhaseWalker(maxHp, walkCellTime).setLevel(level);
	}
}
