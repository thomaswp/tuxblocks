package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
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
	public boolean canRelease(boolean open) {
		return false;
	}
	
	@Override
	public void addFields(HashCode hashCode) { }
	
	@Override
	protected ImageLayerLike generateNinepatch(String text, int color) {
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
	
	public Renderer createRendererWith(BlockSprite sprite) {
		sprite.setPreviewAdd(true);
		try {
			if (sprite instanceof HorizontalModifierSprite) {
				return ((HorizontalModifierSprite) sprite).getProxy(false).createRenderer();
			} else if (sprite instanceof BaseBlockSprite) {
				return ((BaseBlockSprite) sprite).createRenderer();
			}
		} finally {
			sprite.setPreviewAdd(false);
		}
		return null;
	}

	@Override
	protected Sprite copyChild() {
		return new BlockHolder();
	}
}
