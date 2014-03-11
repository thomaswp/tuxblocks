package tuxkids.tuxblocks.core.solve.blocks;

import tuxkids.tuxblocks.core.utils.Formatter;

public class EquationBlockIndex {
	public final ExpressionBlockIndex blockIndex;
	public final int expressionIndex;
	
	public EquationBlockIndex(int expressionIndex, ExpressionBlockIndex blockIndex) {
		this.expressionIndex = expressionIndex;
		this.blockIndex = blockIndex;
	}
	
	public EquationBlockIndex(int expressionIndex, int groupDepth, int groupIndex) {
		this.expressionIndex = expressionIndex;
		this.blockIndex = new ExpressionBlockIndex(groupDepth, groupIndex);
	}
	
	@Override
	public String toString() {
		return Formatter.format("{%d, %d, %d}", expressionIndex, blockIndex.depth, blockIndex.index);
	}
}
