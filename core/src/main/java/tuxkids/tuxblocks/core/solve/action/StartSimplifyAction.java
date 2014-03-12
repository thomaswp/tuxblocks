package tuxkids.tuxblocks.core.solve.action;

public class StartSimplifyAction extends SolveAction {
	
	public final String problem;
	
	public StartSimplifyAction(String problem) {
		super(true);
		this.problem = problem;
	}

	@Override
	public String name() {
		return "StartSimplify";
	}
}
