package tuxkids.tuxblocks.core.expression;

public class Variable extends Expression {

	private String name;
	
	public String getName() {
		return name;
	}
	
	public Variable(String name) {
		this.name = name;
	}
	
	@Override
	public String toMathString() {
		return name;
	}

	@Override
	public boolean hasVariable() {
		return true;
	}

	@Override
	public int evaluate() throws NonevaluatableException {
		throw new NonevaluatableException();
	}

	@Override
	public Expression getSimplified() {
		return this;
	}

	@Override
	public int getPrecedence() {
		return PREC_NUM;
	}

}
