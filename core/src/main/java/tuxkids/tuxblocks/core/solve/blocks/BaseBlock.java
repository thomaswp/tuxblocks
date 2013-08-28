package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;

public abstract class BaseBlock extends Block {
	
	public final static float PREVIEW_ALPHA = 0.5f;
	
	protected HorizontalModifierGroup modifiers;
	protected GroupLayer groupLayer;
	protected boolean canMoveBase;
	
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
	
	public boolean simplified() {
		return !modifiers.isModifiedHorizontally() && 
				!modifiers.isModifiedVertically() &&
				modifiers.children.size() == 0;
	}
	
	@Override
	public int color() {
//		return getColor(300);
		return Colors.WHITE;
//		return Color.rgb(200, 0, 200);
	}

	public float totalWidth() {
		return modifiers.totalWidth();
	}

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
	protected boolean shouldShowReleaseIndicator(boolean openSpace) {
		return canMoveBase;
	}
	
	@Override
	public boolean contains(float gx, float gy) {
		float x = gx - layer().tx();// - getGlobalTx(groupLayer);
		float y = gy - layer().ty();// - getGlobalTy(groupLayer);
		return super.contains(x, y) || modifiers.contains(x, y);
	}

	public void clearPreview() {
		groupLayer.setAlpha(1f);
	}
	public void setPreview(boolean preview) {
		groupLayer.setAlpha(preview ? 1f : PREVIEW_ALPHA);
	}

	
	public void update(int delta, boolean multiExpression, boolean moveBase) {
		this.canMoveBase = moveBase;
		super.update(delta, multiExpression);
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
//		if (blockListener == null && !(this instanceof BlockHolder)) {
//			debug("problem: " + this);
//		}
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
		super.paint(clock);
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

	//The public method is for chaining
	public BaseBlock addModifier(ModifierBlock sprite) {
		addModifier(sprite, false);
		return this;
	}
	
	//While the protected returns the block that was actually added
	protected ModifierBlock addModifier(ModifierBlock sprite, boolean snap) {
		return modifiers.addModifier(sprite, snap);
	}

	public boolean canAccept(Block sprite) {
		if (sprite instanceof ModifierBlock) {
			return true;
		} else if (sprite instanceof NumberBlock) {
			return modifiers.canAddExpression((NumberBlock) sprite);
		}
		return false;
	}
	
	protected ModifierBlock addBlock(Block sprite, boolean snap) {
		if (sprite instanceof ModifierBlock) {
			return addModifier((ModifierBlock) sprite, snap);
		} else if (sprite instanceof NumberBlock) {
			return modifiers.addExpression((NumberBlock) sprite, snap);
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
	
	public BaseBlock plus(int x) {
		return addModifier(new PlusBlock(x));
	}
	
	public BaseBlock minus(int x) {
		return addModifier(new MinusBlock(x));
	}
	
	public BaseBlock add(int x) {
		if (x > 0) {
			return plus(x);
		} else if (x < 0) {
			return minus(-x);
		}
		return this;
	}
	
	public BaseBlock times(int x) {
		if (x == 1) return this;
		return addModifier(new TimesBlock(x));
	}
	
	public BaseBlock over(int x) {
		if (x == 1) return this;
		if (x == -1) return times(-1);
		return addModifier(new OverBlock(x));
	}

	public void simplifyModifiers() {
		modifiers.simplifyModifiers();
	}
	
	@Override
	public void showInverse() {
//		BaseBlockSprite inverse = (BaseBlockSprite) inverse();
//		inverse.modifiers = modifiers;
//		inverse.groupLayer.add(modifiers.layer());
		modifiers.addNegative();
	}
	
	public Renderer createRenderer() {
		return modifiers.createRenderer(new BaseRenderer(text()).setHighlight(previewAdd()));
	}
	
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
	protected void performAction(Action action) {
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
