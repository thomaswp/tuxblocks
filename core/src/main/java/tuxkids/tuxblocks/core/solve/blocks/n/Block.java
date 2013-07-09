package tuxkids.tuxblocks.core.solve.blocks.n;

public abstract class Block {
	public abstract boolean canRelease(boolean openBlock);
	protected abstract String toMathString(String base);
}
