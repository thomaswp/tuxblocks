package tuxkids.tuxblocks.core.solve.expression;

import tuxkids.tuxblocks.core.utils.HashCode;

public class Number extends Expression {

	private int value;
	
	public int getValue() {
		return value;
	}
	
	public Number(int value) {
		this.value = value;
	}
	
	@Override
	public String toMathString() {
		return "" + value;
	}

	@Override
	public boolean hasVariable() {
		return false;
	}

	@Override
	public int evaluate() throws NonevaluatableException {
		return value;
	}

	@Override
	public Expression getSimplified() {
		return this;
	}

	@Override
	public int getPrecedence() {
		return PREC_NUM;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(value);
	}

}
