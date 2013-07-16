package tuxkids.tuxblocks.core.layers;

import playn.core.Image;
import playn.core.Layer;
import playn.core.Pointer.Listener;

public interface ImageLayerLike {
	public Layer layerAddable();
	public void setTranslation(float x, float y);
	public void setSize(float width, float height);
	public void setVisible(boolean visible);
	public void setTint(int tint);
	public void setTint(int baseColor, int tintColor, float perc);
	public void addListener(Listener pointerListener);
	
	public interface Factory {
		public ImageLayerLike create(Image image);
	}
}
