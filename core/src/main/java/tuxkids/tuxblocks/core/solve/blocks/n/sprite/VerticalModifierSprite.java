package tuxkids.tuxblocks.core.solve.blocks.n.sprite;


public abstract class VerticalModifierSprite extends ModifierBlockSprite {

	public VerticalModifierSprite(int value) {
		super(value);
	}

	@Override
	protected float defaultWidth() {
		return baseSize();
	}
	
	@Override
	protected float defaultHeight() {
		return modSize();
	}
	
	@Override
	protected boolean canRelease(boolean openSpace) {
		if (group == null) return false;
		return !group.isModifiedHorizontally();
	}

	public final ModifierBlockSprite copy() {
		ModifierBlockSprite copy = copyChild();
		copy.blockListener = blockListener;
		return copy;
	}
	
	protected abstract ModifierBlockSprite copyChild();
}
