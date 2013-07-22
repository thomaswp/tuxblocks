package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.TextFormat;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;

public abstract class Renderer extends PlayNObject {
	
	public final static int DEFAULT_COLOR = Colors.BLACK;
	
	public abstract ExpressionWriter getExpressionWriter(TextFormat textFormat);
}
