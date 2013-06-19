package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.solve.expression.Expression;
import tuxkids.tuxblocks.core.solve.expression.ModificationOperation;
import tuxkids.tuxblocks.core.solve.expression.Number;
import tuxkids.tuxblocks.core.solve.expression.Variable;

public abstract class Block extends PlayNObject{
	
	public final static int BASE_SIZE = 150;
	public final static int MOD_SIZE = BASE_SIZE / 3;
	
	private static TextFormat textFormat;
	
	protected ImageLayer sprite;

	public float width() {
		return sprite.width();
	}
	
	public float height() {
		return sprite.height();
	}
	
	public Layer getSprite() {
		return sprite;
	}
	
	public abstract int getColor();
	
	public static BaseBlock createBlock(Expression exp) {
		if (exp instanceof ModificationOperation) {
			ModificationOperation modOp = (ModificationOperation) exp;
			BaseBlock base = createBlock(modOp.getOperand());
			base.addModifier(modOp);
			return base;
		} else if (exp instanceof Number) {
			return new NumberBlock(((Number) exp));
		} else if (exp instanceof Variable) {
			return new VariableBlock(((Variable) exp));
		}
		return null;
	}
	
	protected ImageLayer generateSprite(int width, int height, String text, int color) {
		if (textFormat == null) {
			Font font = PlayN.graphics().createFont(Constant.FONT_NAME, Font.Style.PLAIN, 20);
			textFormat = new TextFormat().withFont(font);
		}
		
		CanvasImage image = PlayN.graphics().createImage(width, height);
		//image.canvas().setAlpha(0.5f);
		image.canvas().setFillColor(color);
		image.canvas().fillRect(0, 0, width, height);
		image.canvas().setStrokeColor(Color.rgb(0, 0, 0));
		image.canvas().strokeRect(0, 0, width - 1, height - 1);
		image.canvas().setFillColor(Color.rgb(0, 0, 0));
		
		TextLayout layout = PlayN.graphics().layoutText(text, textFormat);
		float textX = (image.width() - layout.width()) / 2;
		float textY = (image.height() - layout.height()) / 2;
		image.canvas().fillText(layout, textX, textY);
		return PlayN.graphics().createImageLayer(image);
	}
}
