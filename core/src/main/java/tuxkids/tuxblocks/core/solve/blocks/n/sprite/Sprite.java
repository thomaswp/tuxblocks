package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Layer;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.PlayNObject;

public abstract class Sprite extends PlayNObject {
	
	protected static int baseSize() {
		return 150;
	}
	
	protected static int modSize() {
		return 50;
	}
	
	protected static int wrapSize() {
		return 8;
	}
	
	public abstract Layer layer();
	public abstract float width();
	public abstract float height();
	
	public float x() {
		return layer().tx();
	}
	
	public float y() {
		return layer().ty();
	}
	
	public float top() {
		return y();
	}
	
	public float left() {
		return x();
	}
	
	public float right() {
		return x() + width();
	}
	
	public float bottom() {
		return y() + height();
	}
	
	public boolean contains(float x, float y) {
		return x >= left() && x <= right() && y >= top() && y <= bottom(); 
	}
	
	public abstract void update(int delta);
	public abstract void paint(Clock clock);
}
