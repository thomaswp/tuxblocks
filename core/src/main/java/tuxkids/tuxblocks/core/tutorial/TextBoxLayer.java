package tuxkids.tuxblocks.core.tutorial;

import playn.core.Canvas;
import playn.core.Canvas.Composite;
import playn.core.Canvas.LineCap;
import playn.core.Canvas.LineJoin;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Path;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.util.Callback;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.layers.ImageLayerLike.Factory;
import tuxkids.tuxblocks.core.layers.ImageLayerWrapper;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

public class TextBoxLayer extends LayerWrapper {


	private final float spHeight = 31; //constant based on Java dimensions that created the image.. oops
	private final float spWidth = spHeight / 2;
	private final float hideHeight = graphics().height() * 0.1f;
	
	protected final GroupLayer layer, fadeInLayer;
	protected final TextFormat format;
	protected final ImageLayer textLayer, tuxLayer;
	protected final ImageLayerLike backgroundLayer;
	protected final float padding;
	protected final float width;
	
	protected float height;
	protected boolean hidden;
	
	public TextBoxLayer(float width) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		this.width = width;
		
		fadeInLayer = graphics().createGroupLayer();
		layer.add(fadeInLayer);
		
		padding = graphics().height() / 35;
		format = createFormat(graphics().height() / 20)
				.withWrapWidth(width - padding * 2 - spWidth);

		textLayer = graphics().createImageLayer();
		
		backgroundLayer = new NinepatchLayer(new Factory() {
			@Override
			public ImageLayerLike create(Image image) {
				return new ImageLayerWrapper(image);
			}
		}, assets().getImage(Constant.NINEPATCH_BUBBLE));
		backgroundLayer.setDepth(-1);
		backgroundLayer.addToLayer(fadeInLayer);
		
		tuxLayer = graphics().createImageLayer();
		tuxLayer.setImage(assets().getImage(Constant.IMAGE_TUX));
		layer.add(tuxLayer);
		tuxLayer.image().addCallback(new Callback<Image>() {
			@Override
			public void onSuccess(Image result) {
				tuxLayer.setScale(graphics().width() / 8 / result.width());
				tuxLayer.setOrigin(tuxLayer.width() * 0.8f, tuxLayer.height() * 0.2f);
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
		
		hidden = true;
		setVisible(false);
		fadeInLayer.setAlpha(0);
		tuxLayer.setTranslation(0, hideHeight);
	}
	
	public void show(String text) {
		
		if (text != null) {
			TextLayout layout = graphics().layoutText(text, format);
			height = layout.height() + padding * 2 + spHeight;
			
			CanvasImage textImage = graphics().createImage(layout.width(), layout.height());
			textImage.canvas().setFillColor(Colors.BLACK);
			textImage.canvas().fillText(layout, 0, 0);
			textLayer.setImage(textImage);
			textLayer.setTranslation(padding + spWidth, padding);
			fadeInLayer.add(textLayer);
			
			fadeInLayer.setOrigin(0, height);
			
			backgroundLayer.setSize(width, height);
		}
		
		setVisible(true);
		hidden = false;
	}
	
	public void hide() {
		hidden = true;
	}
	
	public void paint(Clock clock) {
		float speed = 0.5f;
		if (!hidden) {
			if (tuxLayer.ty() > 0) {
				tuxLayer.setTy(Math.max(tuxLayer.ty() - speed * clock.dt(), 0));
			} else if (fadeInLayer.alpha() < 1) {
				lerpAlpha(fadeInLayer, 1, 0.995f, clock.dt());
			}
		} else {
			if (fadeInLayer.alpha() > 0) {
				lerpAlpha(fadeInLayer, 0, 0.99f, clock.dt());
				if (fadeInLayer.alpha() == 0) {
					Tutorial.trigger(Trigger.TextBoxHidden);
				}
			} else if (tuxLayer.ty() < hideHeight) {
				tuxLayer.setTy(Math.min(tuxLayer.ty() + 0.5f * clock.dt(), hideHeight));
			} else {
				setVisible(false);
			}
		}
	}

	@SuppressWarnings("unused")
	private Image createSpeechBubble(float width, float height, float rad, 
			float strokeWidth, float spWidth, float spHeight) {
		width = Math.round(width); height = Math.round(height);
		CanvasImage image = PlayN.graphics().createImage(width, height);
		Canvas canvas = image.canvas();
		
		canvas.setFillColor(Color.withAlpha(Colors.WHITE, 235));
		canvas.setStrokeColor(Colors.GRAY);
		canvas.setStrokeWidth(strokeWidth);
		canvas.setLineCap(LineCap.ROUND);
		canvas.setLineJoin(LineJoin.ROUND);
		
		canvas.save();
		canvas.translate(spWidth, 0);
		
		float rectHeight = height - spHeight;
		float rectWidth = width - spWidth;
		canvas.fillRoundRect(strokeWidth / 2, strokeWidth / 2, 
				rectWidth - strokeWidth, rectHeight - strokeWidth, rad);
		int indent = Math.round(strokeWidth / 2); 
		canvas.strokeRoundRect(indent, indent, 
				rectWidth - indent * 2 - 1, rectHeight - indent * 2 - 1, rad);
		
		canvas.restore();
		
		Path path = canvas.createPath();
		path.moveTo(indent + spWidth, rectHeight - rad * 3);
		path.lineTo(indent + spWidth, rectHeight - rad * 2);
		path.lineTo(indent, height - strokeWidth / 2);
		path.lineTo(indent + spWidth + rad * 2, rectHeight - indent - 1);
		path.lineTo(indent + spWidth + rad * 3, rectHeight - indent - 1);
		
		// the clipping here is to support HTML5, where Composite.SRC seems not to work correctly
		// still not perfect on Firefox
		canvas.save();
		canvas.setCompositeOperation(Composite.SRC);
		canvas.clip(path);
		canvas.fillRect(0, rectHeight - rad * 4, indent + spWidth + rad * 4, height - (rectHeight - rad * 4));
		canvas.restore();
		canvas.strokePath(path);
		
		return image;
	}
}
