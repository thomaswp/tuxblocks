package tuxkids.tuxblocks.core.solve.blocks.layer;

import tuxkids.tuxblocks.core.layers.ImageLayerLike;

/** 
 * Small extension of {@link ImageLayerLike} that includes the 
 * {@link BlockLayer#setText(String)} method 
 */
public interface BlockLayer extends ImageLayerLike {
	public void setText(String text);
}
