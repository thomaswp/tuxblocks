package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Color;
import playn.core.Font;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.solve.blocks.layer.BlockLayer;
import tuxkids.tuxblocks.core.solve.blocks.layer.BlockLayerDefault;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

/**
 * Base class for blocks which make up the visual algebra system.
 */
public abstract class Block extends Sprite implements Hashable {

	private final static int DOUBLE_CLICK = 500;
	
	// color constants for the various blocks
	protected final static int COLOR_PLUS = Color.rgb(0xF7, 0x04, 0x04);
	protected final static int COLOR_MINUS = Color.rgb(0x11, 0x4C, 0xA3);
	protected final static int COLOR_TIMES = Color.rgb(0xF7, 0x9D, 0x04);
	protected final static int COLOR_OVER = Color.rgb(0x03, 0xC6, 0x03);
	protected final static int COLOR_NEUTRAL = Color.rgb(0x96, 0x96, 0x96);
	
	protected BlockLayer layer;
	protected boolean multiExpression = true;
	
	// are we in the middle of a player's drag right now?
	private boolean dragging;
	// time since last click for measuring a double-click
	private int doubleClickTime;
	// an increasing number for synchronizing flashing colors
	private int timeElapsed;
	// can this block be picked up?
	private boolean showReleaseIndicator;
	
	protected static TextFormat textFormat;
	
	/** Text to display on the block */
	protected abstract String text();
	/** Width this block should assume if not wrapping other blocks */
	protected abstract float defaultWidth();
	/** Height this block should assume if not wrapping other blocks */
	protected abstract float defaultHeight();
	/** Can this block be picked up right now */
	protected abstract boolean canRelease(boolean multiExpression);
	/** The color of this block */
	protected abstract int color();
		
	/** Make this block show its inverse (when the equals sign is crossed) */
	public abstract void showInverse();
	/** Gets this block's inverse (when the equals sign is crossed) */
	public abstract Block inverse();
	
	public Block() {
		if (textFormat == null) {
			Font font = PlayN.graphics().createFont(Constant.NUMBER_FONT, Font.Style.PLAIN, textSize());
			textFormat = new TextFormat().withFont(font);
		}
	}
	
	@Override
	public void initSprite() {
		if (hasSprite()) return;
		super.initSprite();
		if (blockListener != null) {
			attachBlockListener();
		}
	}
	
	/** 
	 * Call from a paint method to interpolate to this block to it's 
	 * default rect (or pass null as the clock to snap to the default rect) 
	 */
	public final void interpolateDefaultRect(Clock clock) {
		float base, dt;
		if (clock == null) {
			base = 0; dt = 1;
		} else {
			base = lerpBase();
			dt = clock.dt();
		}
		interpolateRect(layer().tx(), layer().ty(), defaultWidth(), defaultHeight(), base, dt);
	}
	
	/** Call from a paint method to interpolate to this block to the given rect */
	public void interpolateRect(float x, float y, float width, float height, float base, float dt) {
		float snap = 1f;
		layer().setTx(lerpTime(layer().tx(), x, base, dt, snap));
		layer().setTy(lerpTime(layer().ty(), y, base, dt, snap));
		layer.setWidth(lerpTime(width(), width, base, dt, snap));
		layer.setHeight(lerpTime(height(), height, base, dt, snap));
	}
	
	@Override
	public Layer layer() {
		return layer.layerAddable();
	}

	@Override
	public float width() {
		return layer.width();
	}

	@Override
	public float height() {
		return layer.height();
	}
	
	protected BlockLayer generateImage(String text) {
		return new BlockLayerDefault(text, 10, 10);
	}

	/**
	 * Update this block, passing whether there are multiple expressions
	 * on this block's side of the equation.
	 */
	public void update(int delta, boolean multiExpression) {
		this.multiExpression = multiExpression;
		update(delta);
	}
	
	@Override
	public void update(int delta) {
		if (doubleClickTime > 0) {
			doubleClickTime = Math.max(0, doubleClickTime - delta);
		}
		if (showReleaseIndicator != shouldShowReleaseIndicator(multiExpression)) {
			showReleaseIndicator = !showReleaseIndicator;
		}
	}
	
	/** 
	 * Indicates whether this block should show the flashing animation, indicating
	 *  that it can be released.
	 */
	protected boolean shouldShowReleaseIndicator(boolean multiExpression) {
		return canRelease(multiExpression);
	}
	
	float[] hsv = new float[3];
	@Override
	public void paint(Clock clock) {
		timeElapsed = PlayN.tick();
		int color = color();
		if (showReleaseIndicator) {
			// convert the color to hsv
			CanvasUtils.rgbToHsv(color, hsv);
			// the base color is darkened
			color = CanvasUtils.hsvToRgb(hsv[0], hsv[1], 0.7f);
			// the flash color is lightened
			int flashColor = CanvasUtils.hsvToRgb(hsv[0], hsv[1], 1f);
			
			// do FancyMath (TM) to make it flash, once every 1.25 seconds
			layer.setTint(flashColor, color, FloatMath.pow(FloatMath.sin(
					timeElapsed / 1250f * 2 * FloatMath.PI) / 2 + 0.5f, 0.7f));
		} else {
			layer.setTint(color);
		}
	}
	
	@Override
	public void addBlockListener(BlockListener listener) {
		if (listener == null || blockListener != null) return;
		blockListener = listener;
		if (hasSprite()) attachBlockListener();
	}
	
	/** Tell this block it is no longer being dragged */
	public void cancelDrag() {
		dragging = false;
	}
	
	// called only if there's no blockListener
	private void attachBlockListener() {
		layer.addListener(new Listener() {
			
			@Override
			public void onPointerStart(Event event) {
				if (canRelease(multiExpression)) {
					// start a drag
					dragging = true;
					blockListener.wasGrabbed(Block.this, event);
				} else if (doubleClickTime == 0) {
					// start a double-click
					doubleClickTime = DOUBLE_CLICK;
				} else {
					// finish a double-click
					blockListener.wasDoubleClicked(Block.this, event);
				}
			}
			
			@Override
			public void onPointerEnd(Event event) {
				if (dragging) {
					dragging = false;
					blockListener.wasReleased(event);
				}
			}
			
			@Override
			public void onPointerDrag(Event event) {
				if (dragging) {
					blockListener.wasMoved(event);
				}
			}
			
			@Override
			public void onPointerCancel(Event event) {
				onPointerEnd(event);
			}
		});
	}
	
	/** 
	 * Gets the block to drag when this block is picked up.
	 * By default, it returns this block, but some blocks, such
	 * as {@link ModifierBlock}s will return a {@link NumberBlock} 
	 * instead.
	 */
	protected Block getDraggingSprite() {
		return this;
	}
	
	/**
	 * Removes this block from its group (if it has one,
	 * such as a {@link ModifierBlock}.
	 */
	public void remove() {
	}
	
	@Override
	public String toString() {
		return "[" + text() + "]";
	}
}
