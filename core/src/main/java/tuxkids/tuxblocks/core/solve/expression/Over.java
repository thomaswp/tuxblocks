package tuxkids.tuxblocks.core.solve.expression;

import playn.core.Canvas;
import playn.core.Color;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.utils.Formatter;

public class Over extends ModificationOperation {

	public Over(Expression base, int value) {
		super(base, value);
	}

	@Override
	public String toMathString() {
		if (operand.getPrecedence() < getPrecedence()) {
			return Formatter.format("(%s) %s %d", operand.toMathString(), getSymbol(), value);
		} else {
			return Formatter.format("%s %s %d", operand.toMathString(), getSymbol(), value);
		}
	}

	@Override
	public int evaluate() throws NonevaluatableException {
		return operand.evaluate() / value;
	}

	@Override
	public int getPrecedence() {
		return PREC_MULT;
	}

	@Override
	public String getSymbol() {
		return "/";
	}

	@Override
	public boolean isCommutative() {
		return false;
	}

	@Override
	public int getColor() {
		return Color.rgb(0, 204, 0);
	}

	@Override
	public ModificationOperation getInverse() {
		return new Times(operand, value);
	}

	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter childWriter = operand.getExpressionWriter(textFormat);
		return new ExpressionWriter(textFormat) {
			
			TextLayout divisorLayout;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				divisorLayout = PlayN.graphics().layoutText("" + value, textFormat);
				return new Vector(childWriter.width(), childWriter.height() + 
						divisorLayout.height() + SPACING * 2);
			}
			
			@Override
			public void drawExpression(Canvas canvas, int childColor) {
				canvas.save();
				canvas.setFillColor(childColor);
				canvas.setStrokeColor(childColor);
				childWriter.drawExpression(canvas, childColor);
				canvas.restore();
				
				float y = childWriter.height() + SPACING;
				canvas.drawLine(0, y, childWriter.width(), y);
				canvas.fillText(divisorLayout, 
						(width() - divisorLayout.width()) / 2, y + SPACING);
			}
		};
	}

	@Override
	public BaseBlock toBaseBlock() {
		return operand.toBaseBlock().over(value);
	}
}
