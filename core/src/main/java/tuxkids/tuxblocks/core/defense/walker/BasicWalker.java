package tuxkids.tuxblocks.core.defense.walker;

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
