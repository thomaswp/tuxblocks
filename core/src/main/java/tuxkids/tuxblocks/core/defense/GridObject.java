package tuxkids.tuxblocks.core.defense;

import playn.core.util.Clock;
import tuxkids.tuxblocks.core.utils.PlayNObject;

public abstract class GridObject extends PlayNObject {
	
	public final static int MAX_BASE_DEPTH = 100;
	
	protected Grid grid;
	
	protected void place(Grid grid) {
		this.grid = grid;
	}
	
	public abstract boolean update(int delta);
	public abstract void paint(Clock clock);
}
