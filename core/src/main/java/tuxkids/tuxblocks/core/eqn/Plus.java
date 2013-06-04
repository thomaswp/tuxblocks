package tuxkids.tuxblocks.core.eqn;

import playn.core.Color;

public class Plus extends ModificationOperation {

	public Plus(Expression base, int value) {
		super(base, value);
	}

	@Override
	public String toMathString() {
		return String.format("%s %s %d", operand.toMathString(), getSymbol(), value);
	}

	@Override
	public int evaluate() throws NonevaluatableException {
		return operand.evaluate() + value;
	}

	@Override
	public int getPrecedence() {
		return PREC_ADD;
	}

	@Override
	public String getSymbol() {
		return "+";
	}

	@Override
	public boolean isCommutative() {
		return true;
	}

	@Override
	public int getColor() {
		return Color.rgb(255, 0, 0);
	}

}
