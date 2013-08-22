package tuxkids.tuxblocks.core.widget;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

/**
 * A slide-bar widget, allowing players to select a value from 0-1.
 * For a slide-bar with discrete options, see {@link DiscreteSlideLayer}.
 * You must call this {@link SlideLayer#paint(Clock)} for this widget to
 * function correctly. 
 */
public class SlideLayer extends LayerWrapper implements Listener {
	protected final GroupLayer layer;
	protected final ImageLayer barLayer, fillLayer, nubLayer;
	protected final float width, height;
	
	protected float dragOffsetX;
	protected boolean dragging;
	
	protected FillListener fillListener;
	
	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	public void centerLayer() {
		setOrigin(width() / 2, height() / 2);
	}
	
	/** Returns the fill percentage [0-1] of the bar. */
	public float fill() {
		return Math.min(Math.max(0, nubLayer.tx() / width), 1);
	}

	/** Sets the fill percentage [0-1] of the bar. */
	public void setFill(float fill) {
		nubLayer.setTx(width * fill);
	}
	
	public void setFillListener(FillListener fillListener) {
		this.fillListener = fillListener;
	}
	
	public SlideLayer(float width, float height, int color) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		
		this.width = width;
		this.height = height;
		
		barLayer = graphics().createImageLayer();
		float h = height / 3, rad = h / 5, strokeWidth = h / 10;
		barLayer.setImage(CanvasUtils.createRoundRectCached(
				width, h, rad, Color.argb(0, 0, 0, 0), strokeWidth, Colors.LIGHT_GRAY));
		barLayer.setTy((height - h) / 2);
		barLayer.addListener(this);
		layer.add(barLayer);
		
		fillLayer = graphics().createImageLayer();
		fillLayer.setImage(CanvasUtils.createRectCached(1, h - strokeWidth * 2 + 1, color));
		fillLayer.setAlpha(0.5f);
		fillLayer.setTranslation(barLayer.tx() + strokeWidth, barLayer.ty() + strokeWidth);
		fillLayer.setDepth(-1);
		layer.add(fillLayer);
		
		nubLayer = graphics().createImageLayer();
		float w = graphics().width() / 20;
		nubLayer.setImage(CanvasUtils.createRoundRectCached(
				w, height, rad, Colors.blend(color, Colors.BLACK, 0.8f), strokeWidth, Colors.DARK_GRAY));
		nubLayer.setTy(height / 2);
		nubLayer.addListener(this);
		nubLayer.setDepth(1);
		centerImageLayer(nubLayer);
		layer.add(nubLayer);
		
	}
	
	// sets the nub's x value and updates graphics appropriately
	protected void setNubX(float x) {
		x = Math.max(0, Math.min(width, x));
		nubLayer.setTx(x);
		fillLayer.setWidth(Math.max(nubLayer.tx() - fillLayer.tx(), 0.1f));
	}
	

	public void paint(Clock clock) {
		float x = nubLayer.tx();
		if (x < 0) x = 0;
		if (x > width) x = width;
		x = lerpTime(nubLayer.tx(), x, 0.99f, clock.dt(), 1f);
		setNubX(x);
	}
	
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
		Audio.se().play(Constant.SE_TICK);
	}

	@Override
	public void onPointerEnd(Event event) {
		dragging = false;
		Audio.se().play(Constant.SE_DROP);
	}

	@Override
	public void onPointerDrag(Event event) {
		float x = event.x() - dragOffsetX;
		setNubX(x);
		if (fillListener != null) {
			fillListener.onFillChanged(fill());
		}
	}

	@Override
	public void onPointerCancel(Event event) {
		dragging = false;
	}
	
	public interface FillListener {
		void onFillChanged(float fill);
	}
}
