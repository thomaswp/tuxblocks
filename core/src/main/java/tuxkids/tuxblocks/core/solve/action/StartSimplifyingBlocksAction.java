package tuxkids.tuxblocks.core.solve.action;

import tuxkids.tuxblocks.core.solve.blocks.EquationBlockIndex;

public class StartSimplifyingBlocksAction extends SolveAction {
	
	public final EquationBlockIndex baseIndex, pairIndex;
	public final int modifierDepth;
	
	public StartSimplifyingBlocksAction(EquationBlockIndex baseIndex, EquationBlockIndex pairIndex,
			int modifierDepth) {
		super(true);
		this.baseIndex = baseIndex;
		this.pairIndex = pairIndex;
		this.modifierDepth = modifierDepth;
	}

	@Override
	public String name() {
		return "StartSimplify";
	}
	
	@Override
	public String toString() {
		return name() + ": " + baseIndex + (pairIndex == null ? " <<-" : " <-> " + pairIndex);
	}
}