package tuxkids.tuxblocks.core.defense.tower;

import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.projectile.Snow;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.defense.walker.buff.Frozen;

public class Freezer extends Tower {

	@Override
	public int rows() {
		return 1;
	}

	@Override
	public int cols() {
		return 1;
	}

	@Override
	public float damage() {
		return 0.2f;
	}

	@Override
	public int fireRate() {
		return 100;
	}

	@Override
	public float range() {
		return 3.1f;
	}

	@Override
	public Projectile createProjectile() {
		return new Snow();
	}

	@Override
	public Tower copy() {
		return new Freezer();
	}

	@Override
	public String name() {
		return "Freezer";
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public int commonness() {
		return 3;
	}
	
	@Override
	public float splashRadius() {
		return 0.7f;
	}

	@Override
	public void addBuffs(Walker walker) {
		walker.addBuff(new Frozen(), true);
	}
}
