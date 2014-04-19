package tuxkids.tuxblocks.core.student;

import static tuxkids.tuxblocks.core.student.ActionType.ADD_EQUATION_SIDES;
import static tuxkids.tuxblocks.core.student.ActionType.ADD_INTEGERS;
import static tuxkids.tuxblocks.core.student.ActionType.BUILDING_SYMBOLIC_EQUATIONS;
import static tuxkids.tuxblocks.core.student.ActionType.BUILDING_WRITTEN_EQUATIONS;
import static tuxkids.tuxblocks.core.student.ActionType.CANCEL_ADDENS;
import static tuxkids.tuxblocks.core.student.ActionType.CANCEL_FACTORS;
import static tuxkids.tuxblocks.core.student.ActionType.COMBINATION;
import static tuxkids.tuxblocks.core.student.ActionType.COMBINE_UNKNOWNS;
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
import static tuxkids.tuxblocks.core.student.ActionType.SIMPLIFY_ADDENS;
import static tuxkids.tuxblocks.core.student.ActionType.SIMPLIFY_DIFFERENT_FACTORS;
import static tuxkids.tuxblocks.core.student.ActionType.SIMPLIFY_LIKE_FACTORS;
import static tuxkids.tuxblocks.core.student.ActionType.SUBTRACT_EQUATION_SIDES;
import static tuxkids.tuxblocks.core.student.ActionType.SUBTRACT_INTEGERS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.action.DragAction;
import tuxkids.tuxblocks.core.solve.action.FinishProblemAction;
import tuxkids.tuxblocks.core.solve.action.FinishSimplifyAction;
import tuxkids.tuxblocks.core.solve.action.HintAction;
import tuxkids.tuxblocks.core.solve.action.ReciprocalAction;
import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.action.StartProblemAction;
import tuxkids.tuxblocks.core.solve.action.StartSimplifyAction;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.Block;
import tuxkids.tuxblocks.core.solve.blocks.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationManipulator;
import tuxkids.tuxblocks.core.solve.blocks.HorizontalModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.MinusBlock;
import tuxkids.tuxblocks.core.solve.blocks.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.ModifierGroup;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.OverBlock;
import tuxkids.tuxblocks.core.solve.blocks.PlusBlock;
import tuxkids.tuxblocks.core.solve.blocks.TimesBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.solve.blocks.VerticalModifierBlock;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.student.EquationTree.EquationTreeNode;
import tuxkids.tuxblocks.core.tutor.IdealEquationSolver.Step;
import tuxkids.tuxblocks.core.tutor.IdealEquationSolver;
import tuxkids.tuxblocks.core.tutor.Tutor;
import tuxkids.tuxblocks.core.tutor.Tutor.HintLevel;
import tuxkids.tuxblocks.core.utils.Debug;

public class BasicStudentModel implements StudentModel {

