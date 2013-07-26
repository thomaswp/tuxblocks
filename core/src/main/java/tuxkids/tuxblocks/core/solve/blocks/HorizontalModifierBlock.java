package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;

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
		if (group == null) return false;
		return true;
	}
	
	@Override
	protected Block getDraggingSprite() {
		if (group.isModifiedVertically()) {
			return getProxy(true);
		} else {
			return this;
		}
	}
	
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
}
