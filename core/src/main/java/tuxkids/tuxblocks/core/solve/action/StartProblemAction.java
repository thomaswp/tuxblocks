package tuxkids.tuxblocks.core.solve.action;

public class StartProblemAction extends SolveAction {

	public final String problem;
	
	public StartProblemAction(String problem) {
		super(true);
		this.problem = problem;
	}

	@Override
	public String name() {
		return "StartProblemAction";
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + problem;
	}

}
