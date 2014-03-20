package tuxkids.tuxblocks.core.tutor;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.Block;
import tuxkids.tuxblocks.core.solve.blocks.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationManipulator;
import tuxkids.tuxblocks.core.solve.blocks.EquationManipulatorSolver;
import tuxkids.tuxblocks.core.solve.blocks.MutableEquation;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.OverBlock;
import tuxkids.tuxblocks.core.solve.blocks.TimesBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.student.StudentAction;

public class IdealEquationSolver {

	private static Comparator<List<Step>> comparator = new Comparator<List<Step>>() {
		@Override
		public int compare(List<Step> o1, List<Step> o2) {
			Equation eq1 = o1.get(o1.size() - 1).result;
			Equation eq2 = o2.get(o2.size() - 1).result;
			// classic A* - compare based on f(x) + h(x)
			return Double.compare(heuristic(eq1) + o1.size(), heuristic(eq2)
					+ o2.size());
		}
	};

	public SolutionPackage getIdealSolution(MutableEquation e) {
		return null;
	}

	public List<Step> aStar(Equation start) {
		// queue of paths to solution, sorted using the heuristic
		PriorityQueue<List<Step>> paths = new PriorityQueue<List<Step>>(20,
				comparator);

		// add an empty start path
		List<Step> startPath = new ArrayList<Step>();
		startPath.add(new Step(start));
		paths.add(startPath);

		// a map of the shortest path lengths to a given node
		HashMap<String, Integer> discoveredNodes = new HashMap<String, Integer>();

		while (paths.size() > 0) {
//			seeAllAndHeuristics(paths);// use this to debug A*'s expansion
										// pattern

			List<Step> toExpand = paths.poll(); // get the best estimated path
			Step last = toExpand.get(toExpand.size() - 1); // get the last state of the equation

			// check if we've gotten here before by a shorter path
//			if (!registerNode(last, discoveredNodes, toExpand.size())) {
//				continue;
//			}

			// break if we win
			if (EquationManipulator.isEquationSolved(last.result)) {
				return toExpand;
			}

			// get all possible nodes reachable from this node
			List<Step> branches = expandState(last.result);
			for (Step step : branches) {
				if (!registerNode(step, discoveredNodes, toExpand.size() + 1)) {
					continue;
				}
				
				// add them all as children, branching from the original path
				List<Step> nPath = new ArrayList<Step>(toExpand);
				nPath.add(step);
				paths.add(nPath);
			}
		}

		// this will almost certainly never happen...
		// but if it does, the equation is unsolvable
		return null;
	}
	
	private boolean registerNode(Step currentStep, HashMap<String, Integer> discoveredNodes, int pathLength) {

		// hash it using its text function... TODO: use a quicker/more
		// accurate hash
		String text = currentStep.equationString();
		Integer lastPathLength = discoveredNodes.get(text);
		// if we've already gotten here by a shorter path, don't expand this node
		if (lastPathLength != null && lastPathLength <= pathLength) {
			return false;
		}
		discoveredNodes.put(text, pathLength);
		return true;
	}

	// for debugging paths
	public static String pathToString(List<Step> path) {
		StringBuilder sb = new StringBuilder();
		for (int i = path.size() - 1; i >= 0; i--) {
			if (i < path.size() - 1)
				sb.append(" <- ");
			Equation eq = path.get(i).result;
			sb.append(eq.getPlainText());
			sb.append(" {");
			sb.append(heuristic(eq));
			sb.append("/");
			sb.append(i);
			sb.append("}");
		}
		return sb.toString();
	}

	public static void seeAllAndHeuristics(Iterable<List<Step>> paths) {
		for (List<Step> path : paths) {
			System.out.println("\t" + pathToString(path));
		}
		System.out.println();
	}

