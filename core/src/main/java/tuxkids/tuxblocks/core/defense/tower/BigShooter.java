package tuxkids.tuxblocks.core.defense.tower;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.PlayN;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.defense.projectile.Pea;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;

public class BigShooter extends Tower {

	@Override
	public int rows() {
		return 2;
	}

	@Override
	public int cols() {
		return 2;
	}

	@Override
	public int damage() {
		return 5;
	}

	@Override
	public int fireRate() {
		return 1000;
	}

	@Override
	public float range() {
		return 6;
	}

	@Override
	public Projectile createProjectile() {
		return new Pea();
	}

	@Override
	public Tower copy() {
		return new BigShooter();
	}

	@Override
	public String name() {
		return "B.I.G. Shooter";
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public int commonness() {
		return 3;
	}

}
