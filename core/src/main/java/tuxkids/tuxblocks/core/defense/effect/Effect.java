package tuxkids.tuxblocks.core.defense.effect;

import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.GridObject;

/**
 * An generic class for visual effects that can be placed on the {@link Grid}
 */
public abstract class Effect extends GridObject {
	
	protected GroupLayer layer;
	private Vector position = new Vector();
	
	public Layer layer() {
		return layer;
	}
	
	public Vector position() {
		return position;
	}
	
	public Effect() {
		layer = graphics().createGroupLayer();
	}
	
	@Override
	public void paint(Clock clock) {
		layer.setTranslation(position.x, position.y);
	}
}
