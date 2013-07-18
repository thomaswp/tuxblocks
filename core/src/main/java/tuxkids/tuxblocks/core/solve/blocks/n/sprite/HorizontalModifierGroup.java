package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.solve.blocks.n.BlockGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.HorizontalGroup;

public class HorizontalModifierGroup extends ModifierGroup {

	
	public HorizontalModifierGroup(Sprite parent) {
		super(parent);
	}

	@Override
	protected void updateChildren(float base, float dt) {
		float x = parentRect.maxX();
		for (ModifierBlockSprite child : children) {
			child.interpolateRect(x, parentRect.y, modSize(), parentRect.height, base, dt);
			//x = child.right();
			x += modSize();
		}
	}
	
	@Override
	protected void updateRect() {
		rect.y = parentRect.y;
		rect.x = parentRect.x;
		rect.width = parentRect.width + children.size() * modSize();
		rect.height = parentRect.height;
	}

	@Override
	protected ModifierGroup createModifiers() {
		return new VerticalModifierGroup(this);
	}

	@Override
	protected boolean canAdd(ModifierBlockSprite sprite) {
		return sprite instanceof HorizontalModifierSprite;
	}

	@Override
	public void addExpression(NumberBlockSprite sprite, boolean snap) {
		if (canAddExpressionImpl(sprite)) {
			addChild(sprite.proxyFor());
			if (snap) snapChildren(); //TODO: maybe better implementation, if I ever use this option
			sprite.layer().destroy();
			return;
		}
		super.addExpression(sprite, snap);
	}

	public boolean canAddExpression(NumberBlockSprite sprite) {
		if (canAddExpressionImpl(sprite)) {
			return true;
		}
		return super.canAddExpression(sprite);
	}
	
	private boolean canAddExpressionImpl(NumberBlockSprite sprite) {
		if (modifiers != null || sprite.modifiers.modifiers == null) {
			List<VerticalModifierSprite> myMods = new ArrayList<VerticalModifierSprite>(), 
					spriteMods = new ArrayList<VerticalModifierSprite>();
			addVerticalModifiers(myMods);
			sprite.modifiers.addVerticalModifiers(spriteMods);
			if (myMods.equals(spriteMods)) {
				return true;
			}
		}
		return false;
	}
}
