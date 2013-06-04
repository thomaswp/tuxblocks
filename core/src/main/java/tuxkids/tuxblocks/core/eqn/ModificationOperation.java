package tuxkids.tuxblocks.core.eqn;

public abstract class ModificationOperation extends Expression {

	protected int value;
	protected Expression operand;
	
	public int getValue() {
		return value;
	}
	
	public Expression getOperand() {
		return operand;
	}
	
	public ModificationOperation(Expression operand, int value) {
		this.value = value;
		this.operand = operand;
	}

	@Override
	public boolean hasVariable() {
		return operand.hasVariable();
	}

	@Override
	public Expression getSimplified() {
		return this;
	}

	public abstract String getSymbol();
	public abstract boolean isCommutative();
	public abstract int getColor();

}
