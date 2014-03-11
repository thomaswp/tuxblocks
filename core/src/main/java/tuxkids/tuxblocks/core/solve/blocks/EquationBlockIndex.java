package tuxkids.tuxblocks.core.solve.blocks;

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
}
