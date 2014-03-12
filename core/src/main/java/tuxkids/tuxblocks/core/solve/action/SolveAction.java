package tuxkids.tuxblocks.core.solve.action;

import playn.core.PlayN;

public abstract class SolveAction {
	public final boolean success;
	public final double timestamp;
	
	public SolveAction(boolean success) {
		this(success, PlayN.currentTime());
	}
	
	public SolveAction(boolean success, double timestamp) {
		this.success = success;
		this.timestamp = timestamp;
	}
	
	public abstract String name();
}
