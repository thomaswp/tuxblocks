package tuxkids.tuxblocks.core.eqn;

import playn.core.Color;

public class Over extends ModificationOperation {

	public Over(Expression base, int value) {
		super(base, value);
	}

	@Override
	public String toMathString() {
		if (operand.getPrecedence() < getPrecedence()) {
			return String.format("(%s)%s%d", operand.toMathString(), getSymbol(), value);
		} else {
			return String.format("%s %s %d", operand.toMathString(), getSymbol(), value);
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
		return Color.rgb(0, 123, 0);
	}

}
