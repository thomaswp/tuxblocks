package tuxkids.tuxblocks.core.solve.action;

public abstract class SolveAction {
	protected boolean success;
	protected long timestamp;
	
	protected abstract String name();
}
