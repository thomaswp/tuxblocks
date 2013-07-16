package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.Layer;
import playn.core.Pointer.Listener;
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

	public void addBlockListener(BlockListener listener) {
		modifiers.addBlockListenerListener(listener);
	}
	
	@Override
	public void update(int delta) {
		modifiers.update(delta);
		BlockGroupSprite newMods = modifiers.updateParentModifiers();
		if (newMods != modifiers) {
			if (newMods != null) {
				groupLayer.add(newMods.layer());
				newMods.layer().setDepth(modifiers.layer().depth());
				modifiers.layer().destroy();
				modifiers = (HorizontalBlockGroupSprite)newMods;
			}
		}
	}

	@Override
	public void paint(Clock clock) {
		modifiers.updateParentRect(this);
		modifiers.paint(clock);
	}
	
	public String hierarchy() {
		return toString() + "\n" + modifiers.hierarchy(1);
	}
	
	public interface BlockListener {
		
	}
}
