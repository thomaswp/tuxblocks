package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.Rectangle;
import tuxkids.tuxblocks.core.solve.blocks.n.BlockGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.HorizontalGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.n.VerticalGroup;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite.BlockListener;

public abstract class BlockGroupSprite extends Sprite {

	protected abstract void updateChildren(float base, float dt);
	protected abstract void updateRect(float base, float dt);
	protected abstract void parseChildren();
	
	protected GroupLayer layer;
	protected Rectangle rect = new Rectangle();
	protected Rectangle parentRect = new Rectangle();
	
	protected List<ModifierBlockSprite> children = new ArrayList<ModifierBlockSprite>();
	private List<ModifierBlockSprite> toRemove = new ArrayList<ModifierBlockSprite>();
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
		int z = 0;
		for (ModifierBlock block : group.blocks()) {
			ModifierBlockSprite child = new ModifierBlockSprite(block);
			children.add(child);
			layer.add(child.layer());
			child.layer().setDepth(z--);
		}
		parseChildren();
		updateParentRect(parent);
		updateRect(0, 1);
		updateChildren(0, 1);
		if (group.modifiers() != null) {
			if (group.modifiers() instanceof HorizontalGroup) {
				modifiers = new HorizontalBlockGroupSprite((HorizontalGroup) group.modifiers(), this);
			} else {
				modifiers = new VerticalBlockGroupSprite((VerticalGroup) group.modifiers(), this);
			}
			layer.add(modifiers.layer());
			modifiers.layer().setDepth(z--);
		}
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
		sprite.layer().destroy();
		children.remove(sprite);
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
	
	protected void releaseLayers() {
		layer.remove(modifiers.layer());
	}
	
	@Override
	public void update(int delta) {
		for (ModifierBlockSprite sprite : children) {
			if (sprite.removed()) {
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
					newMods.layer().setDepth(modifiers.layer().depth());
				}
				modifiers.layer().destroy();
				modifiers = newMods;
			}
		}
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
