package tuxkids.tuxblocks.core.defense.walker.buff;

import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

/**
 * An effect that can be attached to a {@link Walker} to affect
 * it over a given timespan. Currently only supports affecting a
 * Walker's speed, but can be updated to include other affects by 
 * updating Walker to include the affect. See {@link Walker#update(int)}
 * for an example of how the {@link Buff#modifySpeed(float)} method
 * is incorporated.
 */
public abstract class Buff extends PlayNObject implements Hashable {
	
	/** Returns how long (in ms) this buff should last */
	protected abstract int lifespan();
	/** Modifies the given delta-time to be slowed or sped up by this Buff */
	public abstract float modifySpeed(float dt);
	
	protected int currentLife = lifespan();
	
	/** The Tower that caused this Buff */
	private Tower cause;
	
	public Buff(Tower cause) {
		this.cause = cause;
	}
	
	public boolean update(int delta) {
		currentLife -= delta;
		return currentLife <= 0;
	}

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(getClass());
		hashCode.addField(cause);
	}
}
