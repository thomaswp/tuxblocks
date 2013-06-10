package tuxkids.tuxblocks.core.solve.expression;

public class Subtraction extends BinaryOperation {

	public Subtraction(Expression operandA, Expression operandB) {
		super(operandA, operandB);
	}

	@Override
	public int operate(int a, int b) {
		return a - b;
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
	public int getPrecedence() {
		return Expression.PREC_ADD;
	}

}
