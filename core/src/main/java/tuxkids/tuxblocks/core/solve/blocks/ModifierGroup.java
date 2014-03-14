package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.util.Clock;
import pythagoras.f.Rectangle;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.Simplifiable;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

/**
 * Represents a group of modifiers to a base block (or other
 * modifiers). These can be horizontal modifiers, such as addition
 * and subtraction, or vertical modifiers such as multiplication and
 * division. ModifierGroups are organized as a linked list, with the head
 * owned by a BaseBlock. Each ModifierGroup holds a reference to another group,
 * which modifies it, or null if none does.
 */
public abstract class ModifierGroup extends Sprite implements Hashable, Simplifiable {

	/** Z-depth of this group's modifiers */
	protected final static int MODIFIERS_DEPTH = 0;
	/** Z-depth of this group's children */
	protected static final int CHILD_START_DEPTH = -1;
	
	/** 
	 * Called when this group should update the position of its children,
	 *  interpolating with the given base and dt.
	 */
	protected abstract void updateChildren(float base, float dt);
	/**
	 * Called when this group should updates the bounds of its
	 * own rect.
	 */
	protected abstract void updateRect();
	/**
	 * Should return a new ModifierGroup appropriate for this
	 * group's modifiers. For instance, a {@link HorizontalModifierGroup}
	 * will return a {@link VerticalModifierGroup} and vice versa.
	 */
	protected abstract ModifierGroup createModifiers();
	/**
	 * Should return true if the given Block can be added
	 * to this group's children.
	 */
	protected abstract boolean canAdd(ModifierBlock sprite);
	/**
	 * Should create a Renderer representing this group and its
	 * children, given the base renderer of the group or block
	 * this group is modifying. For instance, for the expression
	 * 3x, the modifier group [*3] will be passed x as a base.
	 */
	protected abstract Renderer createRenderer(Renderer base);
	
	/**
	 * Adds a *-1 to this group (or its modifiers), or
	 * removes one as appropriate.
	 */
	public abstract void addNegative();
	
	protected GroupLayer layer;
	protected Rectangle rect = new Rectangle();
	// rect of this group's parent
	protected Rectangle parentRect = new Rectangle();
	// passed from the update method, indicates if there are 
	// multiple BaseBlocks on this side of the equation
	protected boolean multiExpression;
	protected SimplifyLayer simplifyLayer;

	private List<ModifierBlock> toRemove = new ArrayList<ModifierBlock>();
	
	protected List<ModifierBlock> children = new ArrayList<ModifierBlock>(),
			destroying = new ArrayList<ModifierBlock>();
	protected ModifierGroup modifiers;
	
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
	
	public float totalWidth() {
		if (modifiers == null) {
			return width();
		}
		return modifiers.totalWidth();
	}

	public float offsetX() {
		if (modifiers == null) {
			return rect.x;
		}
		return modifiers.offsetX();
	}
	
	@Override
	public boolean showSimplify() {
		return blockListener != null && !blockListener.inBuildMode();
	}
	
