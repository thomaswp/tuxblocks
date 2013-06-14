package tuxkids.tuxblocks.core.defense;

import playn.core.util.Clock;
import tuxkids.tuxblocks.core.PlayNObject;

public abstract class GridObject extends PlayNObject {
	public abstract boolean update(int delta);
	public abstract void paint(Clock clock);
}
