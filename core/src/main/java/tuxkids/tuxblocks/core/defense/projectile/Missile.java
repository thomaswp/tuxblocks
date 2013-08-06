package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.Image;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.effect.MissileExplosion;

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
	}
}
