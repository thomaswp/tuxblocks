package tuxkids.tuxblocks.core.defense.walker;

/** 
 * Basic implementation of the {@link Walker} class that takes the
 * maxHp and walkCellTime as constructor parameters.
 */
public abstract class BasicWalker extends Walker {

	protected final int maxHp, walkCellTime;
	
	public BasicWalker(int maxHp, int walkCellTime) {
		this.maxHp = maxHp;
		this.walkCellTime = walkCellTime;
	}
	
	@Override
	public int maxHpBase() {
		return maxHp;
	}

	@Override
	public int walkCellTime() {
		return walkCellTime;
	}
}
