package tuxkids.tuxblocks.core.solve.expression;

import playn.core.Canvas;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

public abstract class ExpressionWriter {
	
	public final static float SPACING = 5;
	
	protected Vector size;
	protected TextFormat textFormat;
	
	public Vector getSize() {
		return size;
	}
	
	public float width() {
		return size.x;
	}
	
	public float height() {
		return size.y;
	}
	
	public ExpressionWriter(TextFormat textFormat) {
		this.textFormat = textFormat;
		this.size = formatExpression(textFormat);
	}
	
	protected abstract Vector formatExpression(TextFormat textFormat);
	public abstract void drawExpression(Canvas canvas, int childColor);
	
	protected TextLayout layout(String text, TextFormat textFormat) {
		return PlayN.graphics().layoutText(text, textFormat);
	}
}
