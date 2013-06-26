package tuxkids.tuxblocks.core.defense.tower;

import playn.core.Image;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;

public class VerticalWall extends Tower {

	@Override
	public int rows() {
		return 3;
	}

	@Override
	public int cols() {
		return 1;
	}

	@Override
	public float damage() {
		return 0;
	}

	@Override
	public int fireRate() {
		return -1;
	}

	@Override
	public float range() {
		return 0;
	}

	@Override
	public Projectile createProjectile() {
		return null;
	}

	@Override
	public Tower copy() {
		return new VerticalWall();
	}

	@Override
	public String name() {
		return "Vertical Wall";
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public int commonness() {
		return 1;
	}

}
