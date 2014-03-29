package tuxkids.tuxblocks.core.solve.blocks;

import tuxkids.tuxblocks.core.utils.Formatter;

public class EquationBlockIndex {
	//TODO for performance, since all of these indexes are final,
	//perhaps cache (like Integer) some prebuilt objects and use 
	//a static build method
	//Solving a Difficulty 2 equation via AStar requires the allocation of about 1-2k of these
	//For a difficulty 0 equation, it's more like 10.
	
	public final ExpressionBlockIndex blockIndex;
	public final int expressionIndex;
	
	public EquationBlockIndex(int expressionIndex, ExpressionBlockIndex blockIndex) {
		this.expressionIndex = expressionIndex;
		this.blockIndex = blockIndex;
	}
	
	public EquationBlockIndex(int expressionIndex, int groupDepth, int groupIndex) {
		this.expressionIndex = expressionIndex;
		this.blockIndex = ExpressionBlockIndex.makeExpressionBlockIndex(groupDepth, groupIndex);
	}
	
	
	@Override
	public String toString() {
		return Formatter.format("{%d, %d, %d}", expressionIndex, blockIndex.depth, blockIndex.index);
	}

	public static EquationBlockIndex fromBaseBlockIndex(int index) {
		return new EquationBlockIndex(index, ExpressionBlockIndex.makeExpressionBlockIndex(0, 0));
	}
}
