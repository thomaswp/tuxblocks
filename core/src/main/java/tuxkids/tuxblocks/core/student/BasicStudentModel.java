package tuxkids.tuxblocks.core.student;

import static tuxkids.tuxblocks.core.student.ActionType.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.student.EquationTree.EquationTreeNode;

public class BasicStudentModel implements StudentModel {

	private final Map<ActionType, KnowledgeComponent> knowledgeBits = new HashMap<ActionType, KnowledgeComponent>();

	private EquationTree equationTree;
	
	private Random rand = new Random();
	
	//stored to disk
	@SuppressWarnings("unused")
	private List<Float> equationTreeConfidences;
	
	public BasicStudentModel() {
		initializeKnowledgeComponents();
		initializeEquationTree();
	}

	private void initializeEquationTree() {
		this.equationTree = new EquationTree();

		final EquationTreeNode firstLevelMD = equationTree.addInitialNode(BasicStudentModelEquationGenerator.firstLevelMD());
		final EquationTreeNode firstLevelAS = equationTree.addInitialNode(BasicStudentModelEquationGenerator.firstLevelAS());
		
		@SuppressWarnings("unused")
		final EquationTreeNode singleDragAS = equationTree.addNode(BasicStudentModelEquationGenerator.singleDragAS(), new Criteria() {
			
			@Override
			public boolean hasBeenSatisfied() {
				return firstLevelAS.confidence()>.8;
			}
		}, firstLevelAS);
		
		@SuppressWarnings("unused")
		final EquationTreeNode firstLevelMDAS = equationTree.addNode(BasicStudentModelEquationGenerator.firstLevelMDAS(), new Criteria() {
			
			@Override
			public boolean hasBeenSatisfied() {
				return Math.min(firstLevelMD.confidence(), firstLevelAS.confidence()) > .7;
			}
		}, firstLevelAS, firstLevelMD);

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
	private int starredEquationIndex = 0;
	
	static {
		starredEquations[0] = new Equation.Builder().addLeft(new VariableBlock("x"))
				.addRight(new NumberBlock(4).times(3)).createEquation().name("3x4");
		starredEquations[1] = new Equation.Builder().addLeft(new VariableBlock("x"))
				.addRight(new NumberBlock(6).minus(5)).createEquation().name("6-5");
	}

	@Override
	public boolean isReadyForNextStarred() {
		return starredEquationIndex < 2;
	}

	@Override
	public Equation getNextStarredEquation() {
		return starredEquations[starredEquationIndex++];
	}

	@Override
	public Equation getNextGeneralEquation() {

		return equationTree.randomWeightedUnlockedNode(rand).equation();
	}

	
	
	@Override
	public void persist(Data data) throws ParseDataException, NumberFormatException {
		// TODO Auto-generated method stub
		
	}

}
