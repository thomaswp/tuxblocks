package tuxkids.tuxblocks.core.eqn;

import playn.core.Color;

public class Times extends ModificationOperation {

	public Times(Expression base, int value) {
		super(base, value);
	}

	@Override
	public String toMathString() {
		if (operand.getPrecedence() < getPrecedence()) {
			return String.format("%d(%s)", value, operand.toMathString());
		} else {
			return String.format("%s %s %d", operand.toMathString(), getSymbol(), value);
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

}
