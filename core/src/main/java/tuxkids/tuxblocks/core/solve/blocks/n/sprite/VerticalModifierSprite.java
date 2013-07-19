package tuxkids.tuxblocks.core.solve.blocks.n.sprite;


public abstract class VerticalModifierSprite extends ModifierBlockSprite {

	public VerticalModifierSprite(int value) {
		super(value);
	}
	
	public VerticalModifierSprite(VerticalModifierSprite inverse) {
		super(inverse);
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
	protected boolean canRelease(boolean multiExpression) {
		if (group == null) return false;
		return !multiExpression && !group.isModifiedHorizontally();
	}
}
