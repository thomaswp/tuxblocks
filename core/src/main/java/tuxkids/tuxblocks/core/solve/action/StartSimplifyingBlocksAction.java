package tuxkids.tuxblocks.core.solve.action;

import tuxkids.tuxblocks.core.solve.blocks.EquationBlockIndex;

public class StartSimplifyingBlocksAction extends SolveAction {
	
	public final EquationBlockIndex baseIndex, pairIndex;
	public final int modifierDepth;
	public final String problem;
	public final int answer;
	
	public StartSimplifyingBlocksAction(EquationBlockIndex baseIndex, EquationBlockIndex pairIndex,
			int modifierDepth, String problem, int answer) {
		super(true);
		this.baseIndex = baseIndex;
		this.pairIndex = pairIndex;
		this.modifierDepth = modifierDepth;
		this.problem = problem;
		this.answer = answer;
	}

	@Override
	public String name() {
		return "StartSimplify";
	}
	
	@Override
	public String toString() {
		return name() + ": " + baseIndex + (pairIndex == null ? " <<-" : " <-> " + pairIndex) + ": " + problem;
	}
}