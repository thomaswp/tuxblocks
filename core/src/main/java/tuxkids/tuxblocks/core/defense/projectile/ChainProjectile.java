package tuxkids.tuxblocks.core.defense.projectile;

import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.walker.Walker;

/**
 * A {@link ConnectionProjectile} that chains its effect and
 * hit further targets after damage is dealt.
 */
public abstract class ChainProjectile extends ConnectionProjectile {
	
	private int hits; // how many more hits this projectile has
	private float damageFactor; // what percent of the original damage this hit does
	private float rangeFactor; // what percent of the original range this projectile has
	private float damageReduceFactor; // the factor by which damage is reduced each hit
	private float rangeReduceFactor; // the factor by which range is reduced each hit
	private ChainProjectile parent; // the projectile that spawned this one, or null if it's the first
	private Vector projectileStart = null; // the location to start this projectile
	
	private boolean hasChained; 
	
	/** Returns the modified range of this projectile */
	public float range() {
		return source.range() * rangeFactor;
	}
	
	/** Returns true if the given Walker is already part of this projectile chain */
	public boolean partOfChain(Walker walker) {
		if (target == walker) return true;
		if (parent == null) return false;
		return parent.partOfChain(walker);
	}
	
	@Override
	public Vector sourcePosition() {
		if (parent == null) {
			// if this it the first projectile
			if (projectileStart == null) {
				// cache the position in case the tower is destroyed
				projectileStart = source.projectileStart();
			}
			return projectileStart;
		} else {
			// otherwise we start at the position of the parent's target
			return parent.target.position();
		}
	}
	
	/** Creates a new copy of this projectile, reduced from another hit */
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
		if (hits == 0) hasChained = true; // don't chain anymore
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
