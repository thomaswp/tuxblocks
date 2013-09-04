package tuxkids.tuxblocks.core.solve.blocks;

/**
 * A {@link ModifierBlock} that stacks vertically, such as a {@link TimesBlock}
 * or {@link OverBlock}.
 */
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
		// can't release if we don't have a group
		if (group == null) return true;
		// can always release in Build mode
		if (blockListener != null && blockListener.inBuildMode()) return true;
		// otherwise, we can release if there's only one expression on each size
		// and this block's group isn't modified horizontally (there's no addends)
		return !multiExpression && !group.isModifiedHorizontally();
	}
	
	@Override
	protected boolean shouldShowReleaseIndicator(boolean multiExpression) {
		// only flash if we have a group to be released from
		return group != null && super.shouldShowReleaseIndicator(multiExpression);
	}
}
