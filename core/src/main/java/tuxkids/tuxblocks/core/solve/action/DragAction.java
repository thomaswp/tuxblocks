package tuxkids.tuxblocks.core.solve.action;

public class DragAction extends SolveAction {	
	public final int fromIndex, toIndex;
	public final String draggedExpression;
	
	public DragAction(int fromIndex, int toIndex, String draggedExpression) {
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
		this.draggedExpression = draggedExpression;
	}

	@Override
	protected String name() {
		return "DragAction";
	}
}
