package tuxkids.tuxblocks.core.widget;

import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.TextFormat;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

/** 
 * A {@link SlideLayer} with discrete stops that the nub
 * will snap to.
 */
public class DiscreteSlideLayer extends SlideLayer {

	protected final ImageLayer[] stopLayers;
	protected final String[] stops;
	
	protected int stop;
	protected StopChangedListener listener;
	
	public int stop() {
		return stop;
	}
	
	public void setStopChangedListener(StopChangedListener stopChangedListener) {
		listener = stopChangedListener;
	}
	
	/** Sets the current stop. If forced, snap the nub in place. */
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
	
	/**
	 * Creates a {@link DiscreteSlideLayer} with the given properties.
	 * Each stop provided will be even distributed along the bar.
	 */
	public DiscreteSlideLayer(float width, float height, int color,
			String... stops) {
		super(width, height, color);
		this.stops = stops;
		
		float h = height / 3;
		TextFormat format = createNumberFormat(h * 0.7f);
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
		
		setStop(0);
	}

	public void paint(Clock clock) {
		super.paint(clock);
		if (!dragging) {
			float x = lerpTime(nubLayer.tx(), getStopX(stop), 0.99f, clock.dt(), 1f);
			setNubX(x);
		}
	}
	
	private float getStopX(int stop) {
		float buf = height * 0.22f;
		return stop * (width - buf * 2) / (stops.length - 1) + buf;
	}
	
	private int getStop(float x) {
		return Math.min(stops.length - 1, Math.max(0, Math.round(x / width * (stops.length - 1))));
	}

	public interface StopChangedListener {
		void onStopChanged(int stop);
	}
	
	@Override
	public void onPointerEnd(Event event) {
		super.onPointerEnd(event);
		float x = event.x() - dragOffsetX;
		setStop(getStop(x));
	}

	@Override
	public void onPointerDrag(Event event) {
		super.onPointerDrag(event);
		setStop(getStop(nubLayer.tx()));
	}
	
	@Override
	public void onPointerCancel(Event event) {
		super.onPointerCancel(event);
		setStop(getStop(nubLayer.tx()));
	}
}
