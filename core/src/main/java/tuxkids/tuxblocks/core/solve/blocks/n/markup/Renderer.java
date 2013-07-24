package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.TextFormat;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;

public abstract class Renderer extends PlayNObject {
	
	public final static int DEFAULT_COLOR = Colors.WHITE;
	public final static int HIGHLIGHT_COLOR = Colors.RED;
	
	public abstract ExpressionWriter getExpressionWriter(TextFormat textFormat);
	
	protected void setColor(Canvas canvas, boolean highlight) {
		int color = highlight ? HIGHLIGHT_COLOR : DEFAULT_COLOR;
		canvas.setFillColor(color);
		canvas.setStrokeColor(color);
	}
}
