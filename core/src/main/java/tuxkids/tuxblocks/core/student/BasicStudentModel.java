package tuxkids.tuxblocks.core.student;

import static tuxkids.tuxblocks.core.student.ActionType.ADD_EQUATION_SIDES;
import static tuxkids.tuxblocks.core.student.ActionType.ADD_INTEGERS;
import static tuxkids.tuxblocks.core.student.ActionType.ADD_UNKNOWNS;
import static tuxkids.tuxblocks.core.student.ActionType.BUILDING_SYMBOLIC_EQUATIONS;
import static tuxkids.tuxblocks.core.student.ActionType.BUILDING_WRITTEN_EQUATIONS;
import static tuxkids.tuxblocks.core.student.ActionType.COMBINATION;
import static tuxkids.tuxblocks.core.student.ActionType.DISTRIBUTION;
import static tuxkids.tuxblocks.core.student.ActionType.DIVIDE_INTEGERS_HIGH;
import static tuxkids.tuxblocks.core.student.ActionType.DIVIDE_INTEGERS_LOW;
import static tuxkids.tuxblocks.core.student.ActionType.DIVIDE_INTEGERS_MED;
import static tuxkids.tuxblocks.core.student.ActionType.DIVIDE_MULTIPLE_SIDES;
import static tuxkids.tuxblocks.core.student.ActionType.DIVIDE_SINGLE_SIDE;
import static tuxkids.tuxblocks.core.student.ActionType.MULTIPLY_INTEGERS_HIGH;
import static tuxkids.tuxblocks.core.student.ActionType.MULTIPLY_INTEGERS_LOW;
import static tuxkids.tuxblocks.core.student.ActionType.MULTIPLY_INTEGERS_MED;
import static tuxkids.tuxblocks.core.student.ActionType.MULTIPLY_MULTIPLE_SIDES;
import static tuxkids.tuxblocks.core.student.ActionType.MULTIPLY_SINGLE_SIDE;
import static tuxkids.tuxblocks.core.student.ActionType.SUBTRACT_EQUATION_SIDES;
import static tuxkids.tuxblocks.core.student.ActionType.SUBTRACT_INTEGERS;
import static tuxkids.tuxblocks.core.student.ActionType.SUBTRACT_UNKNOWNS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tuxkids.tuxblocks.core.solve.action.FinishSimplifyAction;
import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.action.StartProblemAction;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;

public class BasicStudentModel implements StudentModel {

	private final Map<ActionType, KnowledgeComponent> knowledgeBits = new HashMap<ActionType, KnowledgeComponent>();
	private final List<SolveAction> currentStudentActions = new ArrayList<SolveAction>();
	private Equation currentEquation;

	public BasicStudentModel() {
		initializeKnowledgeComponents();
	}

	private void initializeKnowledgeComponents() {
		knowledgeBits.put(ADD_INTEGERS, new KnowledgeComponent(
				"Add Integers < 100", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(SUBTRACT_INTEGERS, new KnowledgeComponent(
				"Subtract Integers < 100", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(MULTIPLY_INTEGERS_LOW, new KnowledgeComponent(
				"Multiply Integers 1-4", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(MULTIPLY_INTEGERS_MED, new KnowledgeComponent(
				"Multiply Integers 4-9", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(MULTIPLY_INTEGERS_HIGH, new KnowledgeComponent(
				"Multiply Integers 10+", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(DIVIDE_INTEGERS_LOW, new KnowledgeComponent(
				"Divide Integers 1-4", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(DIVIDE_INTEGERS_MED, new KnowledgeComponent(
				"Divide Integers 4-9", L0_HIGH, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(DIVIDE_INTEGERS_HIGH, new KnowledgeComponent(
				"Divide Integers 10+", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));

		knowledgeBits.put(ADD_UNKNOWNS, new KnowledgeComponent("Add Unknowns",
				L0_MED, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.put(SUBTRACT_UNKNOWNS, new KnowledgeComponent(
				"Subtract Unknowns", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));

		knowledgeBits.put(ADD_EQUATION_SIDES, new KnowledgeComponent(
				"Add Sides of Equations", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_LOW));
		knowledgeBits.put(SUBTRACT_EQUATION_SIDES, new KnowledgeComponent(
				"Subtract Sides of Equations", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_LOW));

		knowledgeBits.put(MULTIPLY_SINGLE_SIDE, new KnowledgeComponent(
				"Multiply Sides of Equations 1 term", L0_MED, BASE_SLIP,
				BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.put(MULTIPLY_MULTIPLE_SIDES, new KnowledgeComponent(
				"Multiply Sides of Equations 2+ terms", L0_LOW, BASE_SLIP,
				BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.put(DIVIDE_SINGLE_SIDE, new KnowledgeComponent(
				"Divide Sides of Equations 1 term", L0_MED, BASE_SLIP,
				BASE_GUESS, TRANSITION_LOW));
		knowledgeBits.put(DIVIDE_MULTIPLE_SIDES, new KnowledgeComponent(
				"Divide Sides of Equations 2+ terms", L0_LOW, BASE_SLIP,
				BASE_GUESS, TRANSITION_LOW));

		knowledgeBits.put(DISTRIBUTION, new KnowledgeComponent("Distribution",
				L0_LOW, BASE_SLIP, BASE_GUESS, TRANSITION_MED));
		knowledgeBits.put(COMBINATION, new KnowledgeComponent("Combination",
				L0_LOW, BASE_SLIP, BASE_GUESS, TRANSITION_LOW));

		knowledgeBits.put(BUILDING_SYMBOLIC_EQUATIONS, new KnowledgeComponent(
				"Building Symbolic Equations", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
		knowledgeBits.put(BUILDING_WRITTEN_EQUATIONS, new KnowledgeComponent(
				"Building Written Equations", L0_MED, BASE_SLIP, BASE_GUESS,
				TRANSITION_MED));
	}

	@Override
	public KnowledgeComponent getKnowledgeComponentForAction(StudentAction a) {
		return knowledgeBits.get(a.type);
	}
	
	private static final Equation[] starredEquations = new Equation[2];
	private int equationIndex = 0;
	
	static {
		starredEquations[0] = new Equation.Builder().addLeft(new VariableBlock("x"))
				.addRight(new NumberBlock(4).times(3)).createEquation().name("3x4");
		starredEquations[1] = new Equation.Builder().addLeft(new VariableBlock("x"))
				.addRight(new NumberBlock(6).minus(5)).createEquation().name("6-5");
	}

	@Override
	public boolean isReadyForNextStarred() {
		return equationIndex < 2;
	}

	@Override
	public Equation getNextStarredEquation() {
		return starredEquations[equationIndex++];
	}

	@Override
	public Equation getNextGeneralEquation() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void persist(Data data) throws ParseDataException, NumberFormatException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionPerformed(SolveAction action, Equation before) {
		if (action instanceof StartProblemAction) {		
			currentEquation = before.copy();
		} else {
			currentStudentActions.add(action);
		}
		
		if (action instanceof FinishSimplifyAction) {
			for (SolveAction pastAction : currentStudentActions) {
				
			}
			currentStudentActions.clear();
			currentEquation = null;
		}
		
	}

}
