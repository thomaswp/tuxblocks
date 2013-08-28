package tuxkids.tuxblocks.core.solve.markup;

import java.util.ArrayList;
import java.util.List;

import playn.core.Canvas;
import playn.core.TextFormat;
import pythagoras.f.Vector;

/**
 * Writes a group of other {@link ExpressionWriter}s in specific positions.
 */
abstract class ParentExpressionWriter extends ExpressionWriter {

	private final List<ExpressionWriter> children = new ArrayList<ExpressionWriter>();
	private final List<Vector> childOffsets = new ArrayList<Vector>();
	
	protected abstract void addChildren();
	
	public ParentExpressionWriter(TextFormat textFormat) {
		super(textFormat);
		addChildren();
	}
	
	/** Adds a child ExpressionWriter to be rendered at the given position */
	protected void addChild(ExpressionWriter child, float x, float y) {
		addChild(child, new Vector(x, y));
	}
	
	/** See {@link ParentExpressionWriter#addChild(ExpressionWriter, float, float)} */
	protected void addChild(ExpressionWriter child, Vector offset) {
		children.add(child);
		childOffsets.add(offset);
	}

	@Override
	public void drawExpression(Canvas canvas) {
		// draw child expressions
		for (int i = 0; i < children.size(); i++) {
			ExpressionWriter child = children.get(i);
			Vector offset = childOffsets.get(i);
			
			canvas.save();
			canvas.translate(offset.x, offset.y);
			child.drawExpression(canvas, config);
			canvas.restore();
		}
	}
	
	@Override
	public Vector blankCenter() {
		// find a blank within the children, if any
		// and return its blankCenter, offset by its position
		for (int i = 0; i < children.size(); i++) {
			ExpressionWriter child = children.get(i);
			
			Vector center = child.blankCenter();
			if (center != null) {
				Vector offset = childOffsets.get(i);
				center.x += offset.x;
				center.y += offset.y;
				return center;
			}
		}
		return null;
	}
}
