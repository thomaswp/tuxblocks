package tuxkids.tuxblocks.core.tutorial;

import javax.swing.LayoutStyle.ComponentPlacement;

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
import playn.core.Pointer.Listener;
import playn.core.util.Callback;
import playn.core.util.Clock;
import playn.core.TextFormat;
import playn.core.TextLayout;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class TextBoxLayer extends LayerWrapper {

	protected final GroupLayer layer, fadeInLayer;
	protected final TextFormat format;
	protected final ImageLayer textLayer, backgroundLayer, tuxLayer;
	protected final float width, height;
	protected final float padding;
	
	public TextBoxLayer(String text, float width) {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		fadeInLayer = graphics().createGroupLayer();
		fadeInLayer.setAlpha(0);
		layer.add(fadeInLayer);
		
		padding = graphics().height() / 35;

		float spHeight = graphics().height() / 20f;
		float spWidth = spHeight / 2;
		
		format = createFormat(graphics().height() / 20)
				.withWrapWidth(width - padding * 2 - spWidth);
		TextLayout layout = graphics().layoutText(text, format);

		this.width = width;
		this.height = layout.height() + padding * 2 + spHeight;
		
		textLayer = graphics().createImageLayer();
		CanvasImage textImage = graphics().createImage(layout.width(), layout.height());
		textImage.canvas().setFillColor(Colors.BLACK);
		textImage.canvas().fillText(layout, 0, 0);
		textLayer.setImage(textImage);
		textLayer.setTranslation(padding + spWidth, padding);
		fadeInLayer.add(textLayer);
		
		backgroundLayer = graphics().createImageLayer();
		backgroundLayer.setImage(createSpeechBubble(width, height, padding, 
				padding / 2, spWidth, spHeight));
		backgroundLayer.setDepth(-1);
		fadeInLayer.add(backgroundLayer);
		
		tuxLayer = graphics().createImageLayer();
		tuxLayer.setImage(assets().getImage(Constant.IMAGE_TUX));
		tuxLayer.setTranslation(0, height * 1.6f);
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
	}
	
	public void show() {
		
	}
	
	public void paint(Clock clock) {
		if (tuxLayer.ty() > height) {
			tuxLayer.setTy(Math.max(tuxLayer.ty() - 0.5f * clock.dt(), height));
		} else if (fadeInLayer.alpha() < 1) {
			lerpAlpha(fadeInLayer, 1, 0.995f, clock.dt());
		}
	}

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
		path.moveTo(indent + spWidth, rectHeight - rad * 2);
		path.lineTo(indent, height - strokeWidth / 2);
		path.lineTo(indent + spWidth + rad * 2, rectHeight - indent - 1);
		path.lineTo(indent + spWidth + rad * 3, rectHeight - indent - 1);
		
		canvas.setCompositeOperation(Composite.SRC);
		canvas.fillPath(path);
		canvas.strokePath(path);
		
		return image;
	}
}
