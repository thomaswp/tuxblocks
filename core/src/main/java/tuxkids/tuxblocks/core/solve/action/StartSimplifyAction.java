package tuxkids.tuxblocks.core.solve.action;


public class StartSimplifyAction extends SolveAction {

	public final String problem;
	public final int answer;
	
	public StartSimplifyAction(String problem, int answer) {
		super(true);
		this.problem = problem;
		this.answer = answer;
	}

	@Override
	public String name() {
		return "StartSimplify";
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + problem + " -> " + answer;
	}
}