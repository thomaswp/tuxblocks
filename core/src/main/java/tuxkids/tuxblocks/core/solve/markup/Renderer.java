package tuxkids.tuxblocks.core.solve.markup;

import playn.core.CanvasImage;
import playn.core.TextFormat;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.utils.PlayNObject;

/**
 * Holds information necessary for rendering part or all of an
 * {@link Equation}. Can produce an {@link ExpressionWriter} for
 * a given {@link TextFormat} to actually draw the expression
 * to a {@link CanvasImage}. Renderers are often recursive, in 
 * that one will take another as a parameter and draw it as part
 * of its own rendering. Parts of this Renderer may be drawn in a
 * highlight color, in which case they are considered highlighted.
 */
public abstract class Renderer extends PlayNObject {
	
	/** Returns true of this Renderer is fully highlighted. */
	protected boolean fullyHighlighted() {
		return false;
	}
	
	/** 
	 * Returns the number of (vertical) lines in the expression
	 * to be rendered. This can be useful for adjusting the size
	 * of the text used when calling {@link Renderer#getExpressionWriter(TextFormat)}. 
	 */
	public int lines() {
		return 1;
	}
	
	/**
	 * Returns an {@link ExpressionWriter} capable of rendering this
	 * renderer's expression using the given TextFormat.
	 */
	public abstract ExpressionWriter getExpressionWriter(TextFormat textFormat);
}
