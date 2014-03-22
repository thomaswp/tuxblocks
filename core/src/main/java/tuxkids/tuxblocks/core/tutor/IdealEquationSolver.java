package tuxkids.tuxblocks.core.tutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.Block;
import tuxkids.tuxblocks.core.solve.blocks.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationManipulator;
import tuxkids.tuxblocks.core.solve.blocks.EquationManipulatorSolver;
import tuxkids.tuxblocks.core.solve.blocks.MinusBlock;
import tuxkids.tuxblocks.core.solve.blocks.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.MutableEquation;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.OverBlock;
import tuxkids.tuxblocks.core.solve.blocks.PlusBlock;
import tuxkids.tuxblocks.core.solve.blocks.TimesBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.student.StudentAction;

public class IdealEquationSolver {

	private static final int MAX_TERMS_PER_SIDE = 7;

	private static boolean debugHeuristic = false;

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
			//	seeAllAndHeuristics(paths);
			List<Step> toExpand = paths.poll(); // get the best estimated path
			Step last = toExpand.get(toExpand.size() - 1); // get the last state of the equation



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
		debugHeuristic = true;
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
			debugHeuristic = false;
		}
		return sb.toString();
	}

	public static void seeAllAndHeuristics(Iterable<List<Step>> paths) {
		List<List<Step>> reversablePaths = new ArrayList<List<Step>>();

		for (List<Step> path : paths) {
			reversablePaths.add(path);
		}

		Collections.reverse(reversablePaths);



		for (List<Step> path : reversablePaths) {
			System.out.println("\t" + pathToString(path));
		}

		debugHeuristic = false;

		System.out.println();
	}


	private static class HeuristicTermPackage {
		BaseBlock term = null;
		boolean ignore = true;
		boolean hasBeenHandled = false;
		double termsScore = 0.0;

		HeuristicTermPackage reset() {
			ignore = true;
			hasBeenHandled = false;
			termsScore = 0.0;
			term = null;
			return this;
		}

		@Override
		public String toString() {
			if (ignore) return "";
			if (!hasBeenHandled) return String.format("[%1.2f?]", termsScore);
			return String.format("[%1.2f]", termsScore);
		}
	}

	private static List<HeuristicTermPackage> leftSideTerms = new ArrayList<IdealEquationSolver.HeuristicTermPackage>(MAX_TERMS_PER_SIDE);
	private static List<HeuristicTermPackage> rightSideTerms = new ArrayList<IdealEquationSolver.HeuristicTermPackage>(MAX_TERMS_PER_SIDE);

	static {
		for(int i =0;i<MAX_TERMS_PER_SIDE;i++) {
			leftSideTerms.add(new HeuristicTermPackage());
			rightSideTerms.add(new HeuristicTermPackage());
		}
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


		Iterator<BaseBlock> leftSideIterator = eq.leftSide().iterator();
		Iterator<BaseBlock> rightSideIterator = eq.rightSide().iterator();

		int generalLeftTerms = 0;
		int generalRightTerms = 0;
		int leftVarTerms = 0;
		int rightVarTerms = 0;

		//Dump the terms into our lists and perform some counts of the various terms
		for(int i =0;i<MAX_TERMS_PER_SIDE;i++) {
			HeuristicTermPackage leftTerm = leftSideTerms.get(i).reset();
			HeuristicTermPackage rightTerm = rightSideTerms.get(i).reset();

			if (leftSideIterator.hasNext()) {
				leftTerm.ignore = false;
				leftTerm.term = leftSideIterator.next();
				if (!(leftTerm.term instanceof BlockHolder)) generalLeftTerms++;
				if (leftTerm.term instanceof VariableBlock) leftVarTerms++;
			}
			if (rightSideIterator.hasNext()) {
				rightTerm.ignore = false;
				rightTerm.term = rightSideIterator.next();
				if (!(rightTerm.term instanceof BlockHolder)) generalRightTerms++;
				if (rightTerm.term instanceof VariableBlock) rightVarTerms++;
			}
		}

		//discourage emptying out one side of the equation
		if (generalRightTerms == 0 || generalLeftTerms == 0) {
			score+=.1;
		}

		double leftSideCombinationScore = handleCombinableTerms(leftSideTerms);
		score += leftSideCombinationScore;
		double rightSideCombinationScore = handleCombinableTerms(rightSideTerms);
		score += rightSideCombinationScore;

		if (debugHeuristic) System.out.printf("\t\tLSCS=%1.2f RSCS=%1.2f",leftSideCombinationScore,rightSideCombinationScore);

		handleComplicatedTerms(leftSideTerms, generalRightTerms, leftVarTerms, rightVarTerms);
		handleComplicatedTerms(rightSideTerms, generalLeftTerms, rightVarTerms, leftVarTerms);

		if (debugHeuristic){
			for(int i = 0;i<MAX_TERMS_PER_SIDE;i++) {
				if (leftSideTerms.get(i).ignore) continue;
				System.out.print(leftSideTerms.get(i)+" + ");
			}
			System.out.print(" = ");
			for(int i = 0;i<MAX_TERMS_PER_SIDE;i++) {
				if (rightSideTerms.get(i).ignore) continue;
				System.out.print(rightSideTerms.get(i)+" + ");
			}
		}

		if (debugHeuristic) System.out.printf("\tgL = %d, gR = %d, vL = %d, vR = %d%n",generalLeftTerms,generalRightTerms,
				leftVarTerms, rightVarTerms);

		for(int i = 0;i<MAX_TERMS_PER_SIDE;i++) {
			if (!leftSideTerms.get(i).ignore) score += leftSideTerms.get(i).termsScore;
			if (!rightSideTerms.get(i).ignore) score += rightSideTerms.get(i).termsScore;
		}

		return score;

		/*for (BaseBlock bb : eq.leftSide()) {

			double previousScore = score;

			if (bb instanceof BlockHolder) continue;
			List<Block> attachedBlockList = bb.getAllBlocks();
			Collections.reverse(attachedBlockList);

			if (bb instanceof VariableBlock)
			{
				//Iterate through everything attached to this block
				for(int i = 0;i<attachedBlockList.size()-1; i++) {
					Block block = attachedBlockList.get(i);

					if (block instanceof TimesBlock || block instanceof OverBlock) {
						Block nextBlock = attachedBlockList.get(i+1);

						if (mightDivideOut(block, nextBlock))
						{
							score = adjustScoreForDividingOut(score, block, nextBlock);
							//we essentially got two steps at once
							i++;
						} else {
							//Because we'll have to either multiply or divide to remove this term
							//one step for every variable on this side and every term on the other
							//(may need to be total terms)
							score += leftVarTerms+generalRightTerms;

							// dividing/multiplying out with a plus or minus block can't happen yet
							if (nextBlock instanceof PlusBlock || nextBlock instanceof MinusBlock) {
								score++;
							}
						}
					}
					else {		//if addition or subtraction
						score += 2;//(generalRightTerms == 0 && i == 0 ? 1:2);
					}
				}
			}
			else {
				//We will only have to simplify these out, so this is just one step
				//for every thing attached to the number block
				score += attachedBlockList.size() - 1;
				score += (rightVarTerms == 0? 1: 0);
			}
			//if (debugHeuristic) System.out.printf("%s[%1.1f] ", (isFirst?"":"+ "),score-previousScore);

		}

		//if (debugHeuristic) System.out.print(" = ");

		for (BaseBlock bb : eq.rightSide()) {
			double previousScore = score;
			if (bb instanceof BlockHolder) continue;
			List<Block> attachedBlockList = bb.getAllBlocks();
			Collections.reverse(attachedBlockList);
			if (bb instanceof VariableBlock)
			{
				//Iterate through everything attached to this block
				for(int i = 0;i<attachedBlockList.size() - 1; i++) {
					Block block = attachedBlockList.get(i);

					if (block instanceof TimesBlock || block instanceof OverBlock) {
						Block nextBlock = attachedBlockList.get(i+1);

						if (mightDivideOut(block, nextBlock))
						{
							score = adjustScoreForDividingOut(score, block, nextBlock);
							//we essentially got two steps at once
							i++;
						} else {
							//Because we'll have to either multiply or divide to remove this term
							//one step for every variable on this side and every term on the other
							//(may need to be total terms)
							score += rightVarTerms+generalLeftTerms;

							// dividing/multiplying out with a plus or minus block can't happen yet
							if (nextBlock instanceof PlusBlock || nextBlock instanceof MinusBlock) {
								score++;
							}
						}
					}
					else
						score += 2;// (generalLeftTerms == 0 && i == 0 ? 1:2);
				}

			}
			else {
				//We will only have to simplify these out, so this is just one step
				//for every thing attached to the number block
				score += attachedBlockList.size() - 1;
				score += (leftVarTerms == 0? 1: 0);
			}



			//if (debugHeuristic) System.out.printf("%s[%1.1f] ", (isFirst?"":"+ "),score-previousScore);

		}*/


	}

	private static void handleComplicatedTerms(List<HeuristicTermPackage> terms, int generalOtherSideTerms,
			int thisSideVarTerms, int otherSideVarTerms) {

		for (HeuristicTermPackage thisTerm : terms) {
			if (thisTerm.ignore) break;
			if (thisTerm.hasBeenHandled ) continue;
			thisTerm.hasBeenHandled = true;

			if (thisTerm.term instanceof BlockHolder) continue;
			List<Block> attachedBlockList = thisTerm.term.getAllBlocks();
			Collections.reverse(attachedBlockList);

			if (thisTerm.term instanceof VariableBlock)
			{
				//Iterate through everything attached to this block
				for(int i = 0;i<attachedBlockList.size()-1; i++) {
					Block block = attachedBlockList.get(i);

					if (block instanceof TimesBlock || block instanceof OverBlock) {
						Block nextBlock = attachedBlockList.get(i+1);

						if (mightDivideOut(block, nextBlock))
						{
							thisTerm.termsScore += adjustScoreForDividingOut(block, nextBlock);
							//we essentially got two steps at once
							i++;
						} else {
							//Because we'll have to either multiply or divide to remove this term
							//one step for every variable on this side and every term on the other
							//(may need to be total terms)
							thisTerm.termsScore += thisSideVarTerms+generalOtherSideTerms;

							// dividing/multiplying out with a plus or minus block can't happen yet
							if (nextBlock instanceof PlusBlock || nextBlock instanceof MinusBlock) {
								thisTerm.termsScore++;
							}
						}
					}
					else {		//if addition or subtraction
						thisTerm.termsScore += 2;//(generalRightTerms == 0 && i == 0 ? 1:2);
					}
				}
			}
			else {
				//We will only have to simplify these out, so this is just one step
				//for every thing attached to the number block
				thisTerm.termsScore += attachedBlockList.size() - 1;
				thisTerm.termsScore += (otherSideVarTerms == 0? 1: 0);
			}

		}

	}
	
	private static List<Integer> timeses = new ArrayList<Integer>(MAX_TERMS_PER_SIDE);
	private static Set<HeuristicTermPackage> potentialTimesHandled = new HashSet<IdealEquationSolver.HeuristicTermPackage>();
	private static Set<HeuristicTermPackage> potentialNumbersHandled = new HashSet<IdealEquationSolver.HeuristicTermPackage>();

	
	private static double handleCombinableTerms(List<HeuristicTermPackage> side) {
		double score = 0;
		//int combinableTimesBlocks = 0;
		//int combinableNumberBlocks = 0;
		
		potentialNumbersHandled.clear();
		potentialTimesHandled.clear();
		timeses.clear();
		
		
		
		

		for(int i = 0;i< side.size();i++)	{
			HeuristicTermPackage thisTerm = side.get(i);
			if ( thisTerm.ignore) break;
			if (thisTerm.hasBeenHandled) continue;
			BaseBlock thisBlock = thisTerm.term;

			//if the block and 
			List<Block> attachedBlocks = thisBlock.getAllBlocks();
			if (thisBlock instanceof VariableBlock && attachedBlocks.size()<=2)
			{
				if (attachedBlocks.size() == 1 || attachedBlocks.get(1) instanceof TimesBlock) {
					potentialTimesHandled.add(thisTerm);
					if (attachedBlocks.size() == 1) {
						timeses.add(1);
					} else {
						timeses.add(((ModifierBlock) attachedBlocks.get(1)).value());
					}

				} 
			} else if (thisBlock instanceof NumberBlock && attachedBlocks.size() == 1){
				potentialNumbersHandled.add(thisTerm);
			}
		}

		//one move per n-1 blocks to move together and one move per n-1 blocks to combine numbers
		//e.g. [3]+[3]+[3] would be resolved in 4 moves
		//[3+3]+[3]+[]
		//[6]+ [3] +[]
		//[6+3] + [] + []
		//[9] + [] + []
		if (potentialNumbersHandled.size() > 1) {
			score += (potentialNumbersHandled.size()-1) * 2;
			for (HeuristicTermPackage h:potentialNumbersHandled) h.hasBeenHandled = true;
		} 


		if (potentialTimesHandled.size() > 1) {
			//one move per n-1 blocks to move together (combine happens automatically or instantaneously)
			score += potentialTimesHandled.size() - 1;

			//if the end sum is not 1 or 0, we will have to divide as well
			int sum = sumList(timeses);
			if (!(sum == 1 || sum ==0 )) score++;		//don't need to divide in the end if the num variables sums to 1 or 0
		
			for (HeuristicTermPackage h:potentialTimesHandled) h.hasBeenHandled = true;
		} 

		return score;
	}

	private static int sumList(List<Integer> timeses) {
		int sum = 0;
		for(Integer i: timeses) sum+=i;
		return sum;
	}

	private static double adjustScoreForDividingOut(Block block, Block nextBlock) {
		double score = 0;
		ModifierBlock mBlock = (ModifierBlock)block;
		ModifierBlock mNextBlock = (ModifierBlock) nextBlock;
		if (mBlock.value() == mNextBlock.value()) {
			score += 1;
		} else if (mBlock.value() == -mNextBlock.value()){
			score += 2;
		} else {
			score += 2;	//may be 2 or 3
		}
		return score;
	}

	private static boolean mightDivideOut(Block block, Block nextBlock) {
		return (block instanceof TimesBlock && nextBlock instanceof OverBlock) ||
				(block instanceof OverBlock && nextBlock instanceof TimesBlock);
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
				return 0;
			}
			throw new RuntimeException("Equation is not simplified: " + result.getPlainText());
		}
	}

	public static class SolutionPackage {
		int numSteps;
		List<StudentAction> solutionOrientedActions;
	}

}
