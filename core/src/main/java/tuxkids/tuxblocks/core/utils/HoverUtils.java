package tuxkids.tuxblocks.core.utils;

import java.util.HashMap;

import playn.core.Layer;
import playn.core.Mouse;
import playn.core.Mouse.ButtonEvent;
import playn.core.Mouse.MotionEvent;
import playn.core.Mouse.WheelEvent;
import playn.core.PlayN;
import pythagoras.f.Point;
import tuxkids.tuxblocks.core.layers.LayerLike;

public class HoverUtils extends PlayNObject {
	
	private static Point lastMousePosition = new Point(), testPoint = new Point();
	
	private static HashMap<Object, HoverListener> layerMap = 
			new HashMap<Object, HoverUtils.HoverListener>();
	
	static {
		PlayN.mouse().setListener(new Listener());
	}
	
	public static void registerListener(Layer layer, HoverListener listener) {
		layerMap.put(layer, listener);
	}
	
	public static void registerListener(LayerLike layer, HoverListener listener) {
		layerMap.put(layer, listener);
	}
	
	public static boolean isMouseOver(LayerLike layer) {
		if (layer == null || layer.destroyed() || 
				!PlayN.mouse().hasMouse()) return false;
		Point p = getLocalPoint(layer.layerAddable());
		Layer hit = layer.hitTest(p);
		return layer.incorporatesLayer(hit);
	}
	
	public static boolean isMouseOver(Layer layer) {
		if (layer == null || layer.destroyed() || 
				!PlayN.mouse().hasMouse()) return false;
		Point p = getLocalPoint(layer);
		Layer hit = layer.hitTest(p);
		return hit == layer;
	}
	
	private static Point getLocalPoint(Layer layer) {		
		testPoint.set(lastMousePosition);		
		Layer.Util.screenToLayer(layer, testPoint, testPoint);

		return testPoint;
	}
	
	private static class Listener implements Mouse.Listener {

		@Override
		public void onMouseDown(ButtonEvent event) { }

		@Override
		public void onMouseUp(ButtonEvent event) { }

		@Override
		public void onMouseMove(MotionEvent event) {
			lastMousePosition.set(event.x(), event.y());
			
			for (Object key : layerMap.keySet()) {
				HoverListener listener = layerMap.get(key);
				if (key instanceof Layer) {
					listener.hoverChanged(isMouseOver((Layer) key));
				} else if (key instanceof LayerLike) {
					listener.hoverChanged(isMouseOver((LayerLike) key));
				}
			}
		}

		@Override
		public void onMouseWheelScroll(WheelEvent event) { }
		
	}
	
	public interface HoverListener {
		public void hoverChanged(boolean hovering);
	}

	public static void clear() {
		layerMap.clear();
	}
}
