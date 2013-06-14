package tuxkids.tuxblocks.core;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Callback;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.Positioned;

public class Button extends PlayNObject implements Positioned, Listener {
	
	private final static float UNPRESSED_ALPHA = 0.5f;
	
	private ImageLayer imageLayer;
	private OnPressedListener onPressedListener;
	private OnReleasedListener onReleaseListener;
	private float width, height;
	private boolean pressed;
	private boolean isCircle;

	public ImageLayer layer() {
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
	
	public void setIsCircle(boolean isCircle) {
		this.isCircle = isCircle;
	}
	
	public void setOnPressListener(OnPressedListener onPressedListener) {
		this.onPressedListener = onPressedListener;
	}
	
	public void setOnReleasedListener(OnReleasedListener onReleasedListener) {
		this.onReleaseListener = onReleasedListener;
	}
	
	public void setTint(int tint) {
		imageLayer.setTint(tint);
		imageLayer.setAlpha(pressed ? 1 : UNPRESSED_ALPHA);
	}
	
	public Button(String imagePath, float width, float height, boolean isCircle) {
		this(assets().getImage(imagePath), width, height, isCircle);
	}
	
	public Button(Image image, float width, float height, boolean isCircle) {
		this.width = width;
		this.height = height;
		this.isCircle = isCircle;
		imageLayer = graphics().createImageLayer();
		setImage(image);
		imageLayer.addListener(this);
		imageLayer.setAlpha(UNPRESSED_ALPHA);
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

	@Override
	public void onPointerStart(Event event) {
		if (!insideLocal(event)) return;
		imageLayer.setAlpha(1);
		pressed = true;
		if (onPressedListener != null) onPressedListener.onPress();
	}

	@Override
	public void onPointerEnd(Event event) {
		if (!pressed) return;
		imageLayer.setAlpha(UNPRESSED_ALPHA);
		pressed = false;
		if (onReleaseListener != null) onReleaseListener.onRelease(insideLocal(event));
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
	public void onPointerDrag(Event event) { }

	@Override
	public void onPointerCancel(Event event) { }
	
	public interface OnReleasedListener {
		public void onRelease(boolean inButton);
	}
	
	public interface OnPressedListener {
		public void onPress();
	}
}
