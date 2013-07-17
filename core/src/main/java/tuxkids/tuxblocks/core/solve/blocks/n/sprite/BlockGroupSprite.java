package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.Rectangle;
import tuxkids.tuxblocks.core.solve.blocks.n.Block;
import tuxkids.tuxblocks.core.solve.blocks.n.BlockGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.HorizontalGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.VerticalGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite.BlockListener;

public abstract class BlockGroupSprite extends Sprite {

	protected abstract void updateChildren(float base, float dt);
	protected abstract void updateRect(float base, float dt);
	
	protected GroupLayer layer;
	protected Rectangle rect = new Rectangle();
	protected Rectangle parentRect = new Rectangle();
	
	protected List<ModifierBlockSprite> children = new ArrayList<ModifierBlockSprite>();
	private List<ModifierBlockSprite> toRemove = new ArrayList<ModifierBlockSprite>();
	protected BlockGroupSprite modifiers;
	protected BlockGroup<?> group;
	
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

	protected void init() { }
	
	public BlockGroupSprite(BlockGroup<?> group, Sprite parent) {
		this.group = group;
		layer = graphics().createGroupLayer();
		init();
		for (ModifierBlock block : group.blocks()) {
			ModifierBlockSprite child = new ModifierBlockSprite(block, this);
			addChild(child);
		}
		updateParentRect(parent);
		updateRect(0, 1);
		updateChildren(0, 1);
		addModifiers();
	}
	
	protected void addModifiers() {
		if (modifiers != null || group.modifiers() == null) return;
		if (group.modifiers() instanceof HorizontalGroup) {
			modifiers = new HorizontalBlockGroupSprite((HorizontalGroup) group.modifiers(), this);
		} else {
			modifiers = new VerticalBlockGroupSprite((VerticalGroup) group.modifiers(), this);
		}
		layer.add(modifiers.layer());
		modifiers.layer().setDepth(-Float.MAX_VALUE);
	}
	
	@Override
	public boolean contains(float x, float y) {
		if (super.contains(x, y)) return true;
		if (modifiers != null) return modifiers.contains(x, y);
		return false;
	}
	
	protected void updateParentRect(Sprite parent) {
		parentRect.setBounds(parent.x(), parent.y(), parent.width(), parent.height());
	}
	
	public void addBlockListenerListener(BlockListener listener) {
		for (ModifierBlockSprite child : children) {
			child.addBlockListener(listener);
		}
		if (modifiers != null) {
			modifiers.addBlockListenerListener(listener);
		}
	}

	protected void removeChild(ModifierBlockSprite sprite) {
//		sprite.layer().destroy();
//		layer.remove(sprite.layer());
		children.remove(sprite);
		group.removeBlock(sprite.block);
	}

	protected void addChild(ModifierBlockSprite child) {
		float depth = 0;
		if (children.size() > 0) depth = children.get(children.size() - 1).layer().depth() - 1;
		children.add(child);
		layer.add(child.layer());
		child.layer().setDepth(depth);
		child.group = this;
		
	}
	
	protected void addChild(ModifierBlockSprite child, boolean addToGroup) {
		addChild(child);
		if (addToGroup) {
			group.addBlock(child.block);
		}
	}
	
	protected BlockGroupSprite updateParentModifiers() {
		if (children.size() == 0) {
			if (modifiers == null) {
				return null;
			} else if (modifiers.children.size() == 0) {
				if (modifiers.modifiers == null) {
					return null;
				}
				releaseLayers();
				modifiers.releaseLayers();
				return modifiers.modifiers;
			}
		}
		return this;
	}
	
	private void updateModifiers() {
		if (modifiers != null && modifiers.children.size() == 0 && modifiers.modifiers != null) {
			layer.remove(modifiers.layer());
			for (ModifierBlockSprite child : modifiers.modifiers.children) {
				toRemove.add(child);
			}
			for (ModifierBlockSprite child : toRemove) {
				modifiers.modifiers.removeChild(child);
				addChild(child, true);
			}
			toRemove.clear();
			modifiers = modifiers.modifiers.modifiers;
		}
	}
	
	protected void releaseLayers() {
		layer.remove(modifiers.layer());
	}
	
	public void addModifier(ModifierBlockSprite sprite) {
		if (modifiers != null) {
			modifiers.addModifier(sprite);
		} else if (group.canAdd(sprite.block)) {
			addChild(sprite, true);
			updateRect(0, 1);
			updateChildren(0, 1);
		} else {
			group.forceCreateModifiers();
			addModifiers();
			modifiers.addModifier(sprite);
		}
	}
	
	@Override
	public void update(int delta) {
		for (ModifierBlockSprite sprite : children) {
			if (sprite.group() != this) {
				toRemove.add(sprite);
			} else {
				sprite.update(delta);
			}
		}
		for (ModifierBlockSprite sprite : toRemove) {
			removeChild(sprite);
		}
		toRemove.clear();
		
		if (modifiers != null) {
			modifiers.update(delta);
			BlockGroupSprite newMods = modifiers.updateParentModifiers();
			if (newMods != modifiers) {
				if (newMods != null) {
					layer.add(newMods.layer());
				}
				modifiers.layer().destroy();
				modifiers = newMods;
			}
		}
		updateModifiers();
	}

	@Override
	public void paint(Clock clock) {
		updateRect(0.995f, clock.dt());
		updateChildren(0.995f, clock.dt());
		for (ModifierBlockSprite sprite : children) {
			sprite.paint(clock);
		}
		if (modifiers != null) {
			modifiers.updateParentRect(this);
			modifiers.paint(clock);
		}
	}
	
	public String hierarchy(int tab) {
		String out = "";
		for (int i = 0; i < tab; i++) out += "  ";
		out += children.toString() + "\n";
		if (modifiers != null) out += modifiers.hierarchy(tab + 1);
		return out;
	}

}
