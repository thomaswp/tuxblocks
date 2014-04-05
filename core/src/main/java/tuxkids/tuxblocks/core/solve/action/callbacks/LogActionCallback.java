package tuxkids.tuxblocks.core.solve.action.callbacks;

import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.utils.Debug;

public class LogActionCallback implements SolveActionCallback {

	@Override
	public void onActionPerformed(SolveAction action, Equation before) {
		Debug.write(before.getPlainText() + " -> " + (action == null ? "[ ]" : action));
	}

}
