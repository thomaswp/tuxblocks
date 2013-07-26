package tuxkids.tuxblocks.core;

import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import tripleplay.util.Colors;
import playn.core.Layer;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class MenuSprite extends PlayNObject {
	
	public static float DEFAULT_ALPHA = 0.75f;
	
	private GroupLayer layer;
	private ImageLayer background;
	private ImageLayer textLayer;
	private String text;
	private float width, height;
	private TextFormat textFormat;
	private int textColor = Colors.BLACK;
	
	public Layer layer() {
		return layer;
	}
	
	public float width() {
		return width;
	}
	
	public float height() {
		return height;
	}
	
	public String text() {
		return text;
	}
	
	public int textColor() {
		return textColor;
	}
	
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	
	public void setText(String text) {
		if (text == null) {
			textLayer.setImage(null);
		} else if (!text.equals(this.text)) {
			textLayer.setImage(CanvasUtils.createString(textFormat, text, textColor));
			centerImageLayer(textLayer);
		}
		this.text = text;
	}
	
	public MenuSprite(float width, float height) {
		this.width = width;
		this.height = height;
		layer = graphics().createGroupLayer();
		background = graphics().createImageLayer(
				CanvasUtils.createRect(width, height, Colors.LIGHT_GRAY, 1, Colors.DARK_GRAY));
		background.setAlpha(DEFAULT_ALPHA);
		background.setDepth(-1);
		layer.add(background);
		
		textFormat = new TextFormat().withFont(graphics().createFont(Constant.FONT_NAME, Style.PLAIN, height / 2f));
		textLayer = graphics().createImageLayer();
		textLayer.setTranslation(width / 2, height / 2);
		layer.add(textLayer);
	}
}
