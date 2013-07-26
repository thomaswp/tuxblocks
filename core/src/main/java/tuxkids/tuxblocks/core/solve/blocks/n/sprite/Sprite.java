package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;

public abstract class Sprite extends PlayNObject {

	protected static final int SIMPLIFY_DEPTH = 1;
	
	private boolean hasSprite, previewAdd, destoryed;
	protected BlockListener blockListener;

	protected abstract Sprite copyChild();
	protected abstract void addBlockListener(BlockListener blockListener);
	public abstract void update(int delta);
	public abstract void paint(Clock clock);
	public abstract Layer layer();
	public abstract float width();
	public abstract float height();
	
	public void destroy() {
		if (hasSprite()) layer().destroy();
		destoryed = true;
	}
	
	public boolean destroyed() {
		return destoryed;
	}
	
	protected void performAction(Action action) {
		action.run(this);
	}
	
	protected void setPreviewAdd(boolean previewAdd) {
		this.previewAdd = previewAdd;
	}
	
	protected boolean previewAdd() {
		return previewAdd;
	}
	
	public boolean hasSprite() {
		return hasSprite;
	}
	
	protected static int baseSize() {
		return 100;
	}
	
	protected static int modSize() {
		return 40;
	}
	
	protected static int wrapSize() {
		return 8;
	}
	
	protected static float lerpBase() {
		return 0.992f;
	}
	
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
	
	public float centerX() {
		return x() + width() / 2;
	}
	
	public float centerY() {
		return y() + height() / 2;
	}
	
	public boolean contains(float x, float y) {
		return x >= left() && x <= right() && y >= top() && y <= bottom(); 
	}
	
	protected void initSprite() {
		if (hasSprite) return;
		initSpriteImpl();
		if (hasSprite) PlayN.log().warn("Warning: initSpriteImpl() sets hasSprite");
		hasSprite = true;
	}
	
	protected void initSpriteImpl() { }
	
	public final Sprite copy() {
		return copy(false);
	}
	
	protected void copyFields(Sprite castMe) {
		
	}
	
	public final Sprite copy(boolean init) {
		Sprite copy = copyChild();
		copyFields(copy);
		if (init) {
			copy.addBlockListener(blockListener);
			if (hasSprite()) copy.initSprite();
		}
		return copy;
	}
	
	public interface BlockListener {

		void wasGrabbed(BlockSprite sprite, Event event);
		void wasReleased(Event event);
		void wasMoved(Event event);
		void wasDoubleClicked(BlockSprite sprite, Event event);
		void wasSimplified();
		void wasReduced(Renderer problem, int answer, int startNumber, SimplifyListener callback);
		void wasCanceled();
		
	}
	
	protected interface Action {
		void run(Sprite sprite);
	}
	
	public interface SimplifyListener {
		void wasSimplified(boolean success);
	}
}
