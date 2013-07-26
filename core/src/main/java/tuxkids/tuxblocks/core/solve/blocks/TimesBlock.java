package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Path;
import playn.core.PlayN;
import playn.core.TextLayout;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;

public class TimesBlock extends VerticalModifierBlock{

	public TimesBlock(int value) {
		super(value);
	}
	
	protected TimesBlock(OverBlock inverse) {
		super(inverse);
	}
	
	protected TimesBlock(TimesBlock inverse) {
		super(inverse);
	}

	@Override
	protected String operator() {
		return Constant.TIMES_SYMBOL;
	}
	
	@Override
	public int color() {
		if (value == -1) {
			return Color.rgb(150, 150, 150);
		} else {
//			return getColor(30);
			return Color.rgb(0xF7, 0x9D, 0x04);
		}
	}
	
	@Override
	public String text() {
		if (value == -1) {
			return "-";
		} else {
			return super.text();
		}
	}
	
	@Override
	public boolean canSimplify() {
		if (value == -1) {
			if (group == null) return false;
			return group.children.lastIndexOf(inverse) != group.children.indexOf(inverse);
		}
		return super.canSimplify();
	}

	@Override
	protected NinepatchLayer generateNinepatch(String text) {
		boolean times = true;
		
		TextLayout layout = PlayN.graphics().layoutText(text, textFormat);
		int legs = wrapSize();
		int sides = 3;
		int width = legs * 2 + sides * 2 + (int)layout.width() + 2;
		int height = modSize() + sides * 2;
		
		int[] widthDims = new int[] { legs + 2, sides - 2, width - legs * 2 - sides * 2, sides - 2, legs + 2 };
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
		for (int i = 1; i < 3; i++) {
			for (int j = 1; j < 4; j++) {
				ninePatch.setTouchEnabled(i, j, false);
			}
		}
		return ninePatch;
	}

	@Override
	public ModifierBlock copyChild() {
		return new TimesBlock(value);
	}

	@Override
	protected ModifierBlock inverseChild() {
		if (value == -1) return new TimesBlock(this);
		return new OverBlock(this);
	}
}
