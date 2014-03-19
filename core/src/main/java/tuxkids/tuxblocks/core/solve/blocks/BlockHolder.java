package tuxkids.tuxblocks.core.solve.blocks;

import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.solve.blocks.layer.BlockLayer;
import tuxkids.tuxblocks.core.solve.blocks.layer.EmptyBlockLayer;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * Represents an empty Block that can be filled by another
 * {@link BaseBlock} by dragging it into this one.
 */
public class BlockHolder extends BaseBlock {

	@Override
	protected void initSpriteImpl() {
		super.initSpriteImpl();
		clearPreview();
	}
	
	@Override
	public void clearPreview() {
		// always remain at 0.5 alpha
		groupLayer.setAlpha(0.5f);
	}

	@Override
	protected String text() {
		// don't use the empty string b/c it has no width
		return " ";
	}
	
	@Override
	public int color() {
		return Colors.WHITE;
	}

	@Override
	public boolean canRelease(boolean multiExpression) {
		return false;
	}
	
	@Override
	protected boolean shouldShowReleaseIndicator(boolean open) {
		return false;
	}
	
	@Override
	public void addFields(HashCode hashCode) { }
	
	@Override
	protected BlockLayer generateImage(String text) {
		return new EmptyBlockLayer(10, 10);
	}
	
	@Override
	public boolean canAccept(Block sprite) {
		// accept only BaseBlocks and blocks that can become BaseBlocks
		if (sprite instanceof BaseBlock) {
			return true;
		} else if (sprite instanceof HorizontalModifierBlock) {
			return true;
		}
		return false;
	}
	
	@Override
	public void addBlockListener(BlockListener listener) {
		// can't be dragged, so do nothing here
	}

	@Override
	public Block inverse() {
		return null;
	}
	
	@Override
	protected Renderer createRendererWith(BaseBlock myCopy, Block spriteCopy) {
		// set the sprite and all it's children to highlight
		spriteCopy.performAction(new Action() {
			@Override
			public void run(Sprite sprite) {
				sprite.setPreviewAdd(true);
			}
		});
		// return the sprite's renderer
		if (spriteCopy instanceof HorizontalModifierBlock) {
			return ((HorizontalModifierBlock) spriteCopy).getProxy(false).createRenderer();
		} else if (spriteCopy instanceof BaseBlock) {
			return ((BaseBlock) spriteCopy).createRenderer();
		} else if (spriteCopy instanceof VerticalModifierBlock) {
			// when removing excess multipliers by dragging them
			// to empty BlockHodlers, just return 0
			return new BaseRenderer("0"); 
		}
		return null;
	}

	@Override
	protected Sprite copyChild() {
		return new BlockHolder();
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new BlockHolder();
			}
		};
	}

	@Override
	protected int getBaseValue(int answer) {
		return 0;
	}
}
