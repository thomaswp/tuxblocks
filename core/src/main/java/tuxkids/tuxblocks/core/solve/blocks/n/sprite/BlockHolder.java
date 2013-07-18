package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.utils.HashCode;

public class BlockHolder extends BaseBlockSprite {

	public BlockHolder() {
		super("");
	}

	@Override
	protected String text() {
		return "";
	}

	@Override
	public boolean canRelease(boolean open) {
		return false;
	}
	
	@Override
	public void addFields(HashCode hashCode) { }
	
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
}
