package tuxkids.tuxblocks.core.layers;

import playn.core.Image;
import playn.core.Layer;
import playn.core.Pointer.Listener;

public interface ImageLayerLike extends LayerLike {	
	public void setSize(float width, float height);

	public interface Factory {
		public ImageLayerLike create(Image image);
	}
}
