package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.ExpressionRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;

/**
 * Abstract class representing a square block that serves
 * as the base of an expression. Base blocks have a chain
 * of {@link ModifierGroup}s representing the other terms
 * in the expression.
 */
public abstract class BaseBlock extends Block {
	
	public final static float PREVIEW_ALPHA = 0.5f;
	
	// the head of the modifier group chain
	protected HorizontalModifierGroup modifiers;
	// base blocks serve both as a block (with a ImageLayer like any other)
	// and a group of blocks, with a GroupLayer
	protected GroupLayer groupLayer;
	// parameter supplied by the block controller, indicating if there
	// is an available space to move this block (thus allowing it to be moved)
	protected boolean canMoveBase;
	
	@Override
	public Layer layer() {
		return groupLayer;
	}
	
	// The x and y of a base block is its top-left-hand corner
	// The bounds of it and all its modifiers may extend above/left
	// of this point, but because a BaseBlock is a Block, is represent
	// its position this way
	
	@Override
	public float x() {
		return layer.tx();
	}
	
	@Override
	public float y() {
		return layer.ty();
	}
	
	/**
	 * Returns true if this BaseBlock is not modified
	 * horizontally or vertically.
	 */
	public boolean isUnmodified() {
		return !modifiers.isModifiedHorizontally() && 
				!modifiers.isModifiedVertically() &&
				modifiers.children.size() == 0;
	}
	
	@Override
	public int color() {
		return Colors.WHITE;
	}

	/**
	 * Returns the total width of this BaseBlock
	 * and all of its modifiers.
	 */
	public float totalWidth() {
		return modifiers.totalWidth();
	}

	/**
	 * Returns the how far this block's modifiers extend
	 * to the left of its x coordinate. In other words, the 
	 * left bound of this block group is <code>x() - offsetX()</code>
	 */
	public float offsetX() {
		return modifiers.offsetX();
	}
	
	public BaseBlock() {
		modifiers = new HorizontalModifierGroup();
	}
	
