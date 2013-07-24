package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Font;
import playn.core.Image;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.layers.ImageLayerLike.Factory;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public abstract class BlockSprite extends Sprite implements Hashable {

	private final static int DOUBLE_CLICK = 500;
	
	protected ImageLayerLike layer;
	protected boolean multiExpression = true;
	
	private boolean dragging;
	private int doubleClickTime;
	
	protected static TextFormat textFormat;
	protected static Factory factory;
	
	protected abstract String text();
	protected abstract float defaultWidth();
	protected abstract float defaultHeight();
	protected abstract boolean canRelease(boolean multiExpression);
	public abstract BlockSprite inverse();
	
	public BlockSprite() {
		if (textFormat == null) {
			Font font = PlayN.graphics().createFont(Constant.FONT_NAME, Font.Style.PLAIN, 20);
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
	
	protected void initSprite() {
		if (hasSprite()) return;
		super.initSprite();
		if (blockListener != null) {
			attachBlockListener();
		}
	}
	
	public void interpolateDefaultRect(Clock clock) {
		interpolateRect(layer().tx(), layer().ty(), defaultWidth(), defaultHeight(), lerpBase(), clock.dt());
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
	
	protected ImageLayerLike generateNinepatch(String text, int color) {
		return new BlockLayer(text, 10, 10);
	}
	
	public void update(int delta) {
		if (doubleClickTime > 0) {
			doubleClickTime = Math.max(0, doubleClickTime - delta);
		}
	}
	
	public void update(int delta, boolean multiExpression) {
		this.multiExpression = multiExpression;
		update(delta);
	}
	
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
					blockListener.wasGrabbed(BlockSprite.this, event);
				} else if (doubleClickTime == 0) {
					doubleClickTime = DOUBLE_CLICK;
				} else {
					blockListener.wasDoubleClicked(BlockSprite.this, event);
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
	
	protected BlockSprite getDraggingSprite() {
		return this;
	}
	
	public void remove() {
	}
	
	public void showInverse() {
		if (!hasSprite()) return;
		layer.setVisible(false);
		BlockSprite inverse = inverse();
		inverse.layer.setVisible(true);
		inverse.interpolateRect(x(), y(), width(), height(), 0, 1);
		inverse.layer().setTranslation(layer().tx(), layer().ty());
	}
	
	@Override
	public String toString() {
		return text();
	}
}
