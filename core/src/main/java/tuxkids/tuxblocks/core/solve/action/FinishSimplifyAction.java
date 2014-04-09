package tuxkids.tuxblocks.core.solve.action;

import tuxkids.tuxblocks.core.solve.blocks.EquationBlockIndex;

public class FinishSimplifyAction extends SolveAction {

	public final EquationBlockIndex baseIndex, pairIndex;
	public final int modifierDepth, fails;
	
	public FinishSimplifyAction(EquationBlockIndex baseIndex, EquationBlockIndex pairIndex, int modifierDepth, int fails, boolean success) {
		super(success);
		this.baseIndex = baseIndex;
		this.pairIndex = pairIndex;
		this.modifierDepth = modifierDepth;
		this.fails = fails;
	}
	
	@Override
	public String name() {
		return "FinishSimplify";
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + fails + " fails): " + baseIndex + (pairIndex == null ? " <<-" : " <-> " + pairIndex);
	}
}
