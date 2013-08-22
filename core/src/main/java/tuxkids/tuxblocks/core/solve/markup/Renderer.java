package tuxkids.tuxblocks.core.solve.markup;

import playn.core.TextFormat;
import tuxkids.tuxblocks.core.utils.PlayNObject;

public abstract class Renderer extends PlayNObject {
	
	public boolean fullyHighlighted() {
		return false;
	}
	
	public int lines() {
		return 1;
	}
	
	public abstract ExpressionWriter getExpressionWriter(TextFormat textFormat);
}
