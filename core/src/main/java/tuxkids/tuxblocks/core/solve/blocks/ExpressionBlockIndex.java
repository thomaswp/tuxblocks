package tuxkids.tuxblocks.core.solve.blocks;

import tuxkids.tuxblocks.core.utils.Formatter;

public class ExpressionBlockIndex {
	public final int depth, index;
	
	public ExpressionBlockIndex(int depth, int index) {
		this.depth = depth;
		this.index = index;
	}
	
	public ExpressionBlockIndex oneDeeper() {
		return new ExpressionBlockIndex(depth + 1, index);
	}
	
	public ExpressionBlockIndex oneShallower() {
		return new ExpressionBlockIndex(depth - 1, index);
	}
	
	@Override
	public String toString() {
		return Formatter.format("{%d, %d}", depth, index);
	}
}
