package tuxkids.tuxblocks.core.student;

import tuxkids.tuxblocks.core.solve.action.callbacks.SolveActionCallback;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

public interface StudentModel extends Persistable, SolveActionCallback {
	
	public static final double L0_HIGH =.7;
	public static final double L0_MED  =.4;
	public static final double L0_LOW =.1;
	
	public static final double BASE_SLIP = .1;
	
	public static final double BASE_GUESS =.1;

	public static final double TRANSITION_HIGH =.3;
	public static final double TRANSITION_MED  =.1;
	public static final double TRANSITION_LOW =.05;
	
	KnowledgeComponent getKnowledgeComponentForAction(StudentAction a);

	boolean isReadyForNextStarred();

	Equation getNextStarredEquation();

	Equation getNextGeneralEquation();
	
}