	@Override
	protected void initSpriteImpl() {
		super.initSpriteImpl();
		
		layer = generateImage(text());
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
	public void destroy() {
		super.destroy();
		modifiers.destroy();
	}
	
	@Override
	public void addBlockListener(BlockListener listener) {
		super.addBlockListener(listener);
		modifiers.addBlockListener(listener);
	}
	

	/** Returns the index of the given block in this expression */
	public ExpressionBlockIndex indexOf(Block block) {
		if (block == this) {
			return ExpressionBlockIndex.makeExpressionBlockIndex(0, 0);
		} else {
			ExpressionBlockIndex index = modifiers.indexOf(block); 
			if (index != null) index = index.oneDeeper();
			return index;
		}
	}
	
	/** Returns the Block with the given index in this expression */
	public Block getBlockAtIndex(ExpressionBlockIndex index) {
		if (index == null) return null;
		if (index.depth == 0 && index.index == 0) {
			return this;
		} else if (index.depth > 0) {
			return modifiers.getBlockAtIndex(index.oneShallower());
		}
		return null;
	}
	
	/** Creates and returns a list of all Blocks (including this) in this expression */
	public List<Block> getAllBlocks() {
		final ArrayList<Block> blocks = new ArrayList<Block>();
		performAction(new Action() {
			@Override
			public void run(Sprite sprite) {
				if (sprite instanceof Block) {
					blocks.add((Block) sprite);
				}
			}
		});
		return blocks;
	}
	
	/** Creates and returns a list of all Sprites (including this) in this expression */
	public List<Sprite> getAllSprites() {
		final ArrayList<Sprite> blocks = new ArrayList<Sprite>();
		performAction(new Action() {
			@Override
			public void run(Sprite sprite) {
				blocks.add(sprite);
			}
		});
		return blocks;
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
	protected boolean canRelease(boolean multiExpression) {
		return true;
	}
	
	@Override
	protected boolean shouldShowReleaseIndicator(boolean openSpace) {
		return canMoveBase;
	}
	
	@Override
	public boolean contains(float gx, float gy) {
		// adjust to local coordinates
		float x = gx - layer().tx();
		float y = gy - layer().ty();
		// return true if this block or its modified contains the point
		return super.contains(x, y) || modifiers.contains(x, y);
	}

	/**
	 * Indicates to this block that a block is no longer
	 * being dragged and therefore it does not need to indicate
	 * whether or not it can be dropped onto this block or not.
	 */
	public void clearPreview() {
		groupLayer.setAlpha(1f);
	}
	
	/**
	 * Tells this block to change its appearance to
	 * indicate whether or not a hovering block can be dropped
	 * onto it. If set to true, this block will be opaque, and
	 * if set to false it will be partially transparent. 
	 */
	public void setPreview(boolean preview) {
		groupLayer.setAlpha(preview ? 1f : PREVIEW_ALPHA);
	}

	/**
	 * Updates this BaseBlock, also indicating whether or not there
	 * are multiple blocks on this block's side of the equation (multiExpression), as well
	 * as if there are any open {@link BlockHolder}s for this block to be dragged
	 * to (canMoveBase).
	 */
	public void update(int delta, boolean multiExpression, boolean canMoveBase) {
		this.canMoveBase = canMoveBase;
		super.update(delta, multiExpression);
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		modifiers.update(delta, multiExpression);
		
		// if our modifiers have deleted themselves, set a new head
		ModifierGroup newMods = modifiers.updateParentModifiers();
		if (newMods != modifiers) {
			if (newMods != null) {
				// if we changed to a new head, add and remove the necessary layers 
				groupLayer.add(newMods.layer());
				newMods.layer().setDepth(ModifierGroup.MODIFIERS_DEPTH);
				modifiers.layer().destroy();
				modifiers = (HorizontalModifierGroup)newMods;
			}
		}
	}

	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		interpolateDefaultRect(clock);
		modifiers.updateParentRect(x(), y(), defaultWidth(), defaultHeight());
		modifiers.paint(clock);
	}

	/**
	 * Snaps all modifiers of this Block into their default positions
	 * (instead of interpolating).
	 */
	public void snapChildren() {
		modifiers.updateParentRect(this);
		modifiers.snapChildren();
	}
	
	/**
	 * Outputs a debug String, indicating the stucture of
	 * this block and its children
	 */
	public String hierarchy() {
		return toString() + "\n" + modifiers.hierarchy(1);
	}

	/**
	 * Adds the given modifier to this block at the outermost
	 * possible position. Returns this block for chaining.
	 * This method should be used for constructing new Blocks,
	 * but not for manipulating them in an equation.
	 */
	public BaseBlock addModifier(ModifierBlock sprite) {
		addModifier(sprite, false);
		return this;
	}
	
	/** 
	 * Adds the given modifier to this block in the outermost
	 * possible position. Optionally snaps the block into place.
	 * This method returns the Block that was added.
	 */
	protected ModifierBlock addModifier(ModifierBlock sprite, boolean snap) {
		return modifiers.addModifier(sprite, snap);
	}

	/**
	 * Returns true if the given block can be successfully
	 * added to this Block.
	 */
	public boolean canAccept(Block sprite) {
		if (sprite instanceof ModifierBlock) {
			return true;
		} else if (sprite instanceof NumberBlock) {
			return modifiers.canAddExpression((NumberBlock) sprite);
		}
		return false;
	}
	
	/**
	 * Attempts to add the given Block to this Block. Before calling this,
	 * you should have confirmed this capability with {@link BaseBlock#canAccept(Block)}.
	 * Returns the {@link ModifierBlock} that was actually added. This may be different
	 * from the supplied argument, for instance in the case of adding a 
	 * {@link NumberBlock}, a {@link ModifierBlock} equivalent will be returned.
	 */
	protected ModifierBlock addBlock(Block sprite, boolean snap) {
		if (sprite instanceof ModifierBlock) {
			return addModifier((ModifierBlock) sprite, snap);
		} else if (sprite instanceof NumberBlock) {
			return modifiers.addExpression((NumberBlock) sprite, snap);
		}
		return null;
	}
	
	/**
	 * While most Blocks will return a field-based equality
	 * (equal fields mean equal objects), {@link BaseBlock}s
	 * are considered equal only if the Objects are in fact the
	 * same. This is to help differentiate them within Lists.
	 */
	@Override
	public boolean equals(Object o) {
		return this == o; //For the sake of lists of these, it's important to use object equality
	}
	
	/**
	 * While most Blocks will return a field-based hashcode
	 * (equal fields mean equal hashcodes), {@link BaseBlock}s'
	 * hashcodes use the default implementation. 
	 * This is to help differentiate them within Lists.
	 */
	@Override
	public int hashCode() {
		return nativeHashCode(); //For the sake of lists of these, it's important to use object equality
	}
	
	/** 
	 * Adds a {@link PlusBlock} with the given value to this Block, 
	 * returns this Block for chaining. 
	 */
	public BaseBlock plus(int x) {
		return addModifier(new PlusBlock(x));
	}
	
	/** 
	 * Adds a {@link MinusBlock} with the given value to this Block, 
	 * returns this Block for chaining. 
	 */
	public BaseBlock minus(int x) {
		return addModifier(new MinusBlock(x));
	}
	
	/** 
	 * Adds a {@link PlusBlock} or {@link MinusBlock} to this Block 
	 * as appropriate for the given value. Numbers greater than 0
	 * will add a PlusBlock and number less than 0 will create a MinusBlock.
	 * Supplying 0 will do nothing. Returns this Block for chaining. 
	 */
	public BaseBlock add(int x) {
		if (x > 0) {
			return plus(x);
		} else if (x < 0) {
			return minus(-x);
		}
		return this;
	}
	
	/** 
	 * Adds a {@link TimesBlock} with the given value to this Block, 
	 * returns this Block for chaining. 
	 */
	public BaseBlock times(int x) {
		if (x == 1) return this;
		return addModifier(new TimesBlock(x));
	}
	
	/** 
	 * Adds a {@link OverBlock} with the given value to this Block, 
	 * returns this Block for chaining. 
	 */
	public BaseBlock over(int x) {
		if (x == 1) return this;
		if (x == -1) return times(-1);
		return addModifier(new OverBlock(x));
	}

	/** Simplifies this Block's modifiers where possible. */
	public void simplifyModifiers() {
		modifiers.simplifyModifiers();
	}
	
	@Override
	public void showInverse() {
		modifiers.addNegative();
	}
	
	/**
	 * Creates a {@link Renderer} to display this expression.
	 */
	public Renderer createRenderer() {
		return new ExpressionRenderer(modifiers.createRenderer(
				new BaseRenderer(text()).setHighlight(previewAdd())));
	}
	
	/**
	 * Returns a {@link Renderer} for the {@link BaseBlock} that would result
	 * from adding the given Block to the given BaseBlock. Note that both
	 * of the supplied values will be modified so they <i>must</i> be copies of
	 * the actual Blocks.
	 */
	protected Renderer createRendererWith(BaseBlock myCopy, Block spriteCopy) {
		myCopy.performAction(new Action() {
			@Override
			public void run(Sprite sprite) {
				sprite.setPreviewAdd(true);
			}
		});
		myCopy.addBlock(spriteCopy, false);
		myCopy.performAction(new Action() {
			@Override
			public void run(Sprite sprite) {
				sprite.setPreviewAdd(!sprite.previewAdd());
			}
		});
		return myCopy.createRenderer();
	}
	
	/**
	 * Returns a {@link Renderer} for the {@link BaseBlock} that would
	 * result from adding the given Block to this BaseBlock. Optionally
	 * temporarily inverts the supplied block before adding. Note that neither
	 * of these blocks are actually modified in this process. 
	 */
	public Renderer createRendererWith(Block sprite, boolean invertFirst) {		
		BaseBlock copy = (BaseBlock) copy();
		sprite = (Block) sprite.copy();
		if (invertFirst) {
			sprite.showInverse();
			sprite = sprite.inverse();
		}
		return createRendererWith(copy, sprite);
	}
	
	@Override
	public void performAction(Action action) {
		super.performAction(action);
		modifiers.performAction(action);
	}
	
	@Override 
	protected void copyFields(Sprite castMe) {
		BaseBlock copy = (BaseBlock) castMe;
		copy.modifiers = (HorizontalModifierGroup) modifiers.copy();
	}
	
	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		modifiers = data.persist(modifiers);
	}
}
