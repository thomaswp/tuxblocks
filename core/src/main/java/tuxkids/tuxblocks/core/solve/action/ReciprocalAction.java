package tuxkids.tuxblocks.core.solve.action;

import tuxkids.tuxblocks.core.solve.blocks.EquationBlockIndex;

public class ReciprocalAction extends SolveAction {
	
	public final EquationBlockIndex index;
	
	public ReciprocalAction(EquationBlockIndex index, boolean success) {
		super(success);
		this.index = index;
	}

	@Override
	public String name() {
		return "ReciprocalAction";
	}
	
	@Override
	public String toString() {
		return name() + ": " + index;
	}

}
