package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Canvas.LineCap;
import pythagoras.f.Vector;

public class OverGroupRenderer extends FactorGroupRenderer {

	public OverGroupRenderer(Renderer base, int[] operands) {
		super(base, operands);
	}
	
	public OverGroupRenderer(Renderer base, int[] operands, boolean[] highlights) {
		super(base, operands, highlights);
	}

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter childWriter = base.getExpressionWriter(textFormat);
		final ExpressionWriter factorWriter = getFactorWriter(textFormat);
		
		return new ExpressionWriter(textFormat) {
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				return new Vector(Math.max(childWriter.width(), factorWriter.width()), 
						childWriter.height() + factorWriter.height() + SPACING * 2);
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				canvas.save();
				canvas.translate((width() - childWriter.width()) / 2, 0);
				childWriter.drawExpression(canvas);
				canvas.restore();
				
				setColor(canvas, highlights.length == 1 && highlights[0]);
				canvas.setStrokeWidth(textFormat.font.size() / 10);
				canvas.setLineCap(LineCap.ROUND);
				float y = childWriter.height() + SPACING;
				canvas.drawLine(0, y, width(), y);
				
				canvas.save();
				canvas.translate((width() - factorWriter.width()) / 2, y + SPACING);
				factorWriter.drawExpression(canvas);
				canvas.restore();
			}
		};
	}

	@Override
	protected boolean useNegatives() {
		return false;
	}

}
