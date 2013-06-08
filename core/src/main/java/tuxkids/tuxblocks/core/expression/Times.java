package tuxkids.tuxblocks.core.expression;

import playn.core.Canvas;
import playn.core.Color;
import playn.core.Font;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.utils.Formatter;

public class Times extends ModificationOperation {

	public Times(Expression base, int value) {
		super(base, value);
	}

	@Override
	public String toMathString() {
		if (operand.getPrecedence() < getPrecedence()) {
			return Formatter.format("%d(%s)", value, operand.toMathString());
		} else {
			return Formatter.format("%s %s %d", operand.toMathString(), getSymbol(), value);
		}
	}

	@Override
	public int evaluate() throws NonevaluatableException {
		return value * operand.evaluate();
	}

	@Override
	public int getPrecedence() {
		return PREC_MULT;
	}

	@Override
	public String getSymbol() {
		return "*";
	}

	@Override
	public boolean isCommutative() {
		return true;
	}

	@Override
	public int getColor() {
		return Color.rgb(150, 250, 0);
	}

	@Override
	public ModificationOperation getInverse() {
		return new Over(operand, value);
	}
	
	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		if (operand.getPrecedence() >= getPrecedence()) return super.getExpressionWriter(textFormat);
		
		final ExpressionWriter childWriter = operand.getExpressionWriter(textFormat);
		return new ExpressionWriter(textFormat) {
			
			TextLayout leftParenLayout, rightParenLayout, factorLayout;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				Font font = PlayN.graphics().createFont(textFormat.font.name(), 
						textFormat.font.style(), childWriter.height() * 1.1f);
				TextFormat bigFormat = new TextFormat().withFont(font);
				leftParenLayout = PlayN.graphics().layoutText("(", bigFormat);
				rightParenLayout = PlayN.graphics().layoutText(")", bigFormat);
				factorLayout = PlayN.graphics().layoutText("" + value, textFormat);
				return new Vector(factorLayout.width() + leftParenLayout.width() + 
						childWriter.width() + rightParenLayout.width(), 
						childWriter.height());
			}
			
			@Override
			public void drawExpression(Canvas canvas) {
				canvas.save();
				canvas.translate(leftParenLayout.width() + factorLayout.width() + SPACING, 0);
				childWriter.drawExpression(canvas);
				canvas.restore();
				
				canvas.fillText(factorLayout, 0, (height() - factorLayout.height()) / 2);
				canvas.fillText(leftParenLayout, factorLayout.width(), 
						(height() - leftParenLayout.height()) / 2);
				canvas.fillText(rightParenLayout, width() - rightParenLayout.width(),
						(height() - rightParenLayout.height()) / 2);
			}
		};
	}

}
