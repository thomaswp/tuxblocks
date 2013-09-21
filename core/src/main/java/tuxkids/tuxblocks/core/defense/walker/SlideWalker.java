package tuxkids.tuxblocks.core.defense.walker;

/**
 * Most basic Walker animation, simply slides smoothly from
 * one cell to the next.
 */
public class SlideWalker extends BasicWalker {

	public SlideWalker(int maxHp, int walkCellTime) {
		super(maxHp, walkCellTime);
	}

	@Override
	protected void updateMovement(float perc) {
		float x = lerp(lastCoordinates.y * grid.cellSize(), 
				coordinates.y * grid.cellSize(), perc);
		float y = lerp(lastCoordinates.x * grid.cellSize(), 
				coordinates.x * grid.cellSize(), perc);
		layer.setTranslation(x, y);
	}

	@Override
	public Walker copy() {
		return new SlideWalker(maxHp, walkCellTime).setLevel(level);
	}
	
}
