package tuxkids.tuxblocks.core.solve.action;

import tuxkids.tuxblocks.core.solve.blocks.EquationBlockIndex;

public class FinishSimplifyAction extends SolveAction {
	
	public final static int AUTO_SIMPLIFY = -1;

	public final EquationBlockIndex baseIndex, pairIndex;
	public final int modifierDepth, mistakes;
	
	public FinishSimplifyAction(EquationBlockIndex baseIndex, EquationBlockIndex pairIndex, int modifierDepth, int mistakes, boolean success) {
		super(success);
		this.baseIndex = baseIndex;
		this.pairIndex = pairIndex;
		this.modifierDepth = modifierDepth;
		this.mistakes = mistakes;
	}
	
	@Override
	public String name() {
		return "FinishSimplify";
	}
	
	@Override
	public String toString() {
		String mistakeString = mistakes >= 0 ? " (" + mistakes + " mistakes)" : " (auto)";
		return super.toString() + mistakeString + ": " + baseIndex + (pairIndex == null ? " <<-" : " <-> " + pairIndex);
	}
}
