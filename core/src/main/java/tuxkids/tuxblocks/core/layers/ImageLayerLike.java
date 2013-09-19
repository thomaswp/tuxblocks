package tuxkids.tuxblocks.core.layers;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer.HitTester;

/**
 * An {@link LayerLike} that also wraps the functionality
 * of an {@link ImageLayer}.
 */
public interface ImageLayerLike extends LayerLike {	
	public float width();
	public float height();
	public Image image();
	
	public void setSize(float width, float height);
	public void setWidth(float width);
	public void setHeight(float height);
	public void setHitTester(HitTester hitTester);

	/**
	 * A Factory can be passed to methods which require the ability to 
	 * dynamically create new {@link ImageLayerLike}s 
	 */
	public interface Factory {
		public ImageLayerLike create(Image image);
	}



}
