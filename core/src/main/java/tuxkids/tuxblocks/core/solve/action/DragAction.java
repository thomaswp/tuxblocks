package tuxkids.tuxblocks.core.solve.action;

import tuxkids.tuxblocks.core.solve.blocks.EquationBlockIndex;

public class DragAction extends SolveAction {
	public final EquationBlockIndex fromIndex;	
	public final int toIndex;

	public DragAction(EquationBlockIndex fromIndex, int toIndex, boolean success) {
		super(success);
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}

	@Override
	public String name() {
		return "DragAction";
	}
	
	@Override
	public String toString() {
		return name() + ": " + fromIndex + " -> " + toIndex;
	}
}
