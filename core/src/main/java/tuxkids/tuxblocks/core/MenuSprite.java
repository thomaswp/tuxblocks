package tuxkids.tuxblocks.core;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class MenuSprite extends LayerWrapper {
	
	public static float DEFAULT_ALPHA = 0.75f;
	
	protected final GroupLayer layer;
	protected final float width, height;
	protected final GameState state;
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

	public Button addLeftButton(String path) {
		return addLeftButton(assets().getImage(path));
	}
	
	public Button addLeftButton(Image image) {
		leftButton = addButton(image);
		leftButton.setPosition(leftButton.width() * 0.6f, height / 2);
		return leftButton;
	}

	public Button addRightButton(String path) {
		return addRightButton(assets().getImage(path));
	}
	
	public Button addRightButton(Image image) {
		rightButton = addButton(image);
		rightButton.setPosition(width - rightButton.width() * 0.6f, height / 2);
		return rightButton;
	}
	
	private Button addButton(Image image) {
		Button button = new Button(image, defaultButtonSize(), defaultButtonSize(), true);
		layer.add(button.layerAddable());
		button.setTint(state.themeColor());
		button.layerAddable().setDepth(1);
		return button;
	}
	
	public MenuSprite(GameState state, float width) {
		super(graphics().createGroupLayer());
		this.width = width;
		this.height = defaultButtonSize() * 1.2f;
		this.state = state;
		layer = (GroupLayer) layerAddable();
		background = graphics().createImageLayer(
				CanvasUtils.createRect(width, height, Colors.LIGHT_GRAY, 1, Colors.DARK_GRAY));
		background.setAlpha(DEFAULT_ALPHA);
		background.setDepth(-1);
		layer.add(background);
	}
	
	public void update(int delta) {
		
	}
	
	public void paint(Clock clock) {
		
	}
}
