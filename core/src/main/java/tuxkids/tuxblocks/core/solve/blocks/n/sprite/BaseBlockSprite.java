package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.Mouse.ButtonEvent;
import playn.core.Mouse.MotionEvent;
import playn.core.Mouse.WheelEvent;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import tripleplay.util.Colors;

public abstract class BaseBlockSprite extends BlockSprite {
	
	protected HorizontalModifierGroup modifiers;
	protected GroupLayer groupLayer;
	
	public Layer layerAddable() {
		return groupLayer;
	}
	
	@Override
	public Layer layer() {
		return groupLayer;
	}
	
	@Override
	public float x() {
		return layer.tx();
	}
	
	@Override
	public float y() {
		return layer.ty();
	}
	
	public BaseBlockSprite(String text) {
		layer = generateNinepatch(text, Colors.WHITE);
		layer.setSize(baseSize(), baseSize());
		layer.setInteractive(true);
		groupLayer = graphics().createGroupLayer();
		groupLayer.add(layer.layerAddable());
		
		modifiers = new HorizontalModifierGroup(this);
		groupLayer.add(modifiers.layer());
		modifiers.layer().setDepth(-Float.MAX_VALUE);
	}

	@Override
	public void addBlockListener(BlockListener listener) {
		super.addBlockListener(listener);
		modifiers.addBlockListenerListener(listener);
	}
	
	@Override
	protected float defaultWidth() {
		return baseSize();
	}
	
	@Override
	protected float defaultHeight() {
		return baseSize();
	}
	
	@Override
	protected boolean canRelease(boolean openSpace) {
		return true;
	}
	
	@Override
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
		ModifierGroup newMods = modifiers.updateParentModifiers();
		if (newMods != modifiers) {
			if (newMods != null) {
				groupLayer.add(newMods.layer());
				newMods.layer().setDepth(modifiers.layer().depth());
				modifiers.layer().destroy();
				modifiers = (HorizontalModifierGroup)newMods;
			}
		}
	}

	@Override
	public void paint(Clock clock) {
		interpolateDefaultRect(clock);
		modifiers.updateParentRect(x(), y(), defaultWidth(), defaultHeight());
		modifiers.paint(clock);
	}
	


	public void snapChildren() {
		modifiers.updateParentRect(this);
		modifiers.snapChildren();
	}
	
	public String hierarchy() {
		return toString() + "\n" + modifiers.hierarchy(1);
	}
	
	public interface BlockListener {

		void wasGrabbed(BlockSprite sprite, Event event);
		void wasReleased(Event event);
		void wasMoved(Event event);
		
	}

	public void addModifier(ModifierBlockSprite sprite) {
		addModifier(sprite, true);
	}
	
	public void addModifier(ModifierBlockSprite sprite, boolean snap) {
		modifiers.addModifier(sprite, snap);
	}

	public boolean canAccept(BlockSprite sprite) {
		if (sprite instanceof ModifierBlockSprite) {
			return true;
		} else if (sprite instanceof NumberBlockSprite) {
			return modifiers.canAddExpression((NumberBlockSprite) sprite);
		}
		return false;
	}

	public void addBlock(BlockSprite sprite, boolean snap) {
		if (sprite instanceof ModifierBlockSprite) {
			addModifier((ModifierBlockSprite) sprite, snap);
		} else if (sprite instanceof NumberBlockSprite) {
			modifiers.addExpression((NumberBlockSprite) sprite, snap);
		}
	}
	
	public boolean equals(Object o) {
		return this == o;
	}

	public float totalWidth() {
		return modifiers.totalWidth();
	}

	public float offsetX() {
		return modifiers.offsetX();
	}
}
