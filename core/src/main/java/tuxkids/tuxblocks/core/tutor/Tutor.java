package tuxkids.tuxblocks.core.tutor;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.lang.Lang;
import tuxkids.tuxblocks.core.lang.Strings_Hint;
import tuxkids.tuxblocks.core.solve.action.DragAction;
import tuxkids.tuxblocks.core.solve.action.FinishSimplifyAction;
import tuxkids.tuxblocks.core.solve.action.ReciprocalAction;
import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.Block;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationBlockIndex;
import tuxkids.tuxblocks.core.solve.blocks.HorizontalModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.MutableEquation;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.student.KnowledgeComponent;
import tuxkids.tuxblocks.core.student.StudentAction;
import tuxkids.tuxblocks.core.student.StudentModel;
import tuxkids.tuxblocks.core.tutor.IdealEquationSolver.SolutionPackage;
import tuxkids.tuxblocks.core.tutor.IdealEquationSolver.Step;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.Formatter;

public class Tutor implements Strings_Hint {

	public enum HintLevel {
		Vague, Specific, BottomOut
	}
	
	private StudentModel model;
	private IdealEquationSolver solver = new IdealEquationSolver();

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


	public static class Hint {
		public final String text;
		public final SolveAction action;
		public final List<EquationBlockIndex> highlights = new ArrayList<EquationBlockIndex>();
		
		public final static String DOMAIN = "hint";

		public Hint(String text) {
			this.action = null;
			this.text = null;
		}
		
		public Hint(SolveAction action, String key, Object... args) {
			this.action = action;
			this.text = Formatter.format(Lang.getString(DOMAIN, key), args);
		}
		
		public Hint(SolveAction action, String key) {
			this.action = action;
			this.text = Lang.getString(DOMAIN, key);
		}
		
		public Hint addHighlight(EquationBlockIndex index) {
			highlights.add(index);
			return this;
		}
		
		@Override
		public String toString() {
			return text;
		}
	}
	
	public Hint getHint(Equation equation) {
		List<Step> solution = solver.aStar(equation, 1000);
		if (solution == null) return new Hint("Sorry - this one's got me stumped too...");
		if (solution.size() < 2) return new Hint("It looks like this one's solved. Press the check mark to continue.");
		return getHint(equation, solution.get(1).actions.get(0), HintLevel.Specific);
	}

	private Hint getHint(Equation equation, SolveAction action, HintLevel level) {
		if (action instanceof DragAction) {
			return getDragHint(equation, (DragAction) action, level);
		} else if (action instanceof FinishSimplifyAction) {
			return getSimplifyHint(equation, (FinishSimplifyAction) action, level);
		} else if (action instanceof ReciprocalAction) {
			return getReciprocalHint(equation, (ReciprocalAction) action, level);
		}
		Debug.write("Unkown action: " + action);
		return null;
	}
	
	private Hint getDragHint(Equation equation, DragAction action, HintLevel level) {
		Block dragging = equation.getBlock(action.fromIndex);
		BaseBlock dragTo = equation.getBaseBlock(action.toIndex);
		
		if (level == HintLevel.Vague) {
			String text;
			if (dragging instanceof VariableBlock) {
				if (dragTo instanceof VariableBlock) {
					text = key_dragVagueVariables;
				} else {
					text = key_dragVagueOneVariable;
				}
			} else if (dragging instanceof HorizontalModifierBlock) {
				text = key_dragVagueHorizontal;
			} else {
				text = key_dragVagueVertical;
			}
			return new Hint(action, text);
		}
		
		if (level == HintLevel.Specific) {
			return new Hint(action, key_dragSpecific, dragging.toString())
			.addHighlight(action.fromIndex);
		}
		
		return new Hint(action, key_dragBottomOut, 
				dragging.toString(), dragTo.toString());
	}
	
	private Hint getSimplifyHint(Equation equation, FinishSimplifyAction action, HintLevel level) {
		return null;
	}
	
	private Hint getReciprocalHint(Equation equation, ReciprocalAction action, HintLevel level) {
		return null;
	}
}
