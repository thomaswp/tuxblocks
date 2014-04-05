package tuxkids.tuxblocks.core.solve.action.callbacks;

import playn.core.PlayN;
import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.action.StartProblemAction;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.utils.persist.PersistUtils;

public class PersistActionCallback implements SolveActionCallback {

	private int solutionLength;
	
	@Override
	public void onActionPerformed(SolveAction action, Equation before) {
		PersistUtils.persist(before, "lastSolution" + solutionLength);
		solutionLength++;
		PlayN.storage().setItem("lastSolutionLength", "" + solutionLength);
		if (action instanceof StartProblemAction) {
			solutionLength = 0;
			int l = 0;
			while (PlayN.storage().getItem("lastSolution" + l) != null) {
				PlayN.storage().removeItem("lastSolution" + l);
			}
		}
	}

}
