package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.CanvasImage;
import playn.core.Path;
import playn.core.PlayN;
import playn.core.TextLayout;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;

public class TimesBlockSprite extends VerticalModifierSprite{

	public TimesBlockSprite(int value) {
		super(value);
	}

	@Override
	protected String operator() {
		return "\u00D7";
	}

	protected NinepatchLayer generateNinepatch(String text, int color) {
		boolean times = true;
		
		TextLayout layout = PlayN.graphics().layoutText(text, textFormat);
		int legs = wrapSize();
		int sides = 2;
		int width = legs * 2 + sides * 2 + (int)layout.width();
		int height = modSize() + sides * 2;
		
		int[] widthDims = new int[] { legs + 1, sides - 1, width - legs * 2 - sides * 2, sides - 1, legs + 1 };
		int[] heightDims;
		if (times) {
			heightDims = new int[] { modSize() + 1, sides - 1, sides };
		} else {
			heightDims = new int[] { sides, sides - 1, modSize() + 1 };
		}
		
		float hlw = 0;//0.5f;
		float pWidth = width - 1;
		float pHeight = height - 1;
		float lx, ly;
		
		CanvasImage image = graphics().createImage(width, height);
		if (!times) {
			image.canvas().save();
			image.canvas().translate(0, image.height() / 2);
			image.canvas().scale(1, -1);
			image.canvas().translate(0, -image.height() / 2 + 1);
		}
		
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
		
		float textX = (image.width() - layout.width()) / 2;
		float textY;
		if (times) {
			textY = (modSize() - layout.height()) / 2;
		} else {
			image.canvas().restore();
			textY = image.height() - (modSize() + layout.height()) / 2;
		}
		image.canvas().setFillColor(Colors.BLACK);
		image.canvas().fillText(layout, textX, textY);
		
		NinepatchLayer ninePatch = new NinepatchLayer(factory, image, widthDims, heightDims);
		return ninePatch;
	}
}
