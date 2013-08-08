package tuxkids.tuxblocks.core.title;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextLayout;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class SlideLayer extends LayerWrapper {

	protected final GroupLayer layer;
	protected final ImageLayer barLayer, fillLayer, nubLayer;
	protected final ImageLayer[] stopLayers;
	protected final float width, height;
	protected final String[] stops;
	
	protected int stop;
	protected float dragOffsetX;
	protected boolean dragging;
	protected StopChangedListener listener;
	
	public int stop() {
		return stop;
	}
	
	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	public void setStopChangedListener(StopChangedListener stopChangedListener) {
		listener = stopChangedListener;
	}
	
	public void setStop(int stop, boolean force) {
		if (force) {
			this.stop = -1;
			setNubX(getStopX(stop));
		}
		setStop(stop);
	}
	
	public void setStop(int stop) {
		if (this.stop == stop) return;
		this.stop = stop;
		if (listener != null) {
			listener.onStopChanged(stop);
		}
	}
	
	public void centerLayer() {
		setOrigin(width() / 2, height() / 2);
	}
	
	public SlideLayer(float width, float height, int color, String... stops) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		
		this.width = width;
		this.height = height;
		this.stops = stops;
		
		barLayer = graphics().createImageLayer();
		float h = height / 3, rad = h / 5, strokeWidth = h / 10;
		barLayer.setImage(CanvasUtils.createRoundRectCached(
				width, h, rad, Color.argb(0, 0, 0, 0), strokeWidth, Colors.LIGHT_GRAY));
		barLayer.setTy((height - h) / 2);
		barLayer.addListener(new SlideListener());
		layer.add(barLayer);
		
		fillLayer = graphics().createImageLayer();
		fillLayer.setImage(CanvasUtils.createRectCached(1, h - strokeWidth * 2 + 1, color));
		fillLayer.setAlpha(0.5f);
		fillLayer.setTranslation(barLayer.tx() + strokeWidth, barLayer.ty() + strokeWidth);
		fillLayer.setDepth(-1);
		layer.add(fillLayer);
		

		TextFormat format = createFormat(h * 0.7f);
		stopLayers = new ImageLayer[stops.length];
		for (int i = 0; i < stops.length; i++) {
			ImageLayer stopLayer = graphics().createImageLayer();
			stopLayer.setImage(CanvasUtils.createTextCached(stops[i], format, Colors.WHITE));
			stopLayer.setDepth(2);
			stopLayer.setTx(getStopX(i));
			stopLayer.setTy(height / 2);
			centerImageLayer(stopLayer);
			layer.add(stopLayer);
		}
		
		nubLayer = graphics().createImageLayer();
		float w = graphics().width() / 20;
		nubLayer.setImage(CanvasUtils.createRoundRectCached(
				w, height, rad, Colors.blend(color, Colors.BLACK, 0.8f), strokeWidth, Colors.DARK_GRAY));
		nubLayer.setTy(height / 2);
		nubLayer.addListener(new SlideListener());
		nubLayer.setDepth(1);
		centerImageLayer(nubLayer);
		layer.add(nubLayer);
		
		setStop(0);
	}
	
	private float getStopX(int stop) {
		float buf = height * 0.22f;
		return stop * (width - buf * 2) / (stops.length - 1) + buf;
	}
	
	private void setNubX(float x) {
		x = Math.max(0, Math.min(width, x));
		nubLayer.setTx(x);
		fillLayer.setWidth(nubLayer.tx() - fillLayer.tx());
	}
	
	private int getStop(float x) {
		return Math.min(stops.length - 1, Math.max(0, Math.round(x / width * (stops.length - 1))));
	}

	public void paint(Clock clock) {
		if (!dragging) {
			float x = lerpTime(nubLayer.tx(), getStopX(stop), 0.99f, clock.dt(), 1f);
			setNubX(x);
		}
	}
	
	private class SlideListener implements Listener {
		@Override
		public void onPointerStart(Event event) {
			dragOffsetX = event.x();
			if (event.hit() == nubLayer) {
				dragOffsetX -= nubLayer.tx();
			} else {
				dragOffsetX -= event.localX();
			}
			dragging = true;
			onPointerDrag(event);
		}
	
		@Override
		public void onPointerEnd(Event event) {
			float x = event.x() - dragOffsetX;
			setStop(getStop(x));
			dragging = false;
		}
	
		@Override
		public void onPointerDrag(Event event) {
			float x = event.x() - dragOffsetX;
			setNubX(x);
			setStop(getStop(x));
		}
	
		@Override
		public void onPointerCancel(Event event) {
			onPointerEnd(event);
		}
	}

	public interface StopChangedListener {
		void onStopChanged(int stop);
	}
}
