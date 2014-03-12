package tuxkids.tuxblocks.core.solve.action;

public class LeaveProblemAction extends SolveAction {

	public LeaveProblemAction(boolean success) {
		super(success);
	}

	@Override
	public String name() {
		return "LeaveProblem";
	}

}
