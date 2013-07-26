package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.Image;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Pea extends BodyProjectile {

	@Override
	public float maxSpeed() {
		return 0.5f;
	}

	@Override
	public Image createImage() {
		return CanvasUtils.createCircle(3, Colors.LIGHT_GRAY, 1, Colors.BLACK);
	}

	@Override
	public float acceleration() {
		return 1f;
	}

}
