package tuxkids.tuxblocks.core.defense.tower;

import tuxkids.tuxblocks.core.defense.projectile.Projectile;

/**
 * Non-damaging 3x1 Tower that is used for mazing.
 */
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
	protected float baseDamage() {
		return 0;
	}
	
	@Override
	protected float damagePerLevel() {
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
	public String nameKey() {
		return key_verticalWall;
	}

	@Override
	public int cost() {
		return 2;
	}
	
	@Override
	public int upgradeCost() {
		return 0;
	}
	
	@Override
	public boolean canUpgrade() {
		return false;
	}

	@Override
	public TowerType type() {
		return TowerType.VerticalWall;
	}
}