	public static double heuristic(Equation eq) {
		// the basic plan right now is to add 1 for every expression,
		// 1 for every modifier of a variable and 0.75 for every modifier
		// of a number

		// the ideal here is that the heuristic be consistent, meaning
		// essentially
		// that performing a step can never decrease h(x) by more than 1 and
		// that
		// h(x) <= f(x), where f(x) is the actual number of steps required to
		// solve

		double score = 0;
		// System.out.println(eq.getPlainText())
		
		int generalLeftTerms = countGeneralTerms(eq.leftSide());
		int generalRightTerms = countGeneralTerms(eq.rightSide());
		int leftVarTerms = countVarTerms(eq.leftSide());
		int rightVarTerms = countVarTerms(eq.rightSide());
		
		//int totalTerms = generalLeftTerms+generalRightTerms;
		//int totalVarTerms = leftVarTerms + rightVarTerms;
		
		for (BaseBlock bb : eq.leftSide()) {
			if (bb instanceof BlockHolder) continue;
			List<Block> attachedBlockList = bb.getAllBlocks();
			Collections.reverse(attachedBlockList);
			if (bb instanceof VariableBlock)
			{
				for(int i = 0;i<attachedBlockList.size() - 1; i++) {
					Block block = attachedBlockList.get(i);
					if (i == 0) {
						if (block instanceof TimesBlock || block instanceof OverBlock)
							score += (generalRightTerms == 0? leftVarTerms:leftVarTerms+generalRightTerms);
						else
							score += (generalRightTerms == 0? 1:2);
					}
					else {
						if (block instanceof TimesBlock || block instanceof OverBlock)
							score += leftVarTerms+generalRightTerms;
						else
							score += 2;
					}
				}
			}
			else {
				score += attachedBlockList.size() - 1;
				score += (rightVarTerms == 0? 1: 0);
			}
		}
		
		for (BaseBlock bb : eq.rightSide()) {
			if (bb instanceof BlockHolder) continue;
			List<Block> attachedBlockList = bb.getAllBlocks();
			Collections.reverse(attachedBlockList);
			if (bb instanceof VariableBlock)
			{
				for(int i = 0;i<attachedBlockList.size() - 1; i++) {
					Block block = attachedBlockList.get(i);
					if (i == 0) {
						if (block instanceof TimesBlock || block instanceof OverBlock)
							score += (generalLeftTerms == 0? rightVarTerms:rightVarTerms+generalLeftTerms);
						else
							score += (generalLeftTerms == 0? 1:2);
					}
					else {
						if (block instanceof TimesBlock || block instanceof OverBlock)
							score += rightVarTerms+generalLeftTerms;
						else
							score += 2;
					}
				}
			}
			else {
				score += attachedBlockList.size() - 1;
				score += (leftVarTerms == 0? 1: 0);
			}
		}
		

		return score;
	}


	private static int countVarTerms(Iterable<BaseBlock> side) {
		int terms = 0;

		for (BaseBlock bb : side)
		{
			if (bb instanceof VariableBlock) terms++;
		}
		return terms;
	}

	private static int countGeneralTerms(Iterable<BaseBlock> side) {
		int terms = 0;

		for (BaseBlock bb : side)
		{
			if (!(bb instanceof BlockHolder)) terms++;
		}
		return terms;
	}

	// returns a list of all steps that can be taken for the given equation
	private List<Step> expandState(Equation state) {
		List<Step> steps = new ArrayList<Step>();
		EquationManipulatorSolver solver = new EquationManipulatorSolver(state);
		for (SolveAction action : solver.getAllActions()) {
			solver.push();
			List<SolveAction> actions = solver.performSolveAction(action);
			Step step = new Step(solver.equation());
			step.actions.add(action);
			if (actions != null)
				step.actions.addAll(actions);
			steps.add(step);
			solver.pop();
		}
		return steps;
	}

	public static class Step {
		// sometimes steps have multiple actions associated with them, such as
		// starting and ending a simplification (also the start step has no
		// actions)
		public final List<SolveAction> actions = new ArrayList<SolveAction>();
		public final Equation result;
		private final String equationString;

		public Step(Equation result) {
			this.result = result;
			this.equationString = result.getPlainText();
		}

		public String equationString() {
			return equationString;
		}
		
		public boolean validate(Equation originalEquation) {
			return originalEquation.checkAnswer(getAnswer());
		}
		
		public int getAnswer() {
			if (EquationManipulator.isEquationSolved(result)) {
				for (BaseBlock block : result.allBlocks()) {
					if (block instanceof NumberBlock) {
						return ((NumberBlock) block).value();
					}
				}
			}
			throw new RuntimeException("Equation is not simplified");
		}
	}

	public static class SolutionPackage {
		int numSteps;
		List<StudentAction> solutionOrientedActions;
	}

}
