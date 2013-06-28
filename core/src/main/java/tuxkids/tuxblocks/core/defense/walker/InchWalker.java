package tuxkids.tuxblocks.core.defense.walker;

public class InchWalker extends BasicWalker {

	public InchWalker(int maxHp, int walkCellTime) {
		super(maxHp, walkCellTime);
	}

	@Override
	protected void updateMovement(float perc) {
		float cellSize = grid.cellSize();
		float ox, oy, scaleX, scaleY, x, y;
		if (perc < 0.5f) {
			ox = (0.5f - (coordinates.y - lastCoordinates.y) / 2f) * cellSize;
			oy = (0.5f - (coordinates.x - lastCoordinates.x) / 2f) * cellSize;
			scaleX = Math.abs(perc * (ox / cellSize - 0.5f) * 4) + 1;
			scaleY = Math.abs(perc * (oy / cellSize - 0.5f) * 4) + 1;
			x = lastCoordinates.y * cellSize + ox;
			y = lastCoordinates.x * cellSize + oy;
		} else {
			ox = (0.5f + (coordinates.y - lastCoordinates.y) / 2f) * cellSize;
			oy = (0.5f + (coordinates.x - lastCoordinates.x) / 2f) * cellSize;
			scaleX = Math.abs((1 - perc) * (ox / cellSize - 0.5f) * 4) + 1;
			scaleY = Math.abs((1 - perc) * (oy / cellSize - 0.5f) * 4) + 1;
			x = coordinates.y * cellSize + ox;
			y = coordinates.x * cellSize + oy;
		}
		layer.setOrigin(ox, oy);
		layer.setTranslation(x, y);
		layer.setScale(scaleX, scaleY);
	}

	@Override
	public Walker copy() {
		return new InchWalker(maxHp, walkCellTime);
	}
}
