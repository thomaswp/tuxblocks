package tuxkids.tuxblocks.core.defense.tower;

import tuxkids.tuxblocks.core.defense.projectile.Missile;
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
	protected float baseDamage() {
		return 5;
	}
	
	@Override
	protected float damagePerLevel() {
		return 2;
	}

	@Override
	public int fireRate() {
		return 1000 - (upgradeLevel - 1) * 150;
	}

	@Override
	public float range() {
		return 6;
	}

	@Override
	public Projectile createProjectile() {
		return new Missile(upgradeLevel - 1);
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
		return 2;
	}

	@Override
	public int commonness() {
		return 3;
	}
	
	@Override
	public float splashRadius() {
		return 1.5f + 0.5f * upgradeLevel;
	}

	@Override
	public int upgradeCost() {
		return 2;
	}

	@Override
	public TowerType type() {
		return TowerType.BigShooter;
	}
}