	private final HashMap<ActionType, KnowledgeComponent> knowledgeBits = new HashMap<ActionType, KnowledgeComponent>();
	private final List<SolveAction> currentStudentActions = new ArrayList<SolveAction>();
	private final List<Equation> currentEquations = new ArrayList<Equation>();

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
				"Add Integers < 100", L0_HIGH, SLIP_EXTREME, GUESS_HIGH,
				TRANSITION_MED));
		knowledgeBits.put(SUBTRACT_INTEGERS, new KnowledgeComponent(
				"Subtract Integers < 100", L0_HIGH, SLIP_EXTREME, GUESS_HIGH,
				TRANSITION_MED));
		knowledgeBits.put(MULTIPLY_INTEGERS_LOW, new KnowledgeComponent(
				"Multiply Integers 1-4", L0_HIGH, SLIP_EXTREME, GUESS_HIGH,
				TRANSITION_MED));
		knowledgeBits.put(MULTIPLY_INTEGERS_MED, new KnowledgeComponent(
				"Multiply Integers 4-9", L0_MED, SLIP_EXTREME, GUESS_HIGH,
				TRANSITION_MED));
		knowledgeBits.put(MULTIPLY_INTEGERS_HIGH, new KnowledgeComponent(
				"Multiply Integers 10+", L0_MED, SLIP_EXTREME, GUESS_MED,
				TRANSITION_MED));
		knowledgeBits.put(DIVIDE_INTEGERS_LOW, new KnowledgeComponent(
				"Divide Integers 1-4", L0_HIGH, SLIP_EXTREME, GUESS_HIGH,
				TRANSITION_MED));
		knowledgeBits.put(DIVIDE_INTEGERS_MED, new KnowledgeComponent(
				"Divide Integers 4-9", L0_MED, SLIP_EXTREME, GUESS_HIGH,
				TRANSITION_MED));
		knowledgeBits.put(DIVIDE_INTEGERS_HIGH, new KnowledgeComponent(
				"Divide Integers 10+", L0_MED, SLIP_EXTREME, GUESS_MED,
				TRANSITION_MED));

		knowledgeBits.put(COMBINE_UNKNOWNS, new KnowledgeComponent(
				"Combine Unknowns", L0_MED, SLIP_LOW, GUESS_LOW, TRANSITION_MED));

		knowledgeBits.put(ADD_EQUATION_SIDES, new KnowledgeComponent(
				"Add Sides of Equations", L0_MED, SLIP_MED, GUESS_MED,
				TRANSITION_LOW));
		knowledgeBits.put(SUBTRACT_EQUATION_SIDES, new KnowledgeComponent(
				"Subtract Sides of Equations", L0_MED, SLIP_MED, GUESS_MED,
				TRANSITION_LOW));

		knowledgeBits.put(MULTIPLY_SINGLE_SIDE, new KnowledgeComponent(
				"Multiply Sides of Equations 1 term", L0_MED, SLIP_MED,
				GUESS_MED, TRANSITION_LOW));
		knowledgeBits.put(MULTIPLY_MULTIPLE_SIDES, new KnowledgeComponent(
				"Multiply Sides of Equations 2+ terms", L0_LOW, SLIP_LOW,
				GUESS_LOW, TRANSITION_LOW));
		knowledgeBits.put(DIVIDE_SINGLE_SIDE, new KnowledgeComponent(
				"Divide Sides of Equations 1 term", L0_MED, SLIP_MED,
				GUESS_MED, TRANSITION_LOW));
		knowledgeBits.put(DIVIDE_MULTIPLE_SIDES, new KnowledgeComponent(
				"Divide Sides of Equations 2+ terms", L0_LOW, SLIP_LOW,
				GUESS_LOW, TRANSITION_LOW));
		
		knowledgeBits.put(SIMPLIFY_ADDENS, new KnowledgeComponent(
				"Combine two addens", L0_LOW, SLIP_MED,
				GUESS_MED, TRANSITION_LOW));
		knowledgeBits.put(SIMPLIFY_DIFFERENT_FACTORS, new KnowledgeComponent(
				"Combine a times and over block", L0_LOW, SLIP_MED,
				GUESS_MED, TRANSITION_LOW));
		knowledgeBits.put(SIMPLIFY_LIKE_FACTORS, new KnowledgeComponent(
				"Combine two times or over blocks", L0_LOW, SLIP_MED,
				GUESS_MED, TRANSITION_LOW));
		knowledgeBits.put(CANCEL_ADDENS, new KnowledgeComponent(
				"Cancel two negating addens", L0_LOW, SLIP_MED,
				GUESS_MED, TRANSITION_LOW));
		knowledgeBits.put(CANCEL_FACTORS, new KnowledgeComponent(
				"Cancel two negating factors", L0_LOW, SLIP_LOW,
				GUESS_LOW, TRANSITION_LOW));

		knowledgeBits.put(DISTRIBUTION, new KnowledgeComponent("Distribution",
				L0_LOW, SLIP_LOW, GUESS_LOW, TRANSITION_MED));
		knowledgeBits.put(COMBINATION, new KnowledgeComponent("Combination",
				L0_LOW, SLIP_LOW, GUESS_LOW, TRANSITION_LOW));

		knowledgeBits.put(BUILDING_SYMBOLIC_EQUATIONS, new KnowledgeComponent(
				"Building Symbolic Equations", L0_MED, SLIP_LOW, GUESS_LOW,
				TRANSITION_MED));
		knowledgeBits.put(BUILDING_WRITTEN_EQUATIONS, new KnowledgeComponent(
				"Building Written Equations", L0_MED, SLIP_LOW, GUESS_LOW,
				TRANSITION_MED));
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

	@Override
	public void onActionPerformed(SolveAction action, Equation before) {	
		if (action instanceof StartProblemAction) {		
			currentEquations.clear();
			currentStudentActions.clear();
		} else {
			currentEquations.add(before.copy());
			currentStudentActions.add(action);
		}
		
		if (action instanceof FinishProblemAction) {
			updateModel();
			currentStudentActions.clear();
			currentEquations.clear();
		}
		
	}

	private void updateModel() {
		if (currentEquations.size() < 1) return;
	
		System.out.println("Student Model Update");
		System.out.println("====================");
		
		int resets = 0;
		int[] hints = new int[HintLevel.values().length];
		boolean success = false;
		for (int i = 0; i < currentEquations.size(); i++) {
				SolveAction action = currentStudentActions.get(i);
				SolveAction lastAction = i == 0 ? null : currentStudentActions.get(i - 1);

				// update arithmetic 
				if (action instanceof FinishSimplifyAction) {
					if (lastAction instanceof StartSimplifyAction) {
						updateArithmeticModel((StartSimplifyAction) lastAction, (FinishSimplifyAction) action);
					}
				}
				
				// count resets
				if (action instanceof StartProblemAction && i > 0) {
					resets++;
				}
				
				
				// count hints
				if (action instanceof HintAction) {
					if (!(lastAction instanceof HintAction &&
							((HintAction) action).level == ((HintAction) lastAction).level)) {
						hints[((HintAction) action).level]++;
					}
				}
				
				// check success
				if (action instanceof FinishProblemAction) {
					success = action.success;
				}
		}
		
		int extraSteps = getSolveExcess();
		
		System.out.println("Resets: " + resets);
		System.out.println("Hints: " + Arrays.toString(hints));
		System.out.println("Extra Solve Steps: " + getSolveExcess());
		System.out.println();
		
		int rating = 0;
		if (success) rating++;
		for (int i = 0; i < hints.length; i++) {
			if (i < 2) {
				if (hints[i] > 1) rating--;
			} else {
				if (hints[i] > 0) rating--;
			}
		}
		if (extraSteps > 1) rating--;
		if (resets > 0) rating--;
		else if (extraSteps <= 0) rating++;
		
		int meaningfulSteps = getNumMeaningfulSteps(currentStudentActions, true);
		double rawUpdates = rating * meaningfulSteps / 6.0f;
		int updates = (int) (rawUpdates > 0 ? Math.ceil(rawUpdates) : Math.floor(rawUpdates));
		if (updates > 2) updates = 2;
		if (updates < -2) updates = -2;
		
		System.out.println("Rating: " + rating);
		System.out.println("Meaningful Steps: " + meaningfulSteps);
		System.out.println("Updates: " + updates);
	}
	
	private int getSolveExcess() {
		int studentSteps = getNumMeaningfulSteps(currentStudentActions, true);
		if (studentSteps == 0) return 0;
		
		Equation start = currentEquations.get(0);
		List<Step> aStar = IdealEquationSolver.aStar(start, Tutor.MAX_HINT_ITERATIONS);
		if (aStar == null) return 0;
		
		Equation lastStudentEquation = currentEquations.get(currentEquations.size() - 1);
		Equation lastIdealEquation = aStar.get(aStar.size() - 1).result;
		
		boolean studentSolved = EquationManipulator.isEquationSolved(lastStudentEquation);
		boolean idealSolved = EquationManipulator.isEquationSolved(lastIdealEquation);
		
		int idealSteps = getNumSteps(aStar);
		double heuristicOverweight = 1.5;
		double heuristicEsitmate = idealSteps + 
				IdealEquationSolver.heuristic(lastIdealEquation) * heuristicOverweight;
		
//		System.out.printf("%d vs %d, %s vs %s, %.02f\n", studentSteps, idealSteps, "" + studentSolved,
//				"" + idealSolved, heuristicEsitmate);
//		debugList(aStar);
		
		if (studentSolved) {
			if (idealSolved) {
				return studentSteps - idealSteps;
			} else {
				return studentSteps - (int) heuristicEsitmate;
			}
		} else {
			List<Step> aStarAfter = IdealEquationSolver.aStar(lastStudentEquation, 
				Tutor.MAX_HINT_ITERATIONS);
			if (aStarAfter == null) return studentSteps;
			int idealStepsAfter = getNumSteps(aStarAfter);
			
//			System.out.println("idealAfter: " + idealStepsAfter);
//			debugList(aStarAfter);
			
			if (idealSolved) {	
				return idealStepsAfter + studentSteps - idealSteps;
			} else {
				if (aStarAfter.size() == 0) return 0;
				Equation lastIdealEquationAfter = aStarAfter.get(aStarAfter.size() - 1).result;
				double heuristicAfter = idealStepsAfter + 
						IdealEquationSolver.heuristic(lastIdealEquationAfter) * heuristicOverweight;
				
				return studentSteps + (int) (heuristicAfter - heuristicEsitmate);
			}
		}
	}

	@SuppressWarnings("unused")
	private void debugList(List<Step> aStar) {
		List<List<String>> s = new ArrayList<List<String>>();
		for (Step st : aStar) {
			List<String> ss = new ArrayList<String>();
			for (SolveAction action : st.actions) ss.add(action.name());
			s.add(ss);
		}
		System.out.println(s);
	}
	
	private static int getNumSteps(List<Step> steps) {
		int numSteps = 0;
		for (Step step : steps) {
			numSteps += getNumMeaningfulSteps(step.actions, false);
		}
		return numSteps;
	}
	
	private static int getNumMeaningfulSteps(List<SolveAction> actions, boolean inOrder) {
		int steps = 0;
		boolean lastStartSimplify = false;
		for (SolveAction action : actions) {
			if (action.success && (
					action instanceof DragAction || 
					action instanceof ReciprocalAction ||
					action instanceof FinishSimplifyAction)) {
				
				if (!(action instanceof FinishSimplifyAction && inOrder && !lastStartSimplify)) {
					steps++;
				}
			}
			lastStartSimplify = action instanceof StartSimplifyAction;
		}
		return steps;
	}

	private void updateArithmeticModel(StartSimplifyAction startSimplifying,
			FinishSimplifyAction finishSimplifying) {
		int mistakes = finishSimplifying.mistakes;
		if (mistakes == FinishSimplifyAction.AUTO_SIMPLIFY) return;
		
		boolean success = finishSimplifying.success;
		int power = success ? 1 : 0;
		
		if (mistakes > 0) power--;
		if (mistakes > 2) power--;
		
		if (power != 0) {
			for (Object tag : startSimplifying.tags()) {
				double p = knowledgeBits.get(tag).probLearned();
				updateKC((ActionType) tag, power > 0, Math.abs(power));
				Debug.write("Update: %s %s", tag, power);
				Debug.write("Prob: %s->%s", p, knowledgeBits.get(tag).probLearned());
			}
		}
	}
	
	private void updateKC(ActionType type, boolean success, int power) {
		KnowledgeComponent kc = knowledgeBits.get(type);
		for (int i = 0; i < power; i++) {
			kc.studentAnswered(success);
		}
	}

	@Override
	public void addFinishSimplifyTags(FinishSimplifyAction action,
			Block base, ModifierBlock pair, ModifierGroup modifiers) {
		if (base instanceof VerticalModifierBlock && 
				pair instanceof VerticalModifierBlock) {
			if (base instanceof TimesBlock != pair instanceof TimesBlock) {
				if (((ModifierBlock) base).value() == ((ModifierBlock) pair).value()) {
					action.addTag(CANCEL_FACTORS);
				} else {
					action.addTag(SIMPLIFY_DIFFERENT_FACTORS);
				}
			} else {
				action.addTag(SIMPLIFY_LIKE_FACTORS);
			}
		} else if (base instanceof HorizontalModifierBlock &&
				pair instanceof HorizontalModifierBlock) {
			if (((ModifierBlock) base).value() == ((ModifierBlock) pair).value()) {
				action.addTag(CANCEL_ADDENS);
			} else {
				action.addTag(SIMPLIFY_ADDENS);
			}
		}
		if (base instanceof NumberBlock) {
			if (pair instanceof PlusBlock || pair instanceof MinusBlock) {
				action.addTag(SIMPLIFY_ADDENS);
			} else if (pair instanceof TimesBlock) {
				action.addTag(SIMPLIFY_LIKE_FACTORS);
			} else if (pair instanceof OverBlock) {
				action.addTag(SIMPLIFY_DIFFERENT_FACTORS);
			}
		}
	}

	@Override
	public void addStartSimplifyTags(StartSimplifyAction action,
			Renderer problem, int answer, Stat stat, int level) {
		
		ActionType algebraTag = null;
		switch (stat) {
		case Plus: 
			algebraTag = ADD_INTEGERS;
			break;
		case Minus: 
			algebraTag = SUBTRACT_INTEGERS;
			break;
		case Times: 
			if (level < 3) algebraTag = MULTIPLY_INTEGERS_LOW;
			else if (level < 5) algebraTag = MULTIPLY_INTEGERS_MED;
			else algebraTag = MULTIPLY_INTEGERS_HIGH;
			break;
		case Over:
			if (level < 3) algebraTag = DIVIDE_INTEGERS_LOW;
			else if (level < 5) algebraTag = DIVIDE_INTEGERS_MED;
			else algebraTag = DIVIDE_INTEGERS_HIGH;
			break;
		}
		
		action.addTag(algebraTag);
	}

	@Override
	public void addReciprocalActionTags(ReciprocalAction action, Block block) {
		if (block instanceof TimesBlock) {
			action.addTag(MULTIPLY_MULTIPLE_SIDES);
		} else {
			action.addTag(DIVIDE_MULTIPLE_SIDES);
		}
	}

	@Override
	public void addDragActionTags(DragAction action, Equation before) {
		boolean fromLeft = action.fromIndex.expressionIndex < before.leftCount();
		boolean toLeft = action.toIndex < before.leftCount();
		
		Block dragging = before.getBlock(action.fromIndex);
		BaseBlock draggingTo = before.getBaseBlock(action.toIndex);

		if (dragging instanceof HorizontalModifierBlock) {
			ModifierGroup group = ((HorizontalModifierBlock) dragging).group();
			if (group != null && group.isModifiedVertically()) {
				action.addTag(DISTRIBUTION);
				if (!(draggingTo instanceof BlockHolder)) {
					action.addTag(COMBINATION);
				}
			}
		}
		
		if (draggingTo instanceof BlockHolder && fromLeft == toLeft) {
			//TODO: consider a KC for moving stuff around?
			return;
		}
		
		// all of these are counter-intuitive, but remember
		// if you're dragging a PlusBlock, you're really
		// subtracting both sides by its value
		if (dragging instanceof PlusBlock) {
			action.addTag(SUBTRACT_EQUATION_SIDES);
		} else if (dragging instanceof MinusBlock) {
			action.addTag(ADD_EQUATION_SIDES);
		} else if (dragging instanceof TimesBlock) {
			action.addTag(DIVIDE_SINGLE_SIDE);
		} else if (dragging instanceof OverBlock) {
			action.addTag(MULTIPLY_SINGLE_SIDE);
		} else if (dragging instanceof NumberBlock) {
			if (((NumberBlock) dragging).value() > 0) {
				action.addTag(SUBTRACT_EQUATION_SIDES);
			} else {
				action.addTag(ADD_EQUATION_SIDES);
			}
			if (((NumberBlock) dragging).isModifiedVertically()) {
				action.addTag(COMBINATION);
			}
		} else if (dragging instanceof VariableBlock) {
			if (draggingTo instanceof VariableBlock) {
				action.addTag(COMBINE_UNKNOWNS);
			}
		}
	}

}
