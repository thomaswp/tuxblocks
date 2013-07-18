package tuxkids.tuxblocks.core.layers;

import playn.core.Image;
import playn.core.Layer;
import playn.core.Pointer.Listener;

public interface LayerLike {
	public Layer layerAddable();
	public float tx();
	public float ty();
	
	public void setTranslation(float x, float y);
	public void setVisible(boolean visible);
	public void setTint(int tint);
	public void setTint(int baseColor, int tintColor, float perc);
	public void setInteractive(boolean interactive);
	
	public void addListener(Listener pointerListener);
	
	
}
