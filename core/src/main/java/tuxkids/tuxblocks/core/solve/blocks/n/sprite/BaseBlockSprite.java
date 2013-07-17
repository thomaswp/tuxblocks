package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.Layer;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.solve.blocks.n.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.BlockGroup;

public class BaseBlockSprite extends BlockSprite {
	
	protected BaseBlock block;
	protected HorizontalBlockGroupSprite modifiers;
	protected GroupLayer groupLayer;
	
	public Layer layerAddable() {
		return groupLayer;
	}

	public BaseBlock block() {
		return block;
	}
	
	public BaseBlockSprite(BaseBlock block) {
		super(block);
		this.block = block;
		layer = generateNinepatch(block.text(), Colors.WHITE);
		layer.setSize(baseSize(), baseSize());
		groupLayer = graphics().createGroupLayer();
		groupLayer.add(layer());
		
		modifiers = new HorizontalBlockGroupSprite(block.modifiers(), this);
		groupLayer.add(modifiers.layer());
		modifiers.layer().setDepth(-1);
	}

	public void addBlockListener(BlockListener listener) {
		modifiers.addBlockListenerListener(listener);
	}
	
	public boolean contains(float gx, float gy) {
		float x = gx - getGlobalTx(groupLayer);
		float y = gy - getGlobalTy(groupLayer);
		return super.contains(x, y) || modifiers.contains(x, y);
	}

	public void clearPreview() {
		groupLayer.setAlpha(1f);
	}
	public void setPreview(boolean preview) {
		groupLayer.setAlpha(preview ? 1f : 0.5f);
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

		void wasGrabbed(ModifierBlockSprite sprite);
		boolean wasReleased(ModifierBlockSprite sprite, float gx, float gy);
		void wasMoved(ModifierBlockSprite sprite, float gx, float gy);
		
	}

	public void addModifier(ModifierBlockSprite sprite) {
		modifiers.addModifier(sprite);
	}
}
