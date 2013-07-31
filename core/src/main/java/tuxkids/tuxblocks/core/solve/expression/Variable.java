package tuxkids.tuxblocks.core.solve.expression;

import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.utils.HashCode;

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

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(name);
	}

	@Override
	public BaseBlock toBaseBlock() {
		return new VariableBlock(name);
	}

}
