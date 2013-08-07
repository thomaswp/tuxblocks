package tuxkids.tuxblocks.core;

import playn.core.Color;
import playn.core.Image;
import playn.core.Layer;
import playn.core.Layer.HitTester;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Callback;
import pythagoras.f.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.utils.Positioned;

public class Button extends PlayNObject implements Positioned {
	
	public final static float UNPRESSED_ALPHA = 0.5f;
	
	private final ImageLayerTintable imageLayer;
	private final boolean isCircle;
	
	private OnPressedListener onPressedListener;
	private OnReleasedListener onReleaseListener;
	private OnDragListener onDragListener;
	private float width, height;
	private boolean pressed;
	private int tint, tintPressed;
	private boolean enabled = true;

	public Layer layerAddable() {
		return imageLayer.layerAddable();
	}
	
	public ImageLayerTintable imageLayer() {
		return imageLayer;
	}
	
	public Image image() {
		return imageLayer.image();
	}
	
	@Override
	public float x() {
		return imageLayer.tx();
	}

	@Override
	public float y() {
		return imageLayer.ty();
	}

	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	public boolean isCircle() {
		return isCircle;
	}

	public boolean pressed() {
		return pressed;
	}
	
	public int tint() {
		return imageLayer.tint();
	}
	
	public boolean enabled() {
		return enabled;
	}
	
	public float left() {
		return x() - width / 2;
	}
	
	public float top() {
		return y() - height / 2;
	}
	
	public float right() {
		return x() + width / 2;
	}
	
	public float bottom() {
		return y() + height / 2;
	}
	
	public void setLeft(float left) {
		setX(left + width / 2);
	}
	
	public void setTop(float top) {
		setY(top + height / 2);
	}
	
	public void setRight(float right) {
		setX(right - width / 2);
	}
	
	public void setBottom(float bottom) {
		setY(bottom - height / 2);
	}
	
	public void setX(float x) {
		imageLayer.setTx(x);
	}
	
	public void setY(float y) {
		imageLayer.setTy(y);
	}
	
	public void setPosition(float x, float y) {
		imageLayer.setTranslation(x, y);
	}
	
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		if (imageLayer.image().isReady()) {
			adjustScale();
		}
	}
	
	public void setImage(Image image) {
		imageLayer.setImage(image);
		imageLayer.image().addCallback(new Callback<Image>() {
			@Override
			public void onSuccess(Image result) {
				imageLayer.setOrigin(result.width() / 2, result.height() / 2);
				adjustScale();
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
	}
	
	public void setOnPressListener(OnPressedListener onPressedListener) {
		this.onPressedListener = onPressedListener;
	}
	
	public void setOnReleasedListener(OnReleasedListener onReleasedListener) {
		this.onReleaseListener = onReleasedListener;
	}
	
	public void setOnDragListener(OnDragListener onDragListener) {
		this.onDragListener = onDragListener;
	}
	
	public void setTint(int tint) {
		setTint(tint, UNPRESSED_ALPHA);
	}
	
	public void setTint(int tint, float alphaUnpressed) {
		setTint(Color.withAlpha(tint, (int)(255 * alphaUnpressed)), tint);
	}
	
	public void setTint(int tint, int tintPressed) {
		this.tint = tint;
		this.tintPressed = tintPressed;
		refreshTint();
	}
	
	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) return;
		this.enabled = enabled;
		refreshTint();
	}
	
	public Button(String imagePath, float width, float height, boolean isCircle) {
		this(assets().getImage(imagePath), width, height, isCircle);
	}
	
	private void refreshTint() {
		int tint = pressed ? tintPressed : this.tint;
		if (!enabled) tint = Colors.blend(tint, Color.withAlpha(Colors.BLACK, Color.alpha(tint)), 0.5f);
		imageLayer.setTint(tint);
	}
	
	public Button(final Image image, float width, float height, boolean isCircle) {
		this.width = width;
		this.height = height;
		this.isCircle = isCircle;
		imageLayer = new ImageLayerTintable();
		setImage(image);
		imageLayer.addListener(new PointerListener());
		setTint(Colors.WHITE, UNPRESSED_ALPHA);
		if (isCircle) {
			imageLayer.setHitTester(new HitTester() {
				@Override
				public Layer hitTest(Layer layer, Point p) {
					if (p.distance(image.width() / 2, image.height() / 2) < image.width() / 2) return layer;
					return null;
				}
			});
		}
	}
	
	public Button(Image image, boolean isCircle) {
		this(image, image.width(), image.height(), isCircle);
	}
	
	public void destroy() {
		imageLayer.destroy();
	}

	public boolean hit(float x, float y) {
		if (isCircle) {
			return hitCircle(x, y);
		} else {
			return hitRectangle(x, y);
		}
	}
	
	public boolean hitRectangle(float x, float y) {
		return Math.abs(x - x()) < width / 2 && Math.abs(y - y()) < height / 2;
	}
	
	public boolean hitCircle(float x, float y) {
		return distance(x(), y(), x, y) < width / 2;
	}
	
	private void adjustScale() {
		imageLayer.setScale(width / imageLayer.image().width(), 
				height / imageLayer.image().height());
	}

	private class PointerListener implements Listener {
		
		@Override
		public void onPointerStart(Event event) {
			if (!enabled || !insideLocal(event)) return;
			pressed = true;
			refreshTint();
			if (onPressedListener != null) onPressedListener.onPress(event);
		}

		@Override
		public void onPointerEnd(Event event) {
			if (!enabled || !pressed) return;
			pressed = false;
			refreshTint();
			if (onReleaseListener != null) onReleaseListener.onRelease(event, insideLocal(event));
		}
		
		private boolean insideLocal(Event event) {
			float dw = image().width() / 2;
			float dh = image().height() / 2;
			if (isCircle) {
				return distance(event.localX(), event.localY(), dw, dh) <= dw;
			} else {
				return Math.abs(event.localX() - dw) <= dw &&
						Math.abs(event.localY() - dh) <= dh;
			}
		}

		@Override
		public void onPointerDrag(Event event) {
			if (!enabled) return; 
			if (onDragListener != null) onDragListener.onDrag(event);
		}

		@Override
		public void onPointerCancel(Event event) { }
	}
	
	public interface OnReleasedListener {
		public void onRelease(Event event, boolean inButton);
	}
	
	public interface OnPressedListener {
		public void onPress(Event event);
	}
	
	public interface OnDragListener {
		public void onDrag(Event event);
	}
}
