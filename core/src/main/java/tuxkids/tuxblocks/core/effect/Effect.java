package tuxkids.tuxblocks.core.effect;

import playn.core.GroupLayer;
import pythagoras.f.Vector;
import tripleplay.particle.Particles;
import tuxkids.tuxblocks.core.PlayNObject;

public class Effect extends PlayNObject {
	private GroupLayer layer;
	private Vector position = new Vector();
	
	public Effect() {
		layer = graphics().createGroupLayer();
	}
}
