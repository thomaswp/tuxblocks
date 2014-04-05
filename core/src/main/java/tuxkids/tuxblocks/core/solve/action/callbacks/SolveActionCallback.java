package tuxkids.tuxblocks.core.solve.action.callbacks;

import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.blocks.Equation;

public interface SolveActionCallback {
	void onActionPerformed(SolveAction action, Equation before);
}