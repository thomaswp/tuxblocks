package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;

/**
 * Represents a horizontally stacked {@link ModifierBlock}, such as
 * a {@link PlusBlock} or {@link MinusBlock}.
 */
public abstract class HorizontalModifierBlock extends ModifierBlock {

	protected abstract void setPlusValue(int value);
	protected abstract int plusValue();
	
	public HorizontalModifierBlock(int value) {
		super(value);
	}
	
	protected HorizontalModifierBlock (HorizontalModifierBlock inverse) {
		super(inverse);
	}

	@Override
	protected float defaultWidth() {
		return modSize();
	}
	
	@Override
	protected float defaultHeight() {
		return baseSize();
	}
	
	@Override
	protected boolean canRelease(boolean openSpace) {
		if (group == null) return true;
		return true;
	}
	
	@Override
	protected boolean shouldShowReleaseIndicator(boolean multiExpression) {
		return group != null && super.shouldShowReleaseIndicator(multiExpression);
	}
	
	@Override
	protected Block getDraggingSprite() {
		if (group != null && group.isModifiedVertically()) {
			// if we need to turn this modifier into a NumberBlock...
			return getProxy(true);
		} else {
			return this;
		}
	}
	
	/** 
	 * Returns a {@link NumberBlock} equivalent of this modifier with
	 * the appropriate modifiers. Optionally snaps the NumberBlock to this
	 * blocks size.
	 */
	protected NumberBlockProxy getProxy(boolean snapToMySize) {
		NumberBlockProxy sprite = new NumberBlockProxy(plusValue(), this);
		if (hasSprite()) {
			sprite.initSprite();
			if (snapToMySize) {
				sprite.interpolateRect(layer.tx(), layer.ty(), width(), height(), 0, 1);
				sprite.snapChildren();
			}
		}
		if (group != null) {
			ArrayList<VerticalModifierBlock> modifiers = new ArrayList<VerticalModifierBlock>();
			group.addVerticalModifiersTo(modifiers);
			for (VerticalModifierBlock mod : modifiers) {
				sprite.addModifier((ModifierBlock) mod.copy(true), true);
			}
		}
		return sprite;
	}
	
	@Override
	public void setValue(int value) {
		if (value < 0) {
			// instead of having +-3 or --3, we convert to a minus block
			// or vice versa
			super.setValue(-value);
			showInverse();
			if (group != null) {
				ModifierGroup group = this.group;
				group.removeChild(this);
				group.addChild(inverse);
			}
		} else {
			super.setValue(value);
		}
	}
}
