package tuxkids.tuxblocks.core.effect;

import playn.core.Image;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Explosion extends ParticleEffect {

	public Explosion() {
		super(100, CanvasUtils.createCircle(2, Colors.RED));
	}

}
