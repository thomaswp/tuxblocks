package tuxkids.tuxblocks.core;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import tripleplay.util.Colors;
import playn.core.Layer;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class MenuSprite extends PlayNObject {
	
	public static float DEFAULT_ALPHA = 0.75f;
	
	private GroupLayer layer;
	private ImageLayer background;
	private float width, height;
	
	
	public Layer layer() {
		return layer;
	}
	
	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	public MenuSprite(float width, float height) {
		this.width = width;
		this.height = height;
		layer = graphics().createGroupLayer();
		background = graphics().createImageLayer(
				CanvasUtils.createRect(width, height, Colors.LIGHT_GRAY, 1, Colors.DARK_GRAY));
		background.setAlpha(DEFAULT_ALPHA);
		background.setDepth(-1);
		layer.add(background);
	}
}
