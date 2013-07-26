package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Sprite.Action;
import tuxkids.tuxblocks.core.utils.HashCode;

public class BlockHolder extends BaseBlockSprite {

	protected void initSpriteImpl() {
		super.initSpriteImpl();
		clearPreview();
	}
	
	@Override
	public void clearPreview() {
		groupLayer.setAlpha(0.5f);
	}

	@Override
	protected String text() {
		return " ";
	}
	
	@Override
	public int color() {
		return Colors.WHITE;
	}

	@Override
	public boolean canRelease(boolean open) {
		return false;
	}
	
	@Override
	public void addFields(HashCode hashCode) { }
	
	@Override
	protected ImageLayerLike generateNinepatch(String text) {
		return new EmptyBlockLayer(10, 10);
	}
	
	@Override
	public boolean canAccept(BlockSprite sprite) {
		if (sprite instanceof BaseBlockSprite) {
			return true;
		} else if (sprite instanceof HorizontalModifierSprite) {
			return true;
		}
		return false;
	}
	
	@Override
	public void addBlockListener(BlockListener listener) {
		
	}

	@Override
	public BlockSprite inverse() {
		return null;
	}
	
	@Override
	protected Renderer createRendererWith(BaseBlockSprite myCopy, BlockSprite spriteCopy) {
		spriteCopy.performAction(new Action() {
			@Override
			public void run(Sprite sprite) {
				sprite.setPreviewAdd(true);
			}
		});
		if (spriteCopy instanceof HorizontalModifierSprite) {
			return ((HorizontalModifierSprite) spriteCopy).getProxy(false).createRenderer();
		} else if (spriteCopy instanceof BaseBlockSprite) {
			return ((BaseBlockSprite) spriteCopy).createRenderer();
		}
		return null;
	}

	@Override
	protected Sprite copyChild() {
		return new BlockHolder();
	}
}
