package tuxkids.tuxblocks.core.layers;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer.HitTester;

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

	@Override
	public Image image() {
		return layer.image();
	}

	@Override
	public void setHitTester(HitTester tester) {
		layer.setHitTester(tester);
	}
}
