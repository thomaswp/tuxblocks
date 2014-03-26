package tuxkids.tuxblocks.core.solve.action;


public class StartSimplifyingBlocksAction extends SolveAction {

	public final String problem;
	public final int answer;
	
	public StartSimplifyingBlocksAction(String problem, int answer) {
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