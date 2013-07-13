package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public abstract class BlockSprite extends Sprite {

	protected NinepatchLayer layer;
	private static TextFormat textFormat;
	
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

	protected NinepatchLayer generateNinepatch(String text, int color) {
		if (textFormat == null) {
			Font font = PlayN.graphics().createFont(Constant.FONT_NAME, Font.Style.PLAIN, 20);
			textFormat = new TextFormat().withFont(font);
		}
		TextLayout layout = PlayN.graphics().layoutText(text, textFormat);
		int sides = 5;
		int width = sides * 4 + (int)layout.width() + 2;
		int height = sides * 4 + (int)layout.height() + 2;
		
		int[] widthDims = new int[] { sides, sides, width - sides * 4, sides, sides };
		int[] heightDims = new int[] { sides, sides, height - sides * 4, sides, sides };
		
		CanvasImage image = CanvasUtils.createRect(width, height, Color.withAlpha(color, 255), 1, Colors.RED);
		
		float textX = (image.width() - layout.width()) / 2;
		float textY = (image.height() - layout.height()) / 2;
		image.canvas().setFillColor(Colors.BLACK);
		image.canvas().fillText(layout, textX, textY);
		
		NinepatchLayer ninePatch = new NinepatchLayer(image, widthDims, heightDims);
		return ninePatch;
	}
}
