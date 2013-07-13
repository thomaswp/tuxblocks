package tuxkids.tuxblocks.core.solve.blocks.n;

public abstract class ModifierBlock extends Block {

	protected int value;
	protected BlockGroup<?> group;
	
	public ModifierBlock(int value) {
		this.value = value;
	}
	
	public boolean attached() {
		return group != null;
	}
	
	protected abstract boolean isInverseOperation(ModifierBlock block);
	
	public abstract boolean canSimplify();
	public abstract boolean canReduce();
	protected abstract String symbol();
	
	public int value() {
		return value;
	}
	
	@Override
	public String text() {
		return symbol() + value;
	}
}
