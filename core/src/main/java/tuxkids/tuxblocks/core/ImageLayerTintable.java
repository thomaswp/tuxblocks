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
import pythagoras.f.Point;
import pythagoras.f.Transform;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class ImageLayerTintable extends PlayNObject {

	private GroupLayer layer;
	
	private Image baseImage;
	private ImageLayer base;
	private ImageLayer top;
	
	private HashMap<Integer, Image> tintMap = new HashMap<Integer, Image>();

	public float width() {
		return base.width();
	}

	public float height() {
		return base.height();
	}

	public void setTx(float tx) {
		layer.setTx(tx);
	}
	
	public void setTy(float ty) {
		layer.setTy(ty);
	}
	
	public Layer layer() {
		return layer;
	}
	
	public ImageLayerTintable() {
		this(null);
	}
	
	public ImageLayerTintable(Image image) {
		layer = graphics().createGroupLayer();
		base = graphics().createImageLayer(image);
		top = graphics().createImageLayer();
		layer.add(base); layer.add(top);
		baseImage = image;
		tintMap.put(Colors.WHITE, baseImage);
	}
	
	public void setTint(int color) {
		base.setImage(getTintedImage(color));
		top.setImage(null);
	}
	
	public void setTint(int baseColor, int tintColor, float perc) {
		base.setImage(getTintedImage(baseColor));
		top.setImage(getTintedImage(tintColor));
		top.setAlpha(perc);
	}
	
	private Image getTintedImage(Integer color) {
		Image mapped = tintMap.get(color);
		if (mapped == null) {
			mapped = CanvasUtils.tintImage(baseImage, color, 1);
			tintMap.put(color, mapped);
		}
		return mapped;
	}
}
