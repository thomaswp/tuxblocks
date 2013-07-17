package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.util.Clock;

public abstract class HorizontalModifierSprite extends ModifierBlockSprite {

	public HorizontalModifierSprite(int value) {
		super(value);
	}

	@Override
	protected void interpolateDefaultRect(Clock clock) {
		interpolateRect(layer.tx(), layer.ty(), modSize(), baseSize(), lerpBase(), clock.dt());
	}
	
	@Override
	protected boolean canRelease(boolean openSpace) {
		if (group == null) return false;
		return openSpace || !group.isModifiedVertically();
	}
}
