package tuxkids.tuxblocks.core.solve.action.callbacks;

import tuxkids.tuxblocks.core.solve.SolveScreen;
import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.blocks.Equation;

public interface SolveActionCallback {
	/**
	 * Called when a player does a {@link SolveAction} on the
	 * {@link SolveScreen}. The 'before' Equation is the state of the
	 * equation before the action was performed. This is NOT a copy,
	 * so do not modify it! 
	 */
	void onActionPerformed(SolveAction action, Equation before);
}