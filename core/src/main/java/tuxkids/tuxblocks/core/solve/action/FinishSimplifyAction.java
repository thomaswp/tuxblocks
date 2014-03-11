package tuxkids.tuxblocks.core.solve.action;

public class FinishSimplifyAction extends SolveAction {
	protected String problem;
	protected int answer;
	
	@Override
	protected String name() {
		return "FinishSimplify";
	}
}
