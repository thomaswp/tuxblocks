package tuxkids.tuxblocks.core.defense.projectile;

import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.walker.Walker;

public abstract class ChainProjectile extends ConnectionProjectile {
	
	private int hits;
	private float damageReduceFactor;
	private float rangeReduceFactor;
	private float damageFactor;
	private float rangeFactor;
	private ChainProjectile parent;
	private Vector projectileStart = null;
	
	private boolean hasChained; 
	
	public float range() {
		return source.range() * rangeFactor;
	}
	
	public boolean partOfChain(Walker walker) {
		if (target == walker) return true;
		if (parent == null) return false;
		return parent.partOfChain(walker);
	}
	
	@Override
	public Vector sourcePosition() {
		if (parent == null) {
			if (projectileStart == null) {
				// cache the position in case the tower is destroyed
				projectileStart = source.projectileStart();
			}
			return projectileStart;
		} else {
			return parent.target.position();
		}
	}
	
	public ChainProjectile createProjectile() {
		return copy(hits - 1, damageReduceFactor, rangeReduceFactor);
	}
	
	public ChainProjectile(int hits, float damageReduceFactor, float rangeReduceFactor) {
		this.hits = hits;
		this.rangeFactor = 1;
		this.damageFactor = 1;
		this.damageReduceFactor = damageReduceFactor;
		this.rangeReduceFactor = rangeReduceFactor;
	}
	
	protected abstract ChainProjectile copy(int hits, float damageReduceFactor, float rangeReduceFactor);
	
	public void place(Grid grid, Walker target, ChainProjectile parent) {
		super.place(grid, target, parent.source);
		
		this.parent = parent;
		this.damageFactor = parent.damageFactor * damageReduceFactor;
		this.rangeFactor = parent.rangeFactor * rangeReduceFactor;
		this.damage = source.damage() * damageFactor;
		
		if (Math.round(damage * damageFactor) == 0) hits = 0;
		if (hits == 0) hasChained = true;
	}
	
	@Override
	public boolean doUpdate(int delta) {
		if (!hasChained && progress() > 0.4f) {
			boolean fired = grid.fireProjectile(this);
			hasChained = fired; 
		}
		return super.doUpdate(delta);
	}
}
