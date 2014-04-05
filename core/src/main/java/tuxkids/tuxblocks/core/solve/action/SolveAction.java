package tuxkids.tuxblocks.core.solve.action;

import java.util.ArrayList;
import java.util.List;

import playn.core.PlayN;

public abstract class SolveAction {
	
	
	public final boolean success;
	public final double timestamp;
	private final List<Object> tags = new ArrayList<Object>();
	
	public Iterable<Object> tags() {
		return tags();
	}
	
	public SolveAction addTag(Object tag) {
		tags.add(tag);
		return this;
	}
	
	public SolveAction(boolean success) {
		this(success, PlayN.currentTime());
	}
	
	public SolveAction(boolean success, double timestamp) {
		this.success = success;
		this.timestamp = timestamp;
	}
	
	public abstract String name();
	
	@Override
	public String toString() {
		return (success ? "" : "~") + name();
	}
}
