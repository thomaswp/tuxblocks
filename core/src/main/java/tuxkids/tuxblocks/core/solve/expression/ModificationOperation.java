package tuxkids.tuxblocks.core.solve.expression;

import playn.core.Canvas;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;
import tuxkids.tuxblocks.core.utils.HashCode;

public abstract class ModificationOperation extends Expression {

	protected int value;
	protected Expression operand;
	
	public int getValue() {
		return value;
	}
	
	public Expression getOperand() {
		return operand;
	}
	
	public void setOperand(Expression operand) {
		this.operand = operand;
	}
	
	public ModificationOperation(Expression operand, int value) {
		this.value = value;
		this.operand = operand;
	}

	@Override
	public boolean hasVariable() {
		return operand.hasVariable();
	}

	@Override
	public Expression getSimplified() {
		return this;
	}
	
	@Override
	public ExpressionWriter getExpressionWriter(TextFormat textFormat) {
		final ExpressionWriter childWriter = operand.getExpressionWriter(textFormat);
		return new ExpressionWriter(textFormat) {
			
			TextLayout opLayout;
			
			@Override
			protected Vector formatExpression(TextFormat textFormat) {
				opLayout = PlayN.graphics().layoutText(" " + getSymbol() + " " + value, textFormat);
				return new Vector(childWriter.width() + opLayout.width(), childWriter.height());
			}
			
			@Override
			public void drawExpression(Canvas canvas, int childColor) {
				canvas.save();
				canvas.setFillColor(childColor);
				canvas.setStrokeColor(childColor);
				childWriter.drawExpression(canvas, childColor);
				canvas.restore();
				
				canvas.fillText(opLayout, width() - opLayout.width(), 
						(height() - opLayout.height()) / 2);
			}
		};
	}

	public abstract String getSymbol();
	public abstract boolean isCommutative();
	public abstract int getColor();
	public abstract ModificationOperation getInverse();

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(operand);
		hashCode.addField(value);
	}
}
