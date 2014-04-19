package tuxkids.tuxblocks.core.solve.action;

import tuxkids.tuxblocks.core.tutor.Tutor.Hint;

public class HintAction extends SolveAction {
	public final int level;
	public final String text;
	public final String hintString;
	
	public HintAction(Hint hint) {
		super(true);
		this.level = hint.level.ordinal();
		this.text = hint.text;
		this.hintString = hint.action == null ? "" : hint.action.toString();
	}

	@Override
	public String name() {
		return "HintAction";
	}
	
	public String toString() {
		return super.toString() + " (" + level + "): " + text + " / " + hintString;
	}
}
