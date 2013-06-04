package tuxkids.tuxblocks.core.eqn;

import playn.core.Color;

public class Minus extends ModificationOperation {

	public Minus(Expression base, int value) {
		super(base, value);
	}

	@Override
	public String toMathString() {
		return String.format("%s %s %d", operand.toMathString(), getSymbol(), value);
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


}
