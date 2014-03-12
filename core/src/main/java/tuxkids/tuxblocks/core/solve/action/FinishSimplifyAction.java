package tuxkids.tuxblocks.core.solve.action;

public class FinishSimplifyAction extends SolveAction {

	public final String problem;
	public final int answer;

	public FinishSimplifyAction(String problem, int answer, boolean success) {
		super(success);
		this.problem = problem;
		this.answer = answer;
	}
	
	@Override
	public String name() {
		return "FinishSimplify";
	}
}
