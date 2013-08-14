package tuxkids.tuxblocks.core.layers;

import java.util.HashMap;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Layer.HitTester;
import playn.core.Pointer.Listener;
import playn.core.util.Callback;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.GLStatus;

public class ImageLayerTintable extends LayerWrapper implements ImageLayerLike {
	
	private GroupLayer layer;
	
	private Image baseImage;
	private ImageLayer base;
	private ImageLayer top;
	private int tint;
	
	private HashMap<Integer, Image> tintMap = new HashMap<Integer, Image>();

	private boolean useGL() {
		return GLStatus.enabled();
	}

	@Override
	public float width() {
		return base.width();
	}

	@Override
	public float height() {
		return base.height();
	}

	public Image image() {
		return baseImage;
	}
	
	public int tint() {
		return tint;
	}
	
	public void setImage(Image image) {
		if (baseImage == image) return;
		this.baseImage = image;
		base.setImage(image);
		tintMap.clear();
		tintMap.put(Colors.WHITE, image);
		setTint(tint);
	}

	// to avoid object creation for setting size
	private class SetSizeCallback implements Callback<Image> {
		float width, height;
		@Override
		public void onSuccess(Image result) {
			layer.setScale(width / baseImage.width(), height / baseImage.height());
		}

		@Override
		public void onFailure(Throwable cause) {
			cause.printStackTrace();
		}
	}
	private SetSizeCallback setSizeCallback = new SetSizeCallback();
	@Override
	public void setSize(float width, float height) {
		setSizeCallback.width = width;
		setSizeCallback.height = height;
		baseImage.addCallback(setSizeCallback);
	}

	@Override
	public void setWidth(float width) {
		layer.setScaleX(width / baseImage.width());
	}

	@Override
	public void setHeight(float height) {
		layer.setScaleY(height / baseImage.height());
	}
	
	public ImageLayerTintable() {
		this(null);
	}
	
	public ImageLayerTintable(Image image) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		base = graphics().createImageLayer(image);
		top = graphics().createImageLayer();
		top.setInteractive(false);
		layer.add(base); layer.add(top);
		baseImage = image;
		tintMap.put(Colors.WHITE, baseImage);
		tint = Colors.WHITE;
	}
	
	@Override
	public void setTint(final int color) {
		tint = color;
		if (!useGL()) {
			top.setImage(null);
			
			if (baseImage == null) return;
			baseImage.addCallback(new Callback<Image>() {
				@Override
				public void onSuccess(Image result) {
					base.setImage(getTintedImage(color));
				}
				
				@Override
				public void onFailure(Throwable cause) { }
			});
		} else {
			layer.setTint(color);
		}
	}
	
	@Override
	public void setTint(final int baseColor, final int tintColor, float perc) {
		tint = Colors.blend(baseColor, tintColor, perc);
		if (!useGL()) {
			baseImage.addCallback(new Callback<Image>() {
				@Override
				public void onSuccess(Image result) {
					base.setImage(getTintedImage(baseColor));
					top.setImage(getTintedImage(tintColor));
				}
	
				@Override
				public void onFailure(Throwable cause) { }
			});
			top.setAlpha(1 - perc);
		} else {
			layer.setTint(Colors.blend(baseColor, tintColor, perc));
		}
	}
	
	private Image getTintedImage(Integer color) {
		Image mapped = tintMap.get(color);
		if (mapped == null) {
//			debug("Created: %d", color);
			mapped = CanvasUtils.tintImage(baseImage, color, 1);
			tintMap.put(color, mapped);
		}
		return mapped;
	}

	@Override
	public void addListener(Listener pointerListener) {
		base.addListener(pointerListener);
	}

	@Override
	public void setInteractive(boolean interactive) {
		base.setInteractive(interactive);
	}

	@Override
	public void setHitTester(HitTester tester) {
		base.setHitTester(tester);
	}
}
