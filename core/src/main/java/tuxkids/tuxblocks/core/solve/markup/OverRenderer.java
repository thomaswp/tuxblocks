package tuxkids.tuxblocks.core.solve.markup;

import playn.core.Canvas;
import playn.core.TextFormat;
import playn.core.Canvas.LineCap;
import pythagoras.f.Vector;

public class OverRenderer extends ModifierRenderer {

	public OverRenderer(Renderer base, int[] operands) {
		super(base, new FactorGroupRenderer(operands));
	}
	
	public OverRenderer(Renderer base, int[] operands, boolean[] highlights) {
		super(base, new FactorGroupRenderer(operands, highlights));
	}
	
	public OverRenderer(Renderer base, Renderer factor) {
		super(base, factor);
	}
	
	@Override
	public int lines() {
		return base.lines() + modifier.lines();
	}

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter childWriter = base.getExpressionWriter(textFormat);
		final ExpressionWriter factorWriter = modifier.getExpressionWriter(textFormat);
		
		return new ParentExpressionWriter(textFormat) {
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				return new Vector(Math.max(childWriter.width(), factorWriter.width()), 
						childWriter.height() + factorWriter.height() + spacing() * 2);
			}

			@Override
			protected void addChildren() {
				addChild(childWriter, (width() - childWriter.width()) / 2, 0);
				addChild(factorWriter, (width() - factorWriter.width()) / 2, childWriter.height() + 2 * spacing());
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				super.drawExpression(canvas);
				
				setColor(canvas, modifier.fullyHighlighted());
				canvas.setStrokeWidth(textFormat.font.size() / 10);
				canvas.setLineCap(LineCap.ROUND);
				float y = childWriter.height() + spacing();
				canvas.drawLine(0, y, width(), y);
			}
		};
	}
}
