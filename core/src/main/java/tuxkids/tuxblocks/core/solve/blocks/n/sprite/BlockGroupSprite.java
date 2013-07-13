package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.util.Clock;
import pythagoras.f.Rectangle;
import tuxkids.tuxblocks.core.solve.blocks.n.BlockGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.HorizontalGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.VerticalGroup;

public abstract class BlockGroupSprite extends Sprite {

	protected abstract void updateChildren(float base, float dt);
	protected abstract void updateRect(float base, float dt);
	protected abstract void parseChildren();
	
	protected GroupLayer layer;
	protected Rectangle rect = new Rectangle();
	protected Rectangle parentRect = new Rectangle();
	
	protected List<ModifierBlockSprite> children = new ArrayList<ModifierBlockSprite>();
	protected BlockGroupSprite modifiers;
	
	@Override
	public Layer layer() {
		return layer;
	}

	@Override
	public float x() {
		return rect.x;
	}
	
	@Override
	public float y() {
		return rect.y();
	}
	
	@Override
	public float width() {
		return rect.width;
	}

	@Override
	public float height() {
		return rect.height;
	}
	
	public BlockGroupSprite(BlockGroup<?> group, Sprite parent) {
		layer = graphics().createGroupLayer();
		for (ModifierBlock block : group.blocks()) {
			ModifierBlockSprite child = new ModifierBlockSprite(block);
			children.add(child);
			layer.add(child.layer());
		}
		parseChildren();
		updateParentRect(parent);
		updateChildren(0, 1);
		updateRect(0, 1);
		if (group.modifiers() != null) {
			if (group.modifiers() instanceof HorizontalGroup) {
				modifiers = new HorizontalBlockGroupSprite((HorizontalGroup) group.modifiers(), this);
			} else {
				modifiers = new VerticalBlockGroupSprite((VerticalGroup) group.modifiers(), this);
			}
			layer.add(modifiers.layer());
		}
	}
	
	protected void updateParentRect(Sprite parent) {
		parentRect.setBounds(parent.x(), parent.y(), parent.width(), parent.height());
	}

	@Override
	public void update(int delta) {
		for (ModifierBlockSprite sprite : children) {
			sprite.update(delta);
		}
		if (modifiers != null) {
			modifiers.update(delta);
		}
	}

	@Override
	public void paint(Clock clock) {
		updateChildren(0.995f, clock.dt());
		updateRect(0.995f, clock.dt());
		for (ModifierBlockSprite sprite : children) {
			sprite.paint(clock);
		}
		if (modifiers != null) {
			modifiers.updateParentRect(this);
			modifiers.paint(clock);
		}
	}

}
