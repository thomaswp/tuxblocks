package tuxkids.tuxblocks.core.tutor;

import java.util.List;

import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.student.KnowledgeComponent;
import tuxkids.tuxblocks.core.student.StudentAction;
import tuxkids.tuxblocks.core.student.StudentModel;
import tuxkids.tuxblocks.core.tutor.IdealEquationSolver.SolutionPackage;

public class Tutor {

	private StudentModel model;
	private IdealEquationSolver solver;
	
	private List<StudentAction> previousSolutionOrientedActions;
	
	
	public boolean studentPerformedAction(Equation e, StudentAction a)
	{
		
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
	
	
	public static KnowledgeComponent getKnowledgeComponentForAction(StudentAction a) {
		//going to involve
		return null;
	}
	
}
