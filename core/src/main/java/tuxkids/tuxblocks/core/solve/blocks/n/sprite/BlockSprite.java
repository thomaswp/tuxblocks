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
import tuxkids.tuxblocks.core.layers.ImageLayerLike.Factory;
import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.layers.ImageLayerWrapper;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;
import tuxkids.tuxblocks.core.solve.blocks.n.Block;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public abstract class BlockSprite extends Sprite {

	protected NinepatchLayer layer;
	private Block block;
	
	protected static TextFormat textFormat;
	protected static Factory factory;
	
	public BlockSprite(Block block) {
		this.block = block;
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
		TextLayout layout = PlayN.graphics().layoutText(text, textFormat);
		int sides = 2;
		int width = sides * 4 + (int)layout.width();
		int height = sides * 4 + (int)layout.height();
		
		int[] widthDims = new int[] { sides, sides, width - sides * 4, sides, sides };
		int[] heightDims = new int[] { sides, sides, height - sides * 4, sides, sides };
		
		CanvasImage image = CanvasUtils.createRect(width, height, Color.withAlpha(color, 255), 1, Colors.DARK_GRAY);
		
		float textX = (image.width() - layout.width()) / 2;
		float textY = (image.height() - layout.height()) / 2;
		image.canvas().setFillColor(Colors.BLACK);
		image.canvas().fillText(layout, textX, textY);
		
		
		NinepatchLayer ninePatch = new NinepatchLayer(factory, image, widthDims, heightDims);
		return ninePatch;
	}
	
	@Override
	public String toString() {
		return block.text();
	}
}
