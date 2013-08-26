package tuxkids.tuxblocks.core.layers;

import playn.core.Image;
import playn.core.Layer;
import playn.core.Layer.HitTester;
import pythagoras.f.Point;

public interface ImageLayerLike extends LayerLike {	
	public float width();
	public float height();
	public Image image();
	
	public void setSize(float width, float height);
	public void setWidth(float width);
	public void setHeight(float height);
	public void setHitTester(HitTester hitTester);

	public interface Factory {
		public ImageLayerLike create(Image image);
	}



}
