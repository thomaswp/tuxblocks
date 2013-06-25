package tuxkids.tuxblocks.core;

import java.util.HashMap;

import playn.core.Canvas;
import playn.core.Connection;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.Image.BitmapTransformer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Mouse.LayerListener;
import playn.core.Pointer.Listener;
import playn.core.gl.GLShader;
import playn.core.util.Callback;
import pythagoras.f.Point;
import pythagoras.f.Transform;
import tripleplay.particle.GLStatus;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class ImageLayerTintable extends PlayNObject {

	private static final boolean DISABLE_GL = !GLStatus.enabled();
	
	private GroupLayer layer;
	
	private Image baseImage;
	private ImageLayer base;
	private ImageLayer top;
	private int tint;
	
	private HashMap<Integer, Image> tintMap = new HashMap<Integer, Image>();

	private boolean useGL() {
		return !DISABLE_GL && graphics().ctx() != null;
	}
	
	public float width() {
		return base.width();
	}

	public float height() {
		return base.height();
	}
	
	public Layer layer() {
		return layer;
	}

	public Image image() {
		return baseImage;
	}
	
	public float tx() {
		return layer.tx();
	}
	
	public float ty() {
		return layer.ty();
	}
	
	public int tint() {
		return tint;
	}
	
	public float depth() {
		return layer.depth();
	}
	
	public float alpha() {
		return layer.alpha();
	}
	
	public boolean visible() {
		return layer.visible();
	}

	public float originX() {
		return layer.originX();
	}
	
	public float originY() {
		return layer.originY();
	}
	
	public float rotation() {
		return layer.rotation();
	}

	public void setTx(float tx) {
		layer.setTx(tx);
	}
	
	public void setTy(float ty) {
		layer.setTy(ty);
	}
	
	public void setTranslation(float x, float y) {
		layer.setTranslation(x, y);
	}
	
	public void setOrigin(float x, float y) {
		layer.setOrigin(x, y);
	}
	
	public void setImage(Image image) {
		if (baseImage == image) return;
		this.baseImage = image;
		base.setImage(image);
		tintMap.clear();
		tintMap.put(Colors.WHITE, image);
		setTint(tint);
	}

	public void setScale(float scale) {
		setScale(scale, scale);
	}
	
	public void setScale(float scaleX, float scaleY) {
		layer.setScale(scaleX, scaleY);
	}

	public void setDepth(int depth) {
		layer.setDepth(depth);
	}

	public void setVisible(boolean visible) {
		layer.setVisible(visible);
	}

	public void setAlpha(float alpha) {
		layer.setAlpha(alpha);
	}
	
	public void setRotation(float angle) {
		layer.setRotation(angle);
	}
	
	public ImageLayerTintable() {
		this(null);
	}
	
	public ImageLayerTintable(Image image) {
		layer = graphics().createGroupLayer();
		base = graphics().createImageLayer(image);
		top = graphics().createImageLayer();
		top.setInteractive(false);
		layer.add(base); layer.add(top);
		baseImage = image;
		tintMap.put(Colors.WHITE, baseImage);
		tint = Colors.WHITE;
	}
	
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
	
	public void destroy() {
		layer.destroy();
	}

	public void addListener(Listener pointerListener) {
		base.addListener(pointerListener);
	}
}