	/** Returns the index of the given block in this expression */
	public ExpressionBlockIndex indexOf(Block block) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i) == block) {
				return new ExpressionBlockIndex(0, i);
			}
		}
		if (modifiers != null) {
			ExpressionBlockIndex parentIndex = modifiers.indexOf(block);
			if (parentIndex != null) parentIndex = parentIndex.oneDeeper();
			return parentIndex;
		}
		return null;
	}
	
	/** Returns the Block with the given index in this expression */
	public Block getBlockAtIndex(ExpressionBlockIndex index) {
		if (index.depth == 0) {
			if (index.index < children.size()) return children.get(index.index);
		} else if (modifiers != null) {
			return modifiers.getBlockAtIndex(index.oneShallower());
		}
		return null;
	}
	
	@Override
	public void initSpriteImpl() {
		super.initSpriteImpl();

		layer = graphics().createGroupLayer();
		int index = 0;
		for (ModifierBlock child : children) {
			child.initSprite();
			addChildSprite(child, index++);
		}
		updateRect();
		updateChildren(0, 1);
		if (modifiers != null) {
			modifiers.updateParentRect(this);
			modifiers.initSprite();
			addModifiersSprite();
		}
		
		simplifyLayer = new SimplifyLayer(this);
		layer.add(simplifyLayer.layerAddable());
		simplifyLayer.setDepth(SIMPLIFY_DEPTH);
	}
	
	@Override
	public void destroy() {
		if (hasSprite()) layer().destroy();
		for (ModifierBlock child : children) {
			child.destroy();
		}
		if (modifiers != null) modifiers.destroy();
	}
	
	/** Creates and adds a new ModifierGroup to this group */
	protected void addNewModifiers() {
		if (modifiers != null) return;
		setModifiers(createModifiers());
	}
	
	/** Removes and returns this group's ModifierGroup */
	protected ModifierGroup removeModifiers() {
		ModifierGroup mods = modifiers;
		if (modifiers != null && hasSprite()) layer.remove(modifiers.layer());
		modifiers = null;
		return mods;
	}
	
	/** Sets this group's ModifierGroup to the given mods */
	protected void setModifiers(ModifierGroup mods) {
		removeModifiers();
		modifiers = mods;
		if (mods != null && hasSprite()) {
			mods.initSprite();
			addModifiersSprite();
		}
	}
	
	// initializes the mods' sprite
	protected void addModifiersSprite() {
		if (modifiers != null) {
			layer.add(modifiers.layer());
			modifiers.layer().setDepth(MODIFIERS_DEPTH);
			modifiers.addBlockListener(blockListener);
		}
		
	}

	/** Removes and returns the given sprite (by reference, not equality) */
	protected ModifierBlock removeChild(ModifierBlock sprite) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i) == sprite) {
				ModifierBlock child = children.remove(i);
				child.group = null;
				return child; // make sure it's the exact sprite and not just .equal()
			}
		}
		return null;
	}
	
	/** Removes and returns the given sprite (by reference, not equality) and destroys it */
	protected ModifierBlock removeChild(ModifierBlock sprite, boolean destroy) {
		ModifierBlock child = removeChild(sprite);
		if (hasSprite() && destroy) {
			destroying.add(child);
		}
		return child;
	}

	/** Adds the given modifier to this group */
	protected void addChild(ModifierBlock child) {
		children.add(child);
		child.group = this;	
		if (hasSprite()) addChildSprite(child, children.size() - 1);
		child.addBlockListener(blockListener);
	}
	
	/** Adds the given modifier to this group at the given index */
	protected void addChildSprite(ModifierBlock child, int index) {
		float depth = CHILD_START_DEPTH - index;
		child.initSprite();
		layer.add(child.layer());
		child.layer().setVisible(true);
		child.layer().setDepth(depth);
	}
	
	/** Returns true if this group or any of its modifiers is modifier horizontally */
	public boolean isModifiedHorizontally() {
		if (modifiers == null) return false;
		if (modifiers.children.size() > 0 && modifiers instanceof HorizontalModifierGroup) return true;
		return modifiers.isModifiedHorizontally();
	}
	
	/** Returns true if this group or any of its modifiers is modifier vertically */
	public boolean isModifiedVertically() {
		if (modifiers == null) return false;
		if (modifiers.children.size() > 0 && modifiers instanceof VerticalModifierGroup) return true;
		return modifiers.isModifiedVertically();
	}
	
	/** Recursively adds any of this group's {@link VerticalModifierBlock}s to the given list */
	protected void addVerticalModifiersTo(List<VerticalModifierBlock> mods) {
		if (modifiers == null) return;
		for (ModifierBlock mod : modifiers.children) {
			if (mod instanceof VerticalModifierBlock) {
				mods.add((VerticalModifierBlock) mod);
			} else {
				break;
			}
		}
		modifiers.addVerticalModifiersTo(mods);
		
	}
	
	@Override
	public boolean contains(float x, float y) {
		if (super.contains(x, y)) return true;
		if (modifiers != null) return modifiers.contains(x, y);
		return false;
	}
	
	/** 
	 * Uses the given Sprite's position to call 
	 * {@link ModifierGroup#updateParentRect(float, float, float, float)} 
	 */
	protected void updateParentRect(Sprite parent) {
		updateParentRect(parent.x(), parent.y(), parent.width(), parent.height());
	}
	
	/** 
	 * Updates this group's representation of it's parent's position.
	 * This method allows the group to have no reference to the parent
	 * itself, allowing for a true tree structure. 
	 */
	protected void updateParentRect(float x, float y, float width, float height) {
		parentRect.setBounds(x, y, width, height);
	}

	/**
	 * Snaps this rect and any of its children directly into
	 * place, as opposed to interpolating into position over time.
	 */
	public void snapChildren() {
		updateRect();
		updateChildren(0, 1);
		if (modifiers != null) {
			modifiers.updateParentRect(this);
			modifiers.snapChildren();
		}
	}
	
	@Override
	public void addBlockListener(BlockListener listener) {
		this.blockListener = listener;
		for (ModifierBlock child : children) {
			child.addBlockListener(listener);
		}
		if (modifiers != null) {
			modifiers.addBlockListener(listener);
		}
	}
	
	/** 
	 * Checks to see if this group and it's modifiers have no children.
	 * If so, it removes both links from the chain of modifiers and returns
	 * it's modifier's modifiers (or null if there are none). If this group
	 * is still in use, it simply returns itself. This allows a group's
	 * parent to update its reference to its modifiers.   
	 */
	protected ModifierGroup updateParentModifiers() {
		// if we're empty and not fading out
		if (children.size() == 0 && destroying.size() == 0) {
			if (modifiers == null) {
				return null;
			} else if (modifiers.children.size() == 0) {
				if (modifiers.modifiers == null) {
					return null;
				}
				// destroy this group if necessary
				releaseLayers();
				modifiers.releaseLayers();
				return modifiers.modifiers;
			}
		}
		// or just return this
		return this;
	}
	
	// do some modifier cleanup
	private void updateModifiers() {
		// if our modifiers have no children, but they do have modifiers with children
		if (modifiers != null && modifiers.children.size() == 0 && modifiers.modifiers != null) {
			// remove our modifiers and add our modifiers' modifiers' children to our children
			// in otherwords, close the gap in the chain
			if (hasSprite()) layer.remove(modifiers.layer());
			for (ModifierBlock child : modifiers.modifiers.children) {
				toRemove.add(child);
			}
			for (ModifierBlock child : toRemove) {
				modifiers.modifiers.removeChild(child);
				addChild(child);
			}
			toRemove.clear();
			modifiers = modifiers.modifiers.modifiers;
			if (modifiers != null && hasSprite()) {
				layer.add(modifiers.layer());
			}
		}
	}
	
	protected void releaseLayers() {
		if (hasSprite()) layer.remove(modifiers.layer());
	}
	
	/** Adds the given modifier to this group, optionally snapping into place */
	protected ModifierBlock addModifier(ModifierBlock sprite, boolean snap) {
		if (modifiers == null && canAdd(sprite)) {
			// add it here if possible
			addChild(sprite);
			if (snap) {
				updateRect();
				if (hasSprite()) updateChildren(0, 1);
			}
		} else {
			// or propagate upward
			if (modifiers == null) addNewModifiers();
			if (snap) {
				updateRect();
			}
			modifiers.updateParentRect(this);
			modifiers.addModifier(sprite, snap);
		}
		return sprite;
	}

	// the following 2 methods are overridden in child classes
	
	/** Attempts to add the given NumberBlock and children, or propagates upward if it can't. */
	public ModifierBlock addExpression(NumberBlock sprite, boolean snap) {
		if (modifiers != null) {
			return modifiers.addExpression(sprite, snap);
		}
		return null;
	}
	
	/** Returns true if the given expression can be added */
	public boolean canAddExpression(NumberBlock sprite) {
		if (modifiers != null) {
			return modifiers.canAddExpression(sprite);
		}
		return false;
	}
	
	@Override
	public void update(int delta) {
		// remove children that don't belong to this group
		for (int i = 0; i < children.size(); i++) {
			ModifierBlock sprite = children.get(i);
			if (sprite.group() != this) {
				removeChild(sprite); 
				i--;
			} else {
				sprite.update(delta, multiExpression);
			}
		}
		
		
		// remove blocks that have faded out
		for (int i = 0; i < destroying.size(); i++) {
			ModifierBlock sprite = destroying.get(i);
			if (sprite.layer().alpha() == 0) {
				destroying.remove(i--);
			}
		}
		
		// update our modifiers
		if (modifiers != null) {
			modifiers.update(delta, multiExpression);
			ModifierGroup newMods = modifiers.updateParentModifiers();
			if (newMods != modifiers && hasSprite()) {
				// if we have new modifiers, update the sprites accordingly
				if (newMods != null) {
					layer.add(newMods.layer());
				}
				// TODO: check for memory leaks here... I'm not sure why the layer 
				// might have children in the first place.. but it does sometimes
				modifiers.layer.removeAll();
				modifiers.layer().destroy();
				modifiers = newMods;
			}
		}
		updateModifiers();
		if (simplifyLayer != null) {
			simplifyLayer.update();
		}
	}
	
	public void update(int delta, boolean multiExpression) {
		this.multiExpression = multiExpression;
		update(delta);
	}

	@Override
	public void paint(Clock clock) {
		updateRect();
		updateChildren(lerpBase(), clock.dt());
		for (ModifierBlock sprite : children) {
			sprite.paint(clock);
		}
		for (ModifierBlock sprite : destroying) {
			// fade out sprite that are in the process of being destroyed
			sprite.layer().setAlpha(lerpTime(sprite.layer().alpha(), 0, 0.995f, clock.dt(), 0.01f));
		}
		if (modifiers != null) {
			modifiers.updateParentRect(this);
			modifiers.paint(clock);
		}
	}
	
	public void simplifyModifiers() {
		if (modifiers != null) modifiers.simplifyModifiers();
	}
	
	@Override
	public void performAction(Action action) {
		super.performAction(action);
		for (ModifierBlock child : children) child.performAction(action);;
		if (modifiers != null) modifiers.performAction(action);
	}
	
	/** Prints out a hierarchy of this group's modifiers (with the given indentation) */
	public String hierarchy(int tab) {
		String out = "";
		for (int i = 0; i < tab; i++) out += "  ";
		out += children.toString() + "\n";
		if (modifiers != null) out += modifiers.hierarchy(tab + 1);
		return out;
	}

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(children);
		hashCode.addField(modifiers);
	}

	@Override
	protected void copyFields(Sprite castMe) {
		ModifierGroup copy = (ModifierGroup) castMe;
		for (ModifierBlock child : children) {
			copy.addChild((ModifierBlock) child.copy());
		}
		if (modifiers != null) copy.setModifiers((ModifierGroup) modifiers.copy());
	}
	
	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		children = data.persistList(children);
		modifiers = data.persist(modifiers);
	}
}
