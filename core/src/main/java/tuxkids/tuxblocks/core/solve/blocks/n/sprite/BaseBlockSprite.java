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
import tuxkids.tuxblocks.core.solve.blocks.n.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;

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
	
	public BaseBlockSprite() {
		modifiers = new HorizontalModifierGroup();
	}
	
	@Override
	protected void initSpriteImpl() {
		super.initSpriteImpl();
		
		layer = generateNinepatch(text(), Colors.WHITE);
		layer.setSize(baseSize(), baseSize());
		layer.setInteractive(true);
		groupLayer = graphics().createGroupLayer();
		groupLayer.add(layer.layerAddable());
		layer.layerAddable().setDepth(ModifierGroup.CHILD_START_DEPTH);

		modifiers.updateParentRect(this);
		modifiers.initSprite();
		groupLayer.add(modifiers.layer());
		modifiers.layer().setDepth(ModifierGroup.MODIFIERS_DEPTH);
	}

	@Override
	public void addBlockListener(BlockListener listener) {
		super.addBlockListener(listener);
		modifiers.addBlockListener(listener);
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
	
	public void update(int delta) {
		super.update(delta);
		modifiers.update(delta, multiExpression);
		ModifierGroup newMods = modifiers.updateParentModifiers();
		if (newMods != modifiers) {
			if (newMods != null) {
				groupLayer.add(newMods.layer());
				newMods.layer().setDepth(ModifierGroup.MODIFIERS_DEPTH);
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

	public ModifierBlockSprite addModifier(ModifierBlockSprite sprite) {
		return addModifier(sprite, false, true);
	}
	
	protected ModifierBlockSprite addModifier(ModifierBlockSprite sprite, boolean snap, boolean addSprite) {
		return modifiers.addModifier(sprite, snap, addSprite);
	}

	public boolean canAccept(BlockSprite sprite) {
		if (sprite instanceof ModifierBlockSprite) {
			return true;
		} else if (sprite instanceof NumberBlockSprite) {
			return modifiers.canAddExpression((NumberBlockSprite) sprite);
		}
		return false;
	}

	public ModifierBlockSprite addBlock(BlockSprite sprite, boolean snap) {
		return addBlock(sprite, snap, true);
	}
	
	protected ModifierBlockSprite addBlock(BlockSprite sprite, boolean snap, boolean addSprite) {
		if (sprite instanceof ModifierBlockSprite) {
			return addModifier((ModifierBlockSprite) sprite, snap, addSprite);
		} else if (sprite instanceof NumberBlockSprite) {
			return modifiers.addExpression((NumberBlockSprite) sprite, snap, addSprite);
		}
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		return this == o; //For the sake of lists of these, it's important to use object equality
	}
	
	@Override
	public int hashCode() {
		return nativeHashCode(); //For the sake of lists of these, it's important to use object equality
	}

	public float totalWidth() {
		return modifiers.totalWidth();
	}

	public float offsetX() {
		return modifiers.offsetX();
	}
	
	@Override
	public void showInverse() {
		super.showInverse();
		BaseBlockSprite inverse = (BaseBlockSprite) inverse();
		inverse.modifiers = modifiers;
		inverse.groupLayer.add(modifiers.layer());
	}
	
	public Renderer createRenderer() {
		return modifiers.createRenderer(new BaseRenderer(text()).setHighlight(!hasSprite()));
	}
	
	public Renderer createRendererWith(BlockSprite sprite) {
		debug("Before: " + hierarchy());
		GroupLayer parent = sprite.layer().parent();
		float depth = sprite.layer().depth();
		
		ModifierBlockSprite toRemove = addBlock(sprite, false, false);
		toRemove.setPreviewAdd(true);
		Renderer renderer = modifiers.createRenderer(new BaseRenderer(text()));
		toRemove.setPreviewAdd(false);
		
		toRemove.group.removeChild(toRemove);
		if (toRemove == sprite) {
			sprite.layer().setDepth(depth);
			if (parent != null) parent.add(sprite.layer());
		}
		debug("After: " + hierarchy());
		
		return renderer;
	}
	
	@Override 
	protected void copyFields(Sprite castMe) {
		BaseBlockSprite copy = (BaseBlockSprite) castMe;
		copy.modifiers = (HorizontalModifierGroup) modifiers.copy();
	}
}
