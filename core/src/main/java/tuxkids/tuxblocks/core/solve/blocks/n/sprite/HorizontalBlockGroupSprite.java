package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import tuxkids.tuxblocks.core.solve.blocks.n.BlockGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.HorizontalGroup;

public class HorizontalBlockGroupSprite extends BlockGroupSprite {

	HorizontalGroup group;
	
	public HorizontalBlockGroupSprite(HorizontalGroup group, Sprite parent) {
		super(group, parent);
		this.group = group;
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
//		if (children.size() != 0) {
//			width = children.get(children.size() - 1).right();
//		}
		rect.height = parentRect.height;
	}

	@Override
	protected void init() {

	}

	int time;
	@Override
	public void update(int delta) {
		super.update(delta);
//		time += delta;
//		if (modifiers != null && time > 3000 && children.size() > 0) {
//			time = 0;
//			children.remove(0).layer.destroy();;
//		}
	}
}
