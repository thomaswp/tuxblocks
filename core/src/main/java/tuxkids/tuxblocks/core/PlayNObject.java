package tuxkids.tuxblocks.core;

import playn.core.Graphics;
import playn.core.GroupLayer;
import playn.core.Keyboard;
import playn.core.PlayN;
import playn.core.Pointer;
import tuxkids.tuxblocks.core.utils.Debug;

public class PlayNObject {
	protected static Graphics graphics() {
		return PlayN.graphics();
	}
	
	protected static Pointer pointer() {
		return PlayN.pointer();
	}
	
	protected static Keyboard keyboard() {
		return PlayN.keyboard();
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
	
	public static float lerp(float x0, float x1, float perc) {
		return x0 * (1 - perc) + x1 * perc;
	}
}
