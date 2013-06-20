package tuxkids.tuxblocks.core.defense.tower;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.defense.projectile.Lightning;
import tuxkids.tuxblocks.core.defense.projectile.Missile;
import tuxkids.tuxblocks.core.defense.projectile.Pea;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class PeaShooter extends Tower {

	@Override
	public int rows() {
		return 1;
	}

	@Override
	public int cols() {
		return 1;
	}

	@Override
	public int damage() {
		return 1;
	}

	@Override
	public int fireRate() {
		return 500;
	}

	@Override
	public float range() {
		return 5;
	}

	@Override
	public Projectile createProjectile() {
		return new Missile();
	}

	@Override
	public Tower copy() {
		return new PeaShooter();
	}

	@Override
	public String name() {
		return "Pea Shooter";
	}

	@Override
	public int cost() {
		return 1;
	}

	@Override
	public int commonness() {
		return 5;
	}

}
