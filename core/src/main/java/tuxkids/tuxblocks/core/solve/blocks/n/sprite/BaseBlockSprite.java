package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.Layer;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.solve.blocks.n.BaseBlock;

public class BaseBlockSprite extends BlockSprite {
	
	protected BaseBlock block;
	protected HorizontalBlockGroupSprite modifiers;
	protected GroupLayer groupLayer;
	
	public Layer layerAddable() {
		return groupLayer;
	}
	
	public BaseBlockSprite(BaseBlock block) {
		this.block = block;
		layer = generateNinepatch(block.text(), Colors.WHITE);
		layer.setSize(baseSize(), baseSize());
		groupLayer = graphics().createGroupLayer();
		groupLayer.add(layer());
		
		modifiers = new HorizontalBlockGroupSprite(block.modifiers(), this);
		groupLayer.add(modifiers.layer());
	}

	@Override
	public void update(int delta) {
		modifiers.update(delta);
	}

	@Override
	public void paint(Clock clock) {
		modifiers.updateParentRect(this);
		modifiers.paint(clock);
	}
}
