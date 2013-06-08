package tuxkids.tuxblocks.core.expression;

import playn.core.Color;
import tuxkids.tuxblocks.core.utils.Formatter;

public class Minus extends ModificationOperation {

	public Minus(Expression base, int value) {
		super(base, value);
	}

	@Override
	public String toMathString() {
		return Formatter.format("%s %s %d", operand.toMathString(), getSymbol(), value);
	}

	@Override
	public int evaluate() throws NonevaluatableException {
		return operand.evaluate() - value;
	}

	@Override
	public int getPrecedence() {
		return PREC_ADD;
	}

	@Override
	public String getSymbol() {
		return "-";
	}

	@Override
	public boolean isCommutative() {
		return false;
	}

	@Override
	public int getColor() {
		return Color.rgb(0, 0, 255);
	}

	@Override
	public ModificationOperation getInverse() {
		return new Plus(operand, value);
	}
}
