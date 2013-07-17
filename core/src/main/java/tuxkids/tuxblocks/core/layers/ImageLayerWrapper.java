package tuxkids.tuxblocks.core.layers;

import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Pointer.Listener;
import tripleplay.util.Colors;

public class ImageLayerWrapper implements ImageLayerLike {

	private ImageLayer layer;
	
	public ImageLayerWrapper(ImageLayer layer) {
		this.layer = layer;
	}
	
	@Override
	public Layer layerAddable() {
		return layer;
	}

	@Override
	public void setTranslation(float x, float y) {
		layer.setTranslation(x, y);
	}

	@Override
	public void setSize(float width, float height) {
		layer.setSize(width, height);
	}

	@Override
	public void setVisible(boolean visible) {
		layer.setVisible(visible);
	}

	@Override
	public void setTint(int tint) {
		layer.setTint(tint);
	}

	@Override
	public void setTint(int baseColor, int tintColor, float perc) {
		setTint(Colors.blend(baseColor, tintColor, perc));
	}

	@Override
	public void addListener(Listener pointerListener) {
		layer.addListener(pointerListener);
	}

	@Override
	public float tx() {
		return layer.tx();
	}

	@Override
	public float ty() {
		return layer.ty();
	}

}
