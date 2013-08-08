package tuxkids.tuxblocks.core.title;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class SlideLayer extends LayerWrapper implements Listener {

	protected final GroupLayer layer;
	protected final ImageLayer barLayer;
	protected final ImageLayer nubLayer;
	protected final float width, height;
	protected final int stops;
	
	protected int stop;
	protected float dragOffsetX;
	
	public int stop() {
		return stop;
	}
	
	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	public void setStop(int stop) {
		this.stop = stop;
		nubLayer.setTx(stop * width / (stops - 1));
	}
	
	public void centerLayer() {
		setOrigin(width() / 2, height() / 2);
	}
	
	public SlideLayer(float width, float height, int stops, int color) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		
		this.width = width;
		this.height = height;
		this.stops = stops;
		
		barLayer = graphics().createImageLayer();
		float h = height / 3, rad = h / 4, strokeWidth = h / 10;
		barLayer.setImage(CanvasUtils.createRoundRectCached(
				width, h, rad, Color.argb(0, 0, 0, 0), strokeWidth, Colors.LIGHT_GRAY));
		barLayer.setTy((height - h) / 2);
		layer.add(barLayer);
		
		nubLayer = graphics().createImageLayer();
		float w = width() / 20;
		nubLayer.setImage(CanvasUtils.createRoundRectCached(
				w, height, rad, color, strokeWidth, Colors.DARK_GRAY));
		nubLayer.setTy(height / 2);
		nubLayer.addListener(this);
		centerImageLayer(nubLayer);
		layer.add(nubLayer);
		
		setStop(0);
	}

	@Override
	public void onPointerStart(Event event) {
		dragOffsetX = event.x() - nubLayer.tx();
	}

	@Override
	public void onPointerEnd(Event event) {
		
	}

	@Override
	public void onPointerDrag(Event event) {
		float x = event.x() - dragOffsetX;
		debug(x);
		int stop = Math.round(x / width * (stops - 1));
		stop = Math.min(Math.max(0, stop), stops);
		setStop(stop);
	}

	@Override
	public void onPointerCancel(Event event) {
		// TODO Auto-generated method stub
		
	}

}
