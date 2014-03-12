package tuxkids.tuxblocks.core.solve.action;

public class StartSimplifyVariablesAction extends SolveAction {
	
	public final String problem;
	
	public StartSimplifyVariablesAction(String problem) {
		super(true);
		this.problem = problem;
	}

	@Override
	public String name() {
		return "StartSimplify";
	}
}
