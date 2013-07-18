package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;

import playn.core.util.Clock;

public abstract class HorizontalModifierSprite extends ModifierBlockSprite {

	public HorizontalModifierSprite(int value) {
		super(value);
	}
	
	protected abstract int getPlusValue();

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
	protected BlockSprite getDraggingSprite() {
		if (group.isModifiedVertically()) {
			return getProxy(true);
		} else {
			return this;
		}
	}
	
	protected NumberBlockSpriteProxy getProxy(boolean snapToMySize) {
		NumberBlockSpriteProxy sprite = new NumberBlockSpriteProxy(getPlusValue(), this);
		if (snapToMySize) {
			sprite.interpolateRect(layer.tx(), layer.ty(), width(), height(), 0, 1);
			sprite.snapChildren();
		}
		if (group != null) {
			ArrayList<VerticalModifierSprite> modifiers = new ArrayList<VerticalModifierSprite>();
			group.addVerticalModifiers(modifiers);
			for (VerticalModifierSprite mod : modifiers) {
				sprite.addModifier(mod.copy(), true);
			}
		}
		return sprite;
	}
}
