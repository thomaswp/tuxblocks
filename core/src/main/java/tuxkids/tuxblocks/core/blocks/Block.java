package tuxkids.tuxblocks.core.blocks;

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
import tuxkids.tuxblocks.core.eqn.BinaryOperation;
import tuxkids.tuxblocks.core.eqn.Expression;
import tuxkids.tuxblocks.core.eqn.ModificationOperation;
import tuxkids.tuxblocks.core.eqn.Number;
import tuxkids.tuxblocks.core.eqn.Variable;

public abstract class Block {
	
	public final static int NUM_SIZE = 75;
	public final static float RECT_RATIO = 0.4f;
	
	private static TextFormat textFormat;
	
	protected Layer sprite;

	public Layer getSprite() {
		return sprite;
	}
	
	public static Block createBlock(Expression exp) {
		if (exp instanceof ModificationOperation) {
			ModificationOperation modOp = (ModificationOperation) exp;
			Block base = createBlock(modOp.getOperand());
			return new ModifierBlock(base, modOp);
		} else if (exp instanceof Number) {
			return new NumberBlock(((Number) exp).getValue());
		} else if (exp instanceof Variable) {
			return new VariableBlock(((Variable) exp).getName());
		}
		return null;
	}
	
	protected ImageLayer generateSprite(int width, int height) {
		if (textFormat == null) {
			Font font = PlayN.graphics().createFont("Arial", Font.Style.PLAIN, 20);
			textFormat = new TextFormat().withFont(font);
		}
		
		CanvasImage image = PlayN.graphics().createImage(width, height);
		image.canvas().setFillColor(getColor());
		image.canvas().fillRect(0, 0, width, height);
		image.canvas().setStrokeColor(Color.rgb(0, 0, 0));
		image.canvas().strokeRect(0, 0, width, height);
		image.canvas().setFillColor(Color.rgb(0, 0, 0));
		
		TextLayout layout = PlayN.graphics().layoutText(getText(), textFormat);
		float textX = (image.width() - layout.width()) / 2;
		float textY = (image.height() - layout.height()) / 2;
		image.canvas().fillText(layout, textX, textY);
		return PlayN.graphics().createImageLayer(image);
	}
	
	public abstract float getWidth();
	public abstract float getHeight();
	public abstract int getColor();
	public abstract String getText();
}
