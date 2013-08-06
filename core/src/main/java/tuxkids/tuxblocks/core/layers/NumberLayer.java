package tuxkids.tuxblocks.core.layers;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.Layer.HitTester;
import playn.core.Pointer.Listener;
import playn.core.Surface;
import playn.core.TextFormat;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class NumberLayer extends LayerWrapper {

	protected GroupLayer layer;
	protected float width, height;
	protected NumberBitmapFont font;
	protected int number = Integer.MAX_VALUE;
	protected String text;
	protected ImmediateLayer renderLayer;

	public float width() {
		return width;
	}

	public float height() {
		return height;
	}
	
	public int number() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
		text = String.valueOf(number);
		width = 0; height = 0;
		for (int i = 0; i < text.length(); i++) {
			Image image = font.getImage(text.charAt(i));
			width += image.width();
			height = Math.max(height, image.height());
		}
	}
	
	public NumberLayer(NumberBitmapFont font) {
		super(graphics().createGroupLayer());
		this.layer = (GroupLayer) layerAddable();
		this.font = font;
		renderLayer = graphics().createImmediateLayer(new Renderer() {
			@Override
			public void render(Surface surface) {
				renderNumber(surface);
			}
		});
		layer.add(renderLayer);
	}

	private void renderNumber(Surface surface) {
		if (text == null) return;
		float x = 0;
		for (int i = 0; i < text.length(); i++) {
			Image image = font.getImage(text.charAt(i));
			surface.drawImage(image, x, 0);
			x += image.width();
		}
	}
	
	public static class NumberBitmapFont {

		protected final String characters = "-.0123456789";
		
		protected TextFormat textFormat;
		protected Image[] images;
		
		public NumberBitmapFont(TextFormat textFormat, int color) {
			this.textFormat = textFormat;
			images = new Image[characters.length()];
			for (int i = 0; i < images.length; i++) {
				images[i] = CanvasUtils.createText(characters.substring(i, i+1), textFormat, color);
			}
		}
		
		public Image getImage(char symbol) {
			int index = characters.indexOf(symbol);
			return index >= 0 ? images[index] : null;
		}
	}

	public void setHitTester(HitTester tester) {
		renderLayer.setHitTester(tester);
	}
	
	@Override
	public void addListener(Listener pointerListener) {
		renderLayer.addListener(pointerListener);
	}
}
