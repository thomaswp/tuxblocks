package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import pythagoras.f.Rectangle;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.markup.Renderer;

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
	
	//to avoid anonymous class allocation, we make one static instance
	private static class Search implements Action {
		private Sprite sprite;
		private boolean contains;
		
		Search set(Sprite sprite) {
			this.sprite = sprite;
			contains = false;
			return this;
		}
		
		@Override
		public void run(Sprite sprite) {
			if (sprite == this.sprite) contains = true;
		}
	}
	private static Search search = new Search();
	protected boolean contains(Sprite sprite) {
		performAction(search.set(sprite));
		return search.contains;
	}
	
	//to avoid anonymous class allocation, we make one static instance
	private static class Intersect implements Action {
		private Rectangle rect = new Rectangle(), compareRect = new Rectangle();
		private boolean contains;
		
		Intersect set(Sprite sprite) {
			set(rect, sprite);
			contains = false;
			return this;
		}
		
		void set(Rectangle rect, Sprite sprite) {
			rect.setBounds(getGlobalTx(sprite.layer()), getGlobalTy(sprite.layer()), 
					sprite.width(), sprite.height());
		}
		
		@Override
		public void run(Sprite sprite) {
			set(compareRect, sprite);
			contains |= compareRect.intersects(rect);
		}
	}
	private static Intersect intersect = new Intersect();
	public boolean intersects(Sprite sprite) {
		performAction(intersect.set(sprite));
		return intersect.contains;
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
	
	public final static int baseSize() {
		return (int)(graphics().height() * 0.185f);
	}
	
	public final static int modSize() {
		return (int)(baseSize() * 0.4f);
	}
	
	public final static int wrapSize() {
		return Math.max(modSize() / 5, 6);
	}
	
	public final static float textSize() {
		return Math.max(12, baseSize() / 5f);
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

		void wasGrabbed(Block sprite, Event event);
		void wasReleased(Event event);
		void wasMoved(Event event);
		void wasDoubleClicked(Block sprite, Event event);
		void wasSimplified();
		void wasReduced(Renderer problem, int answer, int startNumber, 
				Stat stat, int level, SimplifyListener callback);
		void wasCanceled();
		boolean inBuildMode();
		
	}
	
	protected interface Action {
		void run(Sprite sprite);
	}
	
	public interface SimplifyListener {
		void wasSimplified(boolean success);
	}
}
