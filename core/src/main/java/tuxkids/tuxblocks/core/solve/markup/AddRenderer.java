package tuxkids.tuxblocks.core.solve.markup;

import playn.core.TextFormat;
import pythagoras.f.Vector;

/**
 * A {@link ModifierRenderer} used specifically for added or subtracted
 * terms. Renders the a base Renderer with the given addends added or subtracted
 * from it.
 */
public class AddRenderer extends ModifierRenderer {

	public AddRenderer(Renderer base, int[] operands) {
		super(base, new AddGroupRenderer(operands));
	}
	
	public AddRenderer(Renderer base, int[] operands, boolean[] highlights) {
		super(base, new AddGroupRenderer(operands, highlights));
	}
	
	public AddRenderer(Renderer base, Renderer factor) {
		super(base, factor);
	}
	
	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter childWriter = base.getExpressionWriter(textFormat);
		final ExpressionWriter addWriter = modifier.getExpressionWriter(textFormat);
		
		return new ParentExpressionWriter(textFormat) {
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				return new Vector(childWriter.width() + addWriter.width(), 
						Math.max(childWriter.height(), addWriter.height()));
			}

			@Override
			protected void addChildren() {
				addChild(childWriter, 0, 0);
				addChild(addWriter, childWriter.width(), (height() - addWriter.height()) / 2);
			}
		};
	}

}
