package tuxkids.tuxblocks.core.layers;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.Layer;
import playn.core.Pointer.Listener;

public interface LayerLike {
	public Layer layerAddable();
	public GroupLayer parent();
	public float tx();
	public float ty();
	public float depth();
	public float alpha();
	public boolean destroyed();
	
	public void setTranslation(float x, float y);
	public void setTx(float ty);
	public void setTy(float tx);
	public void setVisible(boolean visible);
	public void setTint(int tint);
	public void setTint(int baseColor, int tintColor, float perc);
	public void setInteractive(boolean interactive);
	public void setDepth(float depth);
	public void setOrigin(float x, float y);
	public void setAlpha(float alpha);
	
	public void addListener(Listener pointerListener);
	
	public void addToLayer(GroupLayer layer);
	
}
