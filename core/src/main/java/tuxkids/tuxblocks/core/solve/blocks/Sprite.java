package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import pythagoras.f.Rectangle;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.SolveScreen;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * Base class for both {@link Block}s and {@link ModifierGroup}s.
 * Contains methods for creating and managing {@link Layer}s and
 * positions. 
 */
public abstract class Sprite extends PlayNObject implements Persistable {

	/** Depth for the {@link SimplifyLayer} */
	protected static final int SIMPLIFY_DEPTH = 1;
	
	private boolean hasSprite, previewAdd, destoryed;
	protected BlockListener blockListener;

	/** Should return a new instance of the base class */
	protected abstract Sprite copyChild();
	/** Should register this Block listener with the Sprite */
	protected abstract void addBlockListener(BlockListener blockListener);
	public abstract void update(int delta);
	public abstract void paint(Clock clock);
	/** Should return the base layer for this sprite */
	public abstract Layer layer();
	public abstract float width();
	public abstract float height();
	
	/** Destroys this Sprite and its associated Layers */
	public void destroy() {
		if (hasSprite()) layer().destroy();
		destoryed = true;
	}
	
	/** Returns true if this Sprite is destroyed and can no longer be used */
	public boolean destroyed() {
		return destoryed;
	}
	
	/** 
	 * This method should call {@link Action#run(Sprite)} on this Sprite
	 * and recursively on any of its children. Subclasses should override
	 * this method with a custom implementation.
	 */
	public void performAction(Action action) {
		action.run(this);
	}

	// An action for finding a given child Sprite
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
	//to avoid anonymous class allocation, we make one static instance
	private static Search search = new Search();
	
	/** Returns true if this Sprite or any of its children are the given Sprite */
	protected boolean contains(Sprite sprite) {
		performAction(search.set(sprite));
		return search.contains;
	}
	
	// An Action for telling if a Sprite intersects a rect
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
	//to avoid anonymous class allocation, we make one static instance
	private static Intersect intersect = new Intersect();
	
	/** Returns true of this Sprite or its children intersect the given Sprite */
	public boolean intersects(Sprite sprite) {
		performAction(intersect.set(sprite));
		return intersect.contains;
	}
	
	/** Tells this Sprite whether to show up in the preview color when creating a {@link Renderer} */
	protected void setPreviewAdd(boolean previewAdd) {
		this.previewAdd = previewAdd;
	}
	
	/** Returns true if this Sprite should show up in the preview color when creating a {@link Renderer} */
	protected boolean previewAdd() {
		return previewAdd;
	}
	
	/** Returns true if this Sprite has created a {@link Layer} to be displayed */
	public boolean hasSprite() {
		return hasSprite;
	}
	
	/** The length an edge of a square block */
	public final static int baseSize() {
		return (int)(graphics().height() * 0.185f);
	}
	
	/** The length of the short side of a {@link ModifierBlock} */
	public final static int modSize() {
		return (int)(baseSize() * 0.45f);
	}
	
	/** The length of the wrap arm of a {@link TimesBlock} */
	public final static int wrapSize() {
		return Math.max(modSize() / 6, 6);
	}
	
	/** The text size for Block text */
	public final static float textSize() {
		return Math.max(8, baseSize() / 5f);
	}
	
	/** The standard base to use when calling {@link PlayNObject#lerpTime(float, float, float, float)} */
	protected static float lerpBase() {
		return 0.992f;
	}
	
	/** Returns the x-coordinate of this Sprite's top-left corner */
	public float x() {
		return layer().tx();
	}
	
	/** Returns the y-coordinate of this Sprite's top-left corner */
	public float y() {
		return layer().ty();
	}
	
	/** Returns the x-coordinate of this Sprite's left side */
	public float left() {
		return x();
	}
	
	/** Returns the y-coordinate of this Sprite's top side */
	public float top() {
		return y();
	}
	
	/** Returns the x-coordinate of this Sprite's right side */
	public float right() {
		return x() + width();
	}
	
	/** Returns the y-coordinate of this Sprite's bottom side */
	public float bottom() {
		return y() + height();
	}
	
	/** Returns the x-coordinate of this Sprite's center */
	public float centerX() {
		return x() + width() / 2;
	}
	
	/** Returns the y-coordinate of this Sprite's center */
	public float centerY() {
		return y() + height() / 2;
	}
	
	/** Returns true if the given coordinates are within this Sprite or its children */
	public boolean contains(float x, float y) {
		return x >= left() && x <= right() && y >= top() && y <= bottom(); 
	}
	
	/** Attempts to create a {@link Layer} for this sprite */
	protected void initSprite() {
		if (hasSprite) return;
		initSpriteImpl();
		if (hasSprite) PlayN.log().warn("Warning: initSpriteImpl() sets hasSprite"); // this shouldn't happen :)
		hasSprite = true;
	}
	
	/** 
	 * Should create the {@link Layer} for this Sprite and perform any other logic that
	 * needs to happen before it's displayed. 
	 */
	protected void initSpriteImpl() { }
	
	/** 
	 * Returns a working copy of this Sprite. Not all fields are copied, just a value fields
	 * such a {@link NumberBlock}'s number.
	 */
	public final Sprite copy() {
		return copy(false);
	}
	
	/** Should copy all relevant fields to the given Sprite, such as a {@link NumberBlock}'s number. */
	protected void copyFields(Sprite castMe) {
		
	}
	
	/**
	 * Returns a working copy of this Sprite. Not all fields are copied, just a value fields
	 * such a {@link NumberBlock}'s number. Optionally initializes the Sprite for display.
	 */
	public final Sprite copy(boolean init) {
		Sprite copy = copyChild();
		copyFields(copy);
		if (init) {
			copy.addBlockListener(blockListener);
			if (hasSprite()) copy.initSprite();
		}
		return copy;
	}
	
	/** A hook for all events that can happen to a Sprite */
	public interface BlockListener {

		/** Called when a Sprite is grabbed by the player */
		void wasGrabbed(Block sprite, Event event);
		/** Called when a Sprite is released by the player */
		void wasReleased(Event event);
		/** Called when a Sprite is moved by the player */
		void wasMoved(Event event);
		/** Called when a Sprite is double-clicked by the player */
		void wasDoubleClicked(Block sprite, Event event);
		/** Called when a Sprite is simplified and the equation need refreshing */
		void wasSimplified();
		/** Called when a Sprite needs the {@link SolveScreen} to reduce */
		void wasReduced(ModifierBlock sprite, ModifierBlock pair, ModifierGroup modifiers, 
				Renderer problem, int answer, int startNumber, 
				Stat stat, int level, SimplifyListener callback);
		/** Called when a Sprite's movement is cancelled */
		void wasCanceled();
		/** Should return true if the current equation is in BuildMode */
		boolean inBuildMode();
		
	}
	
	/** Used in {@link Sprite#performAction(Action)} */
	public interface Action {
		void run(Sprite sprite);
	}
	
	/** Callback for {@link BlockListener#wasReduced(Renderer, int, int, Stat, int, SimplifyListener)} */
	public interface SimplifyListener {
		void wasSimplified(boolean success);
	}
}