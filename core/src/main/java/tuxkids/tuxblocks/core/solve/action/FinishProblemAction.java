package tuxkids.tuxblocks.core.solve.action;

public class FinishProblemAction extends SolveAction {

	public FinishProblemAction(boolean success) {
		super(success);
	}

	@Override
	public String name() {
		return "FinishProblem";
	}

}
