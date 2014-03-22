package tuxkids.tuxblocks.core.solve.action;

public class FinishSimplifyAction extends SolveAction {


	public FinishSimplifyAction(boolean success) {
		super(success);
	}
	
	@Override
	public String name() {
		return "FinishSimplify";
	}
}
