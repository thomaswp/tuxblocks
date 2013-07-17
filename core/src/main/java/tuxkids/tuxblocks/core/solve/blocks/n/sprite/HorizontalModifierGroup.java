package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

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
	protected void updateRect(float base, float dt) {
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
}
