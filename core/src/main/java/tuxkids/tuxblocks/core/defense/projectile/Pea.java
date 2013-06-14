package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.ImageLayer;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Pea extends Projectile {

	@Override
	public float speed() {
		return 0.5f;
	}

	@Override
	public ImageLayer createLayer() {
		return graphics().createImageLayer(
				CanvasUtils.createCircle(3, Colors.LIGHT_GRAY, 1, Colors.BLACK));
	}

}
