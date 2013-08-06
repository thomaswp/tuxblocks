package tuxkids.tuxblocks.core.solve.blocks;

import java.util.HashMap;

import playn.core.Font;
import playn.core.Image;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.layers.ImageLayerLike.Factory;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.solve.blocks.layer.BlockLayer;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public abstract class Block extends Sprite implements Hashable {

	private final static int DOUBLE_CLICK = 500;
	
	protected ImageLayerLike layer;
	protected boolean multiExpression = true;
	
	private boolean dragging;
	private int doubleClickTime;
	private HashMap<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
	private int timeElapsed;
	private boolean canRelease;
	
	protected static TextFormat textFormat;
	protected static Factory factory;
	
	protected abstract String text();
	protected abstract float defaultWidth();
	protected abstract float defaultHeight();
	protected abstract boolean canRelease(boolean multiExpression);
	protected abstract int color();
	
	public abstract void showInverse();
	public abstract Block inverse();
	
	public Block() {
		if (textFormat == null) {
			Font font = PlayN.graphics().createFont(Constant.FONT_NAME, Font.Style.PLAIN, textSize());
			textFormat = new TextFormat().withFont(font);
		}
		if (factory == null) {
			factory = new Factory() {
				@Override
				public ImageLayerLike create(Image image) {
					return new ImageLayerTintable(image);
				}
			};
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
	
	protected ImageLayerLike generateImage(String text) {
		return new BlockLayer(text, 10, 10);
	}

	
	public void update(int delta, boolean multiExpression) {
		this.multiExpression = multiExpression;
		update(delta);
	}
	
	@Override
	public void update(int delta) {
		if (doubleClickTime > 0) {
			doubleClickTime = Math.max(0, doubleClickTime - delta);
		}
		if (canRelease != shouldShowPreview(multiExpression)) {
			canRelease = !canRelease;
		}
	}
	
	protected boolean shouldShowPreview(boolean multiExpression) {
		return canRelease(multiExpression);
	}
	
	float[] hsv = new float[3];
	@Override
	public void paint(Clock clock) {
		timeElapsed = PlayN.tick();
		int color = color();
		if (canRelease) {
			CanvasUtils.rgbToHsv(color, hsv);
			color = CanvasUtils.hsvToRgb(hsv[0], hsv[1], 0.7f);
			int flashColor = CanvasUtils.hsvToRgb(hsv[0], hsv[1], 1f);
			layer.setTint(flashColor, color, FloatMath.pow(FloatMath.sin(timeElapsed / 1250f * 2 * FloatMath.PI) / 2 + 0.5f, 0.7f));
		} else {
			layer.setTint(color);
		}
	}
	
	static int offset = 0; //(int)(360 * Math.random());
	protected int getColor(int degree) {
		Integer color = colorMap.get(degree);
		degree += offset;
		degree = degree % 360;
		if (degree <= 120) {
			degree /= 2;
		} else if (degree <= 180) {
			degree -= 60;
		} else if (degree < 240) {
			degree = (degree - 180) * 2 + 120;
		}
		if (color == null) {
			color = CanvasUtils.hsvToRgb((degree%360) / 360f, 0.9f, 0.9f);
			colorMap.put(degree, color);
		}
		return color;
	}
	
	@Override
	public void addBlockListener(BlockListener listener) {
		if (listener == null || blockListener != null) return;
		blockListener = listener;
		if (hasSprite()) attachBlockListener();
	}
	
	private void attachBlockListener() {
		layer.addListener(new Listener() {
			
			@Override
			public void onPointerStart(Event event) {
				if (canRelease(multiExpression)) {
					dragging = true;
					blockListener.wasGrabbed(Block.this, event);
				} else if (doubleClickTime == 0) {
					doubleClickTime = DOUBLE_CLICK;
				} else {
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
				
			}
		});
	}
	
	protected Block getDraggingSprite() {
		return this;
	}
	
	public void remove() {
	}
	
	@Override
	public String toString() {
		return text();
	}
}
