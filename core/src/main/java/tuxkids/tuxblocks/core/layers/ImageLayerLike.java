package tuxkids.tuxblocks.core.layers;

import playn.core.Image;
import playn.core.Layer;
import playn.core.Pointer.Listener;

public interface ImageLayerLike extends LayerLike {	
	public float width();
	public float height();
	
	public void setSize(float width, float height);
	public void setWidth(float width);
	public void setHeight(float height);

	public interface Factory {
		public ImageLayerLike create(Image image);
	}

}
