package tuxkids.tuxblocks.core;

import playn.core.Assets;
import playn.core.Graphics;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Keyboard;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import playn.core.util.Callback;
import pythagoras.f.FloatMath;
import pythagoras.f.IVector;
import pythagoras.f.Vector;
import pythagoras.i.IPoint;
import pythagoras.i.Point;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;
import tuxkids.tuxblocks.core.utils.Positioned;

public abstract class PlayNObject {
	protected static Graphics graphics() {
		return PlayN.graphics();
	}
	
	protected static Pointer pointer() {
		return PlayN.pointer();
	}
	
	protected static Keyboard keyboard() {
		return PlayN.keyboard();
	}
	
	protected static Assets assets() {
		return PlayN.assets();
	}
	
	protected static long currentTime() {
		return (long)PlayN.currentTime();
	}
	
	protected static int gWidth() {
		return graphics().width();
	}
	
	protected static int gHidth() {
		return graphics().height();
	}
	
	protected static GroupLayer gRootLayer() {
		return graphics().rootLayer();
	}
	
	protected static void debug(String msg) {
		Debug.write(msg);
	}
	
	protected static void debug(String msg, Object... args) {
		Debug.write(msg, args);
	}
	
	protected static void debug(int msg) {
		Debug.write(msg);
	}
	
	protected static void debug(float msg) {
		Debug.write(msg);
	}
	
	protected static void debug(double msg) {
		Debug.write(msg);
	}
	
	protected static void debug(long msg) {
		Debug.write(msg);
	}
	
	protected static void debug(Object o) {
		Debug.write(o);
	}
	
	public static float lerp(float x0, float x1, float perc) {
		return x0 * (1 - perc) + x1 * perc;
	}
	
	public static void lerp(Vector v0, float x1, float y1, float perc) {
		v0.x = lerp(v0.x, x1, perc);
		v0.y = lerp(v0.y, y1, perc);
	}
	
	public static float lerpTime(float x0, float x1, float base, float dt) {
		float perc = 1 - FloatMath.pow(base, dt);
		return x0 * (1 - perc) + x1 * perc;
	}

	public static void lerpTime(Vector v0, int x1, int y1, float base, float dt) {
		v0.x = lerpTime(v0.x, x1, base, dt);
		v0.y = lerpTime(v0.y, y1, base, dt);
	}
	
	public static float distance(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		return FloatMath.sqrt(dx * dx + dy * dy);
	}
	
	public static float distance(Object o1, Object o2) {
		return distance(getX(o1), getY(o1), getX(o2), getY(o2));
	}
	
	public static float distance(Object o1, float x2, float y2) {
		return distance(getX(o1), getY(o1), x2, y2);	
	}

	private static float getX(Object o) {
		if (o instanceof IPoint) {
			return ((IPoint) o).x();
		} else if (o instanceof IVector) {
			return ((IVector) o).x();
		} else if (o instanceof Event) {
			return ((Event) o).x();
		} else if (o instanceof Layer) {
			return ((Layer) o).tx();
		} else if (o instanceof Positioned) {
			return ((Positioned) o).x();
		}
		return 0;
	}
	
	private static float getY(Object o) {
		if (o instanceof IPoint) {
			return ((IPoint) o).y();
		} else if (o instanceof IVector) {
			return ((IVector) o).y();
		} else if (o instanceof Event) {
			return ((Event) o).y();
		} else if (o instanceof Layer) {
			return ((Layer) o).ty();
		} else if (o instanceof Positioned) {
			return ((Positioned) o).y();
		}
		return 0;
	}
	
	protected static void centerImageLayer(final ImageLayer layer) {
		if (layer.image() != null) {
			layer.image().addCallback(new Callback<Image>() {
				@Override
				public void onSuccess(Image result) {
					layer.setOrigin(result.width() / 2, result.height() / 2);
				}

				@Override
				public void onFailure(Throwable cause) { }	
			});
			
		}
	}
	
	protected static void centerImageLayer(final ImageLayerTintable layer) {
		if (layer.image() != null) {
			layer.image().addCallback(new Callback<Image>() {
				@Override
				public void onSuccess(Image result) {
					layer.setOrigin(result.width() / 2, result.height() / 2);
				}

				@Override
				public void onFailure(Throwable cause) { }	
			});
			
		}
	}
	
	public static float getGlobalTx(Layer layer) {
		Layer parent = layer;
		float tx = 0;
		while (parent != null) {
			tx += parent.tx();
			parent = parent.parent();
		}
		return tx;
	}
	
	public static float getGlobalTy(Layer layer) {
		Layer parent = layer;
		float ty = 0;
		while (parent != null) {
			ty += parent.ty();
			parent = parent.parent();
		}
		return ty;
	}
	
	private HashCode hashCode;
	protected PlayNObject() {
		if (this instanceof Hashable) hashCode = new HashCode((Hashable) this);
	}
	
	@Override
	public int hashCode() {
		if (hashCode == null) return super.hashCode();
		return hashCode.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (hashCode == null) return super.equals(obj);
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		return hashCode.equals(((PlayNObject) obj).hashCode);
	}
}
