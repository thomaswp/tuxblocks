package tuxkids.tuxblocks.core.solve.expression;

public class Multiplication extends BinaryOperation {

	public Multiplication(Expression operandA, Expression operandB) {
		super(operandA, operandB);
	}

	@Override
	public int operate(int a, int b) {
		return a * b;
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
	public int getPrecedence() {
		return Expression.PREC_MULT;
	}

}
