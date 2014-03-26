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

	public static boolean debugHeuristic = false;

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
			seeAllAndHeuristics(paths);
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
		boolean isEventualCombine = false;
		private double pendingScore;

		HeuristicTermPackage reset() {
			ignore = true;
			hasBeenHandled = false;
			termsScore = 0.0;
			term = null;
			isEventualCombine = false;
			pendingScore = 0;
			return this;
		}

		@Override
		public String toString() {
			if (ignore) return "";
			if (!hasBeenHandled) return String.format("[%1.2f+%1.2f?]%s", termsScore,pendingScore, isEventualCombine?"`":"");
			return String.format("[%1.2f+%1.2f]%s", termsScore,pendingScore, isEventualCombine?"`":"");
		}

		public void queueUpScore(double d) {
			this.pendingScore += d;
		}
		
		public HeuristicTermPackage finalizeScore() {
			this.termsScore += pendingScore;
			this.pendingScore = 0;
			return this;
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

		// the ideal here is that the heuristic be consistent, meaning essentially
		// that performing a step can never decrease h(x) by more than 1 and that
		// h(x) <= f(x), where f(x) is the actual number of steps required to solve

		double score = 0;

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
		
		if (leftVarTerms >0 && rightVarTerms > 0) {
			score += .5;
		}

		double leftSideEasyCombinationScore = handleEasilyCombinableTerms(leftSideTerms);
		score += leftSideEasyCombinationScore;
		double rightSideEasyCombinationScore = handleEasilyCombinableTerms(rightSideTerms);
		score += rightSideEasyCombinationScore;
		
		double leftSideEventualCombinationScore = accountForEventualCombinableTerms(leftSideTerms);
		score += leftSideEventualCombinationScore;
		double rightSideEventualCombinationScore = accountForEventualCombinableTerms(rightSideTerms);
		score += rightSideEventualCombinationScore;

		if (debugHeuristic) System.out.printf("\t\tLSECS=%1.2f LSDCS=%1.2f | RSECS=%1.2f RSDCS=%1.2f ",leftSideEasyCombinationScore, leftSideEventualCombinationScore, rightSideEasyCombinationScore, rightSideEventualCombinationScore);

		//handleComplicatedTerms(leftSideTerms, generalLeftTerms, generalRightTerms, leftVarTerms, rightVarTerms);
		//handleComplicatedTerms(rightSideTerms, generalRightTerms, generalLeftTerms, rightVarTerms, leftVarTerms);

		handleIsolatedTerms(leftSideTerms);
		handleIsolatedTerms(rightSideTerms);
		
		handleDependentTerms(leftSideTerms, rightSideTerms);
		handleDependentTerms(rightSideTerms, leftSideTerms);
		
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

		if (debugHeuristic) {
			System.out.printf("\tgL = %d, gR = %d, vL = %d, vR = %d%n",generalLeftTerms,generalRightTerms,leftVarTerms, rightVarTerms);
		}
				

		for(int i = 0;i<MAX_TERMS_PER_SIDE;i++) {
			if (!leftSideTerms.get(i).ignore) score += leftSideTerms.get(i).finalizeScore().termsScore;
			if (!rightSideTerms.get(i).ignore) score += rightSideTerms.get(i).finalizeScore().termsScore;
		}

		if (score <= 0)
			return 0;
		return score;

}

	private static void handleDependentTerms(List<HeuristicTermPackage> theseTerms, List<HeuristicTermPackage> otherTerms) {
		for (HeuristicTermPackage thisTerm : theseTerms) {
			if (thisTerm.ignore) break;
			if (thisTerm.hasBeenHandled ) continue;
			thisTerm.hasBeenHandled = true;
			if (thisTerm.term instanceof BlockHolder) continue;
			List<Block> attachedBlockList = thisTerm.term.getAllBlocks();
			Collections.reverse(attachedBlockList);

			if (thisTerm.term instanceof VariableBlock && attachedBlockList.size() >= 2) //ignore solo blocks
			{
				for(int i = 0;i<attachedBlockList.size()-1; i++) {
					Block block = attachedBlockList.get(i);

					if (block instanceof TimesBlock || block instanceof OverBlock) {
						Block nextBlock = attachedBlockList.get(i+1);

						if (!(doesDivideOut(block, nextBlock) || timesMightCombine(block, nextBlock)) )
						{
							for(HeuristicTermPackage otherTerm : theseTerms) {
								if (otherTerm == thisTerm) continue;
								if (otherTerm.ignore) break;
								thisTerm.queueUpScore(otherTerm.termsScore+1);		//plus 1 to handle this multiplication/division
							}
							
							for(HeuristicTermPackage otherTerm : otherTerms) {
								if (otherTerm.ignore) break;
								thisTerm.queueUpScore(otherTerm.termsScore+1);		//plus 1 to handle this multiplication/division
							}
							break;		//only do the first dependent
						}
					}
				}
			}
		}
	}

	private static void handleIsolatedTerms(List<HeuristicTermPackage> terms) {
		for (HeuristicTermPackage thisTerm : terms) {
			if (thisTerm.ignore) break;
			if (thisTerm.hasBeenHandled ) continue;

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

						if (mightDivideOut(block, nextBlock)) {
							thisTerm.termsScore += adjustScoreForDividingOut(block, nextBlock);
							//we essentially got two steps at once, so skip to the next block
							i++;
						} else if (timesMightCombine(block, nextBlock)) {
							//this is like combining 7*3(x-5) -> 21(x-5), which is one step
							thisTerm.termsScore += 1;
						}
						else if (thisTerm.isEventualCombine && i == attachedBlockList.size()-2) {
							thisTerm.termsScore += 1;	//we won't have to divide out the last term
						}
						//will depend on others
					}
					else {		//if addition or subtraction
						thisTerm.termsScore += 2;
					}
				}
			}
			else {		//simply numbers
				//We will only have to simplify these out, so this is just one step
				//for every thing attached to the number block
				thisTerm.termsScore += attachedBlockList.size() - 1;
				thisTerm.hasBeenHandled = true;
			}

		}
	}

	private static void handleComplicatedTerms(List<HeuristicTermPackage> terms, int generalThisSideTerms,
			int generalOtherSideTerms, int thisSideVarTerms, int otherSideVarTerms) {

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

						if (mightDivideOut(block, nextBlock)) {
							thisTerm.termsScore += adjustScoreForDividingOut(block, nextBlock);
							//we essentially got two steps at once, so skip to the next block
							i++;
						} else if (timesMightCombine(block, nextBlock)) {
							//this is like combining 7*3(x-5) -> 21(x-5), which is one step
							thisTerm.termsScore += 1;
						}
						else if (thisTerm.isEventualCombine && i == attachedBlockList.size()-2) {
							thisTerm.termsScore += 1;	//we won't have to divide out the last term
						}
						else {
							//Because we'll have to either multiply or divide to remove this term
							//one step for every variable on this side and every term on the other
							//(may need to be total terms)
							thisTerm.termsScore += generalThisSideTerms+generalOtherSideTerms;
							
							//TODO terms based on attached plusses and minuses.
							//I.e. 2x + 5(x-6) = 20 is worse than 2x + 5x = 20 + 30
							
							
							// dividing/multiplying out with a plus or minus block can't happen yet
							if (nextBlock instanceof PlusBlock || nextBlock instanceof MinusBlock) {
								thisTerm.termsScore++;
							}
						}
					}
					else {		//if addition or subtraction
						thisTerm.termsScore += 2;
					}
				}
			}
			else {		//simply numbers
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

	
	private static double handleEasilyCombinableTerms(List<HeuristicTermPackage> side) {
		double score = 0;

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
				//Either the block is solo     or  three is one times block at index 1
				if (attachedBlocks.size() <= 1 || attachedBlocks.get(1) instanceof TimesBlock) {
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
			if (!(sum == 1 || sum ==0 )) score+=2;		//1 for the drag over, 1 for the simplify
		
			for (HeuristicTermPackage h:potentialTimesHandled) h.hasBeenHandled = true;
		} 

		return score;
	}

	private static double accountForEventualCombinableTerms(List<HeuristicTermPackage> side) {
		double score = 0;
		
		//for cases like [10x] + [8x + 6] = [-97 + 13] + [ ], where we won't have to divide out
		//the 10x and 8x, we'll be able to add them
		
		potentialTimesHandled.clear();
		potentialNumbersHandled.clear();
		timeses.clear();

		for(int i = 0;i< side.size();i++)	{
			HeuristicTermPackage thisTerm = side.get(i);
			if (thisTerm.ignore) break;
			//we do not need to skip over the terms that might have been combined in the easy case,
			//in fact, those might be part of the combination
			//e.g. [10x]` + [8x + 6] + [-17x]` = 34
			//e.g. the 10x and -17x would have already been handled, but they will be part of the 
			//eventual combination (down to 1x, no less) and so, we must account for them 
			//We just won't re-update their values
			BaseBlock thisBlock = thisTerm.term;

			//if the block and 
			List<Block> attachedBlocks = thisBlock.getAllBlocks();
			if (thisBlock instanceof VariableBlock)
			{
				if (attachedBlocks.size() <= 1) {
					potentialTimesHandled.add(thisTerm);
					timeses.add(1);
				} else if (attachedBlocks.size() >2 && attachedBlocks.get(1) instanceof TimesBlock
						&& !(attachedBlocks.get(2) instanceof TimesBlock)) {
					potentialTimesHandled.add(thisTerm);
					timeses.add(((ModifierBlock) attachedBlocks.get(1)).value());
					//We don't want to update already handled's scores
				} 
			}
			else if (thisBlock instanceof NumberBlock){
				potentialNumbersHandled.add(thisTerm);
			}
		}
		
		if (potentialTimesHandled.size() > 1) {
			//if the end sum is not 1 or 0, we will have to divide as well
			int sum = sumList(timeses);
			if ((sum == 1 || sum ==0 )) score-=1;		//1 for the drag over, 1 for the simplify
		
			for (HeuristicTermPackage h:potentialTimesHandled) if (!h.hasBeenHandled) h.isEventualCombine = true;
		}
		
		if (potentialNumbersHandled.size() > 1) {
			//eventually, we will have to combine these blocks using n-1 drags and n-1 combines
			score += 2* (potentialNumbersHandled.size() - 1);
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

	private static boolean doesDivideOut(Block block, Block nextBlock) {
		if (!(mightDivideOut(block, nextBlock))) return false;
		ModifierBlock mBlock = (ModifierBlock)block;
		ModifierBlock mNextBlock = (ModifierBlock) nextBlock;
		return mBlock.value() == mNextBlock.value() || mBlock.value() == -mNextBlock.value();
	}

	private static boolean mightDivideOut(Block block, Block nextBlock) {
		return (block instanceof TimesBlock && nextBlock instanceof OverBlock) ||
				(block instanceof OverBlock && nextBlock instanceof TimesBlock);
	}


	private static boolean timesMightCombine(Block block, Block nextBlock) {
		return (block instanceof TimesBlock && nextBlock instanceof TimesBlock);
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
