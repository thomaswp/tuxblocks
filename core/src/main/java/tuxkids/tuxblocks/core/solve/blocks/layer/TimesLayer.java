package tuxkids.tuxblocks.core.solve.blocks.layer;

import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer.HitTester;
import playn.core.Path;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;
import tuxkids.tuxblocks.core.solve.blocks.Sprite;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class TimesLayer extends LayerWrapper implements BlockLayer {

	protected final GroupLayer layer;
	protected final NinepatchLayer ninepatch;
	protected final ImageLayer textLayer;
	protected final TextFormat format;
	
	protected static Image ninepatchImage;
	protected static Factory factory;
	
	protected String text;
	
	public String text() {
		return text;
	}
	
	@Override
	public void setText(String text) {
		this.text = text;
		refreshTextLayer();
	}

	public TimesLayer(TextFormat format, String text) {
		super(graphics().createGroupLayer());
		this.format = format;
		layer = (GroupLayer) layerAddable();
		
		ninepatch = createNinepatch();
		layer.add(ninepatch.layerAddable());
		
		textLayer = graphics().createImageLayer();
		textLayer.setTy(Sprite.modSize() / 2);
		layer.add(textLayer);
		setText(text);
	}
	
	private void refreshTextLayer() {
		textLayer.setImage(CanvasUtils.createText(text, format, Colors.BLACK));
		centerImageLayer(textLayer);
	}

	private NinepatchLayer createNinepatch() {
		int legs = Sprite.wrapSize();
		int sides = 3;
		int middle = 5;
		int width = legs * 2 + middle;
		int textHeight = Sprite.modSize();
		int height = textHeight + sides * 2;
		
		//adjust the dims a bit to make sure none of the path overflows
		int[] widthDims = new int[] { legs + 1, middle - 3, legs + 2 };
		int[] heightDims = new int[] { textHeight + 1, sides - 1, sides };
		
		if (ninepatchImage == null) {
			float hlw = 0;//0.5f;
			float pWidth = width - 1;
			float pHeight = height - 1;
			float lx, ly;
		
			CanvasImage image = graphics().createImage(width, height);
			
			Path path = image.canvas().createPath();
			path.moveTo(lx = hlw, ly = hlw);
			path.lineTo(lx = pWidth - hlw, ly);
			path.lineTo(lx, ly = pHeight - hlw);
			path.lineTo(lx = pWidth - legs + hlw + 1, ly);
			path.lineTo(lx, ly = pHeight - sides * 2 + hlw);
			path.lineTo(lx = legs - hlw - 1, ly);
			path.lineTo(lx, ly = pHeight - hlw);
			path.lineTo(lx = hlw, ly);
			path.lineTo(lx, hlw);
			image.canvas().setFillColor(Colors.WHITE);
			image.canvas().setStrokeColor(Colors.DARK_GRAY);
			image.canvas().fillPath(path);
			image.canvas().strokePath(path);
			
			ninepatchImage = image;
		}
		if (factory == null) {
			factory = new Factory() {
				@Override
				public ImageLayerLike create(Image image) {
					return new ImageLayerTintable(image);
				}
			}; 
		}
		
		NinepatchLayer ninepatch = new NinepatchLayer(factory, ninepatchImage, widthDims, heightDims);
		for (int i = 1; i < 3; i++) {
			for (int j = 1; j < 2; j++) {
				ninepatch.setTouchEnabled(i, j, false);
			}
		}
		return ninepatch;
	}
	
	@Override
	public void addListener(Listener listener) {
		ninepatch.addListener(listener);;
	}

	@Override
	public float width() {
		return ninepatch.width();
	}

	@Override
	public float height() {
		return ninepatch.height();
	}

	@Override
	public Image image() {
		return ninepatch.image();
	}

	@Override
	public void setSize(float width, float height) {
		ninepatch.setSize(width, height);
		textLayer.setTx(width / 2);
	}

	@Override
	public void setWidth(float width) {
		setSize(width, height());
	}

	@Override
	public void setHeight(float height) {
		ninepatch.setHeight(height);
	}

	@Override
	public void setHitTester(HitTester hitTester) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTint(int tint) {
		ninepatch.setTint(tint);
	}
	
	@Override
	public void setTint(int baseColor, int tintColor, float perc) {
		ninepatch.setTint(baseColor, tintColor, perc);
	}
}
