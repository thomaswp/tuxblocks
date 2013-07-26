package tuxkids.tuxblocks.core.solve.blocks;


public abstract class VerticalModifierBlock extends ModifierBlock {

	public VerticalModifierBlock(int value) {
		super(value);
	}
	
	public VerticalModifierBlock(VerticalModifierBlock inverse) {
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
