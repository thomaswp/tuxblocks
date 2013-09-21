package tuxkids.tuxblocks.core.defense.walker;

import pythagoras.f.FloatMath;

/**
 * Walker that spins as it moves from cell to cell.
 */
public class SpinWalker extends BasicWalker {

	public SpinWalker(int maxHp, int walkCellTime) {
		super(maxHp, walkCellTime);
	}

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

	@Override
	public Walker copy() {
		return new SpinWalker(maxHp, walkCellTime).setLevel(level);
	}
}
