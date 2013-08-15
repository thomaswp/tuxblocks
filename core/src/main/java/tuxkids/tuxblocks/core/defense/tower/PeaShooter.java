package tuxkids.tuxblocks.core.defense.tower;

import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.projectile.Pea;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;

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
	protected float baseDamage() {
		return 1;
	}
	
	@Override
	protected float damagePerLevel() {
		return 0;
	}

	@Override
	public int fireRate() {
		return 650 - upgradeLevel * 150;
	}

	@Override
	public float range() {
		return 5;
	}

	@Override
	public Projectile createProjectile() {
		return new Pea();
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

	@Override
	public int upgradeCost() {
		return 1;
	}

	private static int pop = 0;
	@Override
	protected boolean fire() {
		boolean fire = super.fire();
		if (fire) {
			Audio.se().play(Constant.SEPop(pop++), 0.5f);
			pop %= 2;
		}
		return fire;
	}

	@Override
	public TowerType type() {
		return TowerType.PeaShooter;
	}
}
