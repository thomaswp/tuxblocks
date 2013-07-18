package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.solve.blocks.n.TimesBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.VerticalBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.VerticalGroup;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public class VerticalModifierGroup extends ModifierGroup {

	protected List<ModifierBlockSprite> timesBlocks;
	protected List<ModifierBlockSprite> divBlocks;
	
	public VerticalModifierGroup(Sprite parent) {
		super(parent);
		timesBlocks = new ArrayList<ModifierBlockSprite>();
		divBlocks = new ArrayList<ModifierBlockSprite>();
	}

	@Override
	protected void updateChildren(float base, float dt) {
		float y = parentRect.y - modSize();
		for (ModifierBlockSprite block : timesBlocks) {
			block.interpolateRect(rect.x, y, rect.width, parentRect.maxY() - y, base, dt);
			y -= modSize();
		}

//		y = parentRect.maxY() + modSize(); // for times-like wrap shape
		y = parentRect.maxY();
		for (ModifierBlockSprite block : divBlocks) {
//			block.interpolateRect(rect.x, parentRect.centerY(), rect.width, y - parentRect.centerY(), base, dt);
			block.interpolateRect(rect.x, y, rect.width, modSize(), base, dt);
			y += block.height();
		}
	}

	@Override
	protected void updateRect() {
		rect.y = parentRect.y - timesBlocks.size() * modSize();
		
		rect.x = parentRect.x;
		rect.width = parentRect.width;
		if (timesBlocks.size() > 0) {
			rect.x -= wrapSize();
			rect.width += 2 * wrapSize();
		}
		rect.height = parentRect.height + children.size() * modSize();
	}
	
	@Override
	protected void removeChild(ModifierBlockSprite child) {
		super.removeChild(child);
		timesBlocks.remove(child);
		divBlocks.remove(child);
	}
	
	@Override
	protected void addChild(ModifierBlockSprite child) {
		super.addChild(child);
		if (child instanceof TimesBlockSprite) {
			timesBlocks.add(child);
		} else {
			divBlocks.add(child);
		}
	}

	@Override
	protected ModifierGroup createModifiers() {
		return new HorizontalModifierGroup(this);
	}

	@Override
	protected boolean canAdd(ModifierBlockSprite sprite) {
		return sprite instanceof VerticalModifierSprite;
	}
}
