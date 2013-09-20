package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.Image;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.defense.tower.PeaShooter;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

/**
 * The {@link BodyProjectile} fired from {@link PeaShooter}s.
 */
public class Pea extends BodyProjectile {

	@Override
	public float maxSpeed() {
		return 0.5f;
	}

	@Override
	public Image createImage() {
		return CanvasUtils.createCircleCached(3, Colors.LIGHT_GRAY, 1, Colors.BLACK);
	}

	@Override
	public float acceleration() {
		return 1f;
	}

}
