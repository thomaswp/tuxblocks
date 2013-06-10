package tuxkids.tuxblocks.core.solve.expression;

import playn.core.Canvas;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;

public abstract class Expression {

	public final static int PREC_ADD = 0;
	public final static int PREC_MULT = 1;
	public final static int PREC_NUM = 2;
	
	public abstract String toMathString();
	public abstract boolean hasVariable();
	public abstract int evaluate() throws NonevaluatableException;
	public abstract Expression getSimplified();
	public abstract int getPrecedence();
	
	public Expression plus(int value) {
		return new Plus(this, value);
	}
	
	public Expression minus(int value) {
		return new Minus(this, value);
	}
	
	public Expression times(int value) {
		return new Times(this, value);
	}
	
	public Expression over(int value) {
		return new Over(this, value);
	}
	
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final String mathString = toMathString();
		return new ExpressionWriter(textFormat) {
			
			private TextLayout layout;
			
			@Override
			public void drawExpression(Canvas canvas, int childColor) {
				canvas.fillText(layout, 0, 0);
			}

			@Override
			public Vector formatExpression(TextFormat textFormat) {
				layout = layout(mathString, textFormat);
				return new Vector(layout.width(), layout.height());
			}
		};
	}
}
