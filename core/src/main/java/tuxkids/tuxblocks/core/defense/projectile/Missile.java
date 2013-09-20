package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.Image;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.effect.MissileExplosion;
import tuxkids.tuxblocks.core.defense.tower.BigShooter;

/**
 * The {@link BodyProjectile} fired from {@link BigShooter}s.
 */
public class Missile extends BodyProjectile {

	private int level;
	
	public Missile(int level) {
		this.level = level;
	}
	
	@Override
	public float maxSpeed() {
		return 0.1f;
	}

	@Override
	public Image createImage() {
		return assets().getImage(Constant.IMAGE_PATH + "missile.png");
	}

	@Override
	public float acceleration() {
		return 0.003f;
	}
	
	
	@Override
	public void onFinish() {
		super.onFinish();
		new MissileExplosion(grid, position, level);
		Audio.se().play(Constant.SE_BOOM);
	}
}
