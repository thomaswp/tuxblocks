package tuxkids.tuxblocks.core.tutor;

import java.util.List;

import tuxkids.tuxblocks.core.solve.blocks.MutableEquation;
import tuxkids.tuxblocks.core.student.KnowledgeComponent;
import tuxkids.tuxblocks.core.student.StudentAction;
import tuxkids.tuxblocks.core.student.StudentModel;
import tuxkids.tuxblocks.core.tutor.IdealEquationSolver.SolutionPackage;

public class Tutor {

	private StudentModel model;
	private IdealEquationSolver solver;

	private List<StudentAction> previousSolutionOrientedActions;


	public boolean studentPerformedAction(MutableEquation e, StudentAction a)
	{

		if (a.wasValidAction) {

			if (previousSolutionOrientedActions.contains(a))
			{
				//update student model positive
				KnowledgeComponent kc = getKnowledgeComponentForAction(a);
				kc.studentAnswered(true);
			}
			else
			{
				//negatively enforce 
				for(StudentAction sa : previousSolutionOrientedActions) {
					KnowledgeComponent kc = getKnowledgeComponentForAction(sa);
					kc.studentAnswered(false);
				}
			}



			SolutionPackage idealSolution = solver.getIdealSolution(e);

			if (idealSolution.numSteps != 0) {
				previousSolutionOrientedActions = idealSolution.solutionOrientedActions;
				return false;
			}
			return true;
		}

		//bad things

		return false;
	}


	public KnowledgeComponent getKnowledgeComponentForAction(StudentAction a) {
		return model.getKnowledgeComponentForAction(a);
	}

}
