package tuxkids.tuxblocks.core.layers;

import playn.core.ImageLayer;

public class ImageLayerWrapper extends LayerWrapper implements ImageLayerLike {

	private ImageLayer layer;
	
	public ImageLayerWrapper(ImageLayer layer) {
		super(layer);
		this.layer = layer;
	}

	@Override
	public void setSize(float width, float height) {
		layer.setSize(width, height);
	}

	@Override
	public void setWidth(float width) {
		layer.setWidth(width);
	}

	@Override
	public void setHeight(float height) {
		layer.setHeight(height);
	}

	@Override
	public float width() {
		return layer.width();
	}

	@Override
	public float height() {
		return layer.height();
	}

}
