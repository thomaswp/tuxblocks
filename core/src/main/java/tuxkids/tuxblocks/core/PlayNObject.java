package tuxkids.tuxblocks.core;

import playn.core.Graphics;
import playn.core.GroupLayer;
import playn.core.Keyboard;
import playn.core.PlayN;
import playn.core.Pointer;

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
	
	protected static int gWidth() {
		return graphics().width();
	}
	
	protected static int gHidth() {
		return graphics().height();
	}
	
	protected static GroupLayer gRootLayer() {
		return graphics().rootLayer();
	}
}
