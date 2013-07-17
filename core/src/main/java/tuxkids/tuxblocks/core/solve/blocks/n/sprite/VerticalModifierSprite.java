package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.util.Clock;

public abstract class VerticalModifierSprite extends ModifierBlockSprite {

	public VerticalModifierSprite(int value) {
		super(value);
	}

	@Override
	protected void interpolateDefaultRect(Clock clock) {
		interpolateRect(layer.tx(), layer.ty(), baseSize(), modSize(), lerpBase(), clock.dt());
	}
	
	@Override
	protected boolean canRelease(boolean openSpace) {
		if (group == null) return false;
		return !group.isModifiedHorizontally();
	}
}
