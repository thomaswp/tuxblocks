package tuxkids.tuxblocks.core.expression;

import tuxkids.tuxblocks.core.utils.Formatter;

public abstract class BinaryOperation extends Expression {
	
	private Expression operandA, operandB;
	
	public Expression getOperandA() {
		return operandA;
	}
	
	public Expression getOperandB() {
		return operandB;
	}
	
	public BinaryOperation(Expression operandA, Expression operandB) {
		this.operandA = operandA;
		this.operandB = operandB;
	}
	
	@Override
	public String toMathString() {
		String aString = operandA.toMathString();
		if (operandA.getPrecedence() < getPrecedence()) {
			aString = "(" + aString + ")";
		}
		String bString = operandB.toMathString();
		if (operandB.getPrecedence() < getPrecedence()) {
			bString = "(" + bString + ")";
		}
		
		return Formatter.format("%s %s %s", aString, 
				getSymbol(), bString);
	}

	@Override
	public boolean hasVariable() {
		return operandA.hasVariable() || operandB.hasVariable();
	}

	@Override
	public int evaluate() throws NonevaluatableException {
		return operate(operandA.evaluate(), operandB.evaluate());
	}

	@Override
	public Expression getSimplified() {
		operandA = operandA.getSimplified();
		operandB = operandB.getSimplified();
		try {
			return new Number(evaluate());
		} catch (NonevaluatableException e) {
			return this;
		}
	}

	public abstract String getSymbol();
	public abstract int operate(int a, int b);
	public abstract boolean isCommutative();
}
