package tuxkids.tuxblocks.core.defense.walker.buff;

import tuxkids.tuxblocks.core.defense.tower.Tower;

public class Frozen extends Buff {

	public Frozen(Tower cause) {
		super(cause);
	}

	@Override
	protected int lifespan() {
		return 1000;
	}

	@Override
	public float modifySpeed(float dt) {
		return dt * 0.5f;
	}
}
