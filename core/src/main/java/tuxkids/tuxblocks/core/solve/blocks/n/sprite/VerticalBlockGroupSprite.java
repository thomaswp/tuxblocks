package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.solve.blocks.n.TimesBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.VerticalGroup;

public class VerticalBlockGroupSprite extends BlockGroupSprite {

	protected VerticalGroup group;
	protected List<ModifierBlockSprite> timesBlocks;
	protected List<ModifierBlockSprite> divBlocks;
	
	public void parseChildren() {
		timesBlocks = new ArrayList<ModifierBlockSprite>();
		divBlocks = new ArrayList<ModifierBlockSprite>();
		for (ModifierBlockSprite child : children) {
			if (child.block instanceof TimesBlock) {
				timesBlocks.add(child);
			} else {
				divBlocks.add(child);
			}
		}
	}
	
	public VerticalBlockGroupSprite(VerticalGroup group, Sprite parent) {
		super(group, parent);
		this.group = group;
	}

	@Override
	protected void updateChildren(float base, float dt) {
		float y = parentRect.y - modSize();
		for (ModifierBlockSprite block : timesBlocks) {
			block.interpolateRect(parentRect.x, y, parentRect.width, modSize(), base, dt);
			y -= block.height();
		}

		y = parentRect.maxY();
		for (ModifierBlockSprite block : divBlocks) {
			block.interpolateRect(parentRect.x, y, parentRect.width, modSize(), base, dt);
			y += block.height();
		}
	}

	@Override
	protected void updateRect(float base, float dt) {
		//layer.setTy(lerpTime(layer.ty(), ty, base, dt, 1f));
//		layer.setTy(ty);
		rect.y = -timesBlocks.size() * modSize();
		
		rect.width = parentRect.width;
		rect.height = parentRect.height - rect.y + divBlocks.size() * modSize();
//		if (divBlocks.size() > 0) {
//			height = divBlocks.get(divBlocks.size() - 1).bottom();
//		}
	}

	int time;
	@Override
	public void update(int delta) {
		super.update(delta);
		time += delta;
		if (modifiers != null && time > 3000 && children.size() > 0) {
			time = 0;
			ModifierBlockSprite mod = children.remove(0);
			mod.layer.destroy();
			timesBlocks.remove(mod);
			divBlocks.remove(mod);
			
		}
	}
}
