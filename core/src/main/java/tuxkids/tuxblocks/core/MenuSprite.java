package tuxkids.tuxblocks.core;

import playn.core.Font.Style;
import playn.core.util.Clock;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import tripleplay.util.Colors;
import playn.core.Layer;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class MenuSprite extends PlayNObject {
	
	public static float DEFAULT_ALPHA = 0.75f;
	
	protected GroupLayer layer;
	private ImageLayer background;
	private float width, height;
	protected GameState state;
	private Button leftButton, rightButton;
	
	public static float defaultButtonSize() {
		return graphics().height() * 0.15f;
	}
	
	public Layer layerAddable() {
		return layer;
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
	
//	public String text() {
//		return text;
//	}
//	
//	public int textColor() {
//		return textColor;
//	}
//	
//	public void setTextColor(int textColor) {
//		this.textColor = textColor;
//	}
//	
//	public void setText(String text) {
//		if (text == null) {
//			textLayer.setImage(null);
//		} else if (!text.equals(this.text)) {
//			textLayer.setImage(CanvasUtils.createString(textFormat, text, textColor));
//			centerImageLayer(textLayer);
//		}
//		this.text = text;
//	}
	
	public MenuSprite(GameState state, float width) {
		this.width = width;
		this.height = defaultButtonSize() * 1.2f;
		this.state = state;
		layer = graphics().createGroupLayer();
		background = graphics().createImageLayer(
				CanvasUtils.createRect(width, height, Colors.LIGHT_GRAY, 1, Colors.DARK_GRAY));
		background.setAlpha(DEFAULT_ALPHA);
		background.setDepth(-1);
		layer.add(background);
		
//		textFormat = new TextFormat().withFont(graphics().createFont(Constant.FONT_NAME, Style.PLAIN, height / 2f));
//		textLayer = graphics().createImageLayer();
//		textLayer.setTranslation(width / 2, height / 2);
//		layer.add(textLayer);
	}
	
	public void update(int delta) {
		
	}
	
	public void paint(Clock clock) {
		
	}
}
