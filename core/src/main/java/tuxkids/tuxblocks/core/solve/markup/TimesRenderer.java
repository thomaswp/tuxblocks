package tuxkids.tuxblocks.core.solve.markup;

import playn.core.Canvas;
import playn.core.Path;
import playn.core.TextFormat;
import playn.core.Canvas.LineCap;
import pythagoras.f.Vector;

/** 
 * Renders one base expression, multiplied by the supplied factors
 * in the form f1*f2*f3(exp).
 */
public class TimesRenderer extends ModifierRenderer {
	
	public final static int UNKNOWN_NUMBER = Integer.MAX_VALUE;
	
	public TimesRenderer(Renderer base, int[] operands) {
		super(base, new FactorGroupRenderer(operands));
		((FactorGroupRenderer) modifier).useNegatives = true;
	}
	
	public TimesRenderer(Renderer base, int[] operands, boolean[] highlights) {
		super(base, new FactorGroupRenderer(operands, highlights));
		((FactorGroupRenderer) modifier).useNegatives = true;
	}
	
	public TimesRenderer(Renderer base, Renderer factor) {
		super(base, factor);
	}

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter childWriter = base.getExpressionWriter(textFormat);
		final ExpressionWriter factorWriter = modifier.getExpressionWriter(textFormat);
		
		return new ParentExpressionWriter(textFormat) {
			
			// the width of a parenthesis
			float w;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				float height = childWriter.height();
				w = height / 7;
				return new Vector(factorWriter.width() + childWriter.width() + w * 4, 
						Math.max(factorWriter.height(), childWriter.height()));
			}

			@Override
			protected void addChildren() {
				addChild(factorWriter, 0, (height() - factorWriter.height()) / 2);
				addChild(childWriter, w * 2 + factorWriter.width(), (height() - childWriter.height()) / 2);
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				// draw the factors and the expression
				super.drawExpression(canvas);

				canvas.save();
				canvas.setStrokeWidth(textFormat.font.size() / 10);
				canvas.setLineCap(LineCap.ROUND);
				
				setColor(canvas, modifier.fullyHighlighted());
				Path path = canvas.createPath();
				
				float height = childWriter.height();
				float x = factorWriter.width() + w * 0.5f, h = height * 0.9f;
				float y = (height() - h) / 2;
				canvas.translate(x, y);
				
				// draw left paren
				path.moveTo(w, (h + height) / 2);
				path.quadraticCurveTo(-w, height / 2, w, height - h);
				canvas.strokePath(path);
				
				// draw right paren
				path = canvas.createPath();
				canvas.translate(childWriter.width() + w * 3, 0);
				path.moveTo(-w, (h + height) / 2);
				path.quadraticCurveTo(w, height / 2, -w, height - h);
				canvas.strokePath(path);
				canvas.restore();
			}
		};
	}
}
