package tuxkids.tuxblocks.core.widget;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

/** 
 * A Layer to place at the top of the screen with information and buttons.
 */
public class HeaderLayer extends LayerWrapper {
	
	public static float DEFAULT_ALPHA = 0.75f;
	
	protected final GroupLayer layer;
	protected final float width, height;
	protected final int themeColor;
	
	private final ImageLayer background;
	private Button leftButton, rightButton;
	
	public static float defaultButtonSize() {
		return graphics().height() * 0.15f;
	}
	
	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	public Button leftButton() {
		return leftButton;
	}
	
	public Button rightButton() {
		return rightButton;
	}

	/** Creates a menu button and sets it in the left corner */
	public Button addLeftButton(String path) {
		return addLeftButton(assets().getImage(path));
	}
	
	/** Creates a menu button and sets it in the left corner */
	public Button addLeftButton(Image image) {
		leftButton = addButton(image);
		leftButton.setPosition(leftButton.width() * 0.6f, height / 2);
		return leftButton;
	}

	/** Creates a menu button and sets it in the right corner */
	public Button addRightButton(String path) {
		return addRightButton(assets().getImage(path));
	}
	
	/** Creates a menu button and sets it in the right corner */
	public Button addRightButton(Image image) {
		rightButton = addButton(image);
		rightButton.setPosition(width - rightButton.width() * 0.6f, height / 2);
		return rightButton;
	}
	
	/** Creates a menu button */
	public Button createButton(String path) {
		return createButton(assets().getImage(path));
	}
	
	private Button addButton(Image image) {
		Button button = createButton(image);
		layer.add(button.layerAddable());
		button.layerAddable().setDepth(1);
		return button;
	}
	
	public Button createButton(Image image) {
		Button button = new Button(image, defaultButtonSize(), defaultButtonSize(), true);
		button.setTint(themeColor);
		return button;
	}
	
	public HeaderLayer(float width, int themeColor) {
		super(graphics().createGroupLayer());
		this.width = width;
		this.themeColor = themeColor;
		this.height = defaultButtonSize() * 1.2f;
		layer = (GroupLayer) layerAddable();
		background = graphics().createImageLayer(
				CanvasUtils.createRectCached(width, height, Colors.LIGHT_GRAY, 1, Colors.DARK_GRAY));
		background.setAlpha(DEFAULT_ALPHA);
		background.setDepth(-1);
		layer.add(background);
	}
	
	public void update(int delta) {
		
	}
	
	public void paint(Clock clock) {
		
	}
}
