package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.Image;
import playn.core.ImageLayer;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Pea extends BodyProjectile {

	@Override
	public float speed() {
		return 0.5f;
	}

	@Override
	public Image createImage() {
		return CanvasUtils.createCircle(3, Colors.LIGHT_GRAY, 1, Colors.BLACK);
	}

}
