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
import tuxkids.tuxblocks.core.solve.blocks.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.OverBlock;
import tuxkids.tuxblocks.core.solve.blocks.TimesBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.Formatter;

/**
 * Handles solving an equation using search and a heuristic.
 */
public class IdealEquationSolver {

	private static final int MAX_TERMS_PER_SIDE = 7;
	
	private static final int MAX_ASTAR_QUEUE_SIZE = 50;
	
	//can be used for observing the inner steps of the heuristic
	private static boolean debugHeuristic = false;

	private static Comparator<List<Step>> comparator = new Comparator<List<Step>>() {
		@Override
		public int compare(List<Step> o1, List<Step> o2) {
			Equation eq1 = o1.get(o1.size() - 1).result;
			Equation eq2 = o2.get(o2.size() - 1).result;
			// classic A* - compare based on f(x) + h(x)
			return Double.compare(heuristic(eq1) + o1.size(), heuristic(eq2) + o2.size());
		}
	};

	public static List<Step> aStar(Equation start) {
		return aStar(start, Integer.MAX_VALUE);
	}
	
	public static List<Step> aStar(Equation start, int maxSteps) {
		// queue of paths to solution, sorted using the heuristic
		PriorityQueue<PathList> paths = new PriorityQueue<PathList>(20);
		
		// add an empty start path
		PathList startPath = new PathList();
		startPath.add(new Step(start));
		paths.add(startPath);

		// a map of the shortest path lengths to a given node
		HashMap<String, Integer> discoveredNodes = new HashMap<String, Integer>();

		// count how many nodes we've expanded
		int numExpanded = 0;
		
		while (paths.size() > 0) {
			// seeAllAndHeuristics(paths);
			PathList toExpand = paths.poll(); // get the best estimated path
			Step last = toExpand.get(toExpand.size() - 1); // get the last state of the equation
			numExpanded++; //increment the number expanded
			
			// break if we win or excede the limit
			if (numExpanded >= maxSteps || EquationManipulator.isEquationSolved(last.result)) {
				return toExpand;
			}

			// get all possible nodes reachable from this node
			List<Step> branches = expandState(last.result);
			for (Step step : branches) {
				if (!registerNode(step, discoveredNodes, toExpand.size() + 1)) {
					continue;
				}

				// add them all as children, branching from the original path
				PathList nPath = new PathList(toExpand);
				nPath.add(step);
				paths.add(nPath);
			}
			
			if (paths.size() > MAX_ASTAR_QUEUE_SIZE * 2) {
				PriorityQueue<PathList> ps = new PriorityQueue<PathList>(MAX_ASTAR_QUEUE_SIZE);
				for (int i = 0; i < MAX_ASTAR_QUEUE_SIZE; i++) {
					ps.add(paths.poll());
				}
				paths = ps;
			}
		}

		// this will almost certainly never happen...
		// but if it does, the equation is unsolvable
		return null;
	}
	
	@SuppressWarnings("serial")
	private static class PathList extends ArrayList<Step> implements Comparable<PathList> {
		
		public PathList() { }
		
		public PathList(PathList copyFrom) {
			super(copyFrom);
		}
		
		@Override
		public int compareTo(PathList other) {
			return comparator.compare(this, other);
		}
	}

	// returns a list of all steps that can be taken for the given equation
	private static List<Step> expandState(Equation state) {
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

	private static boolean registerNode(Step currentStep, HashMap<String, Integer> discoveredNodes, int pathLength) {

		// hash it using its text function... TODO: use a quicker/more accurate hash
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

	/**
	 * An object to represent a single term and help hold the half-way steps
	 * for computing a heuristic for the entire equation.
	 * @author Kevin Lubick
	 *
	 */
	private static class HeuristicTermPackage {
		BaseBlock term = null;
		boolean ignore = true;
		boolean hasBeenHandled = false;
		double termsScore = 0.0;		
		boolean isEventualCombine = false;
		private double pendingScore;

		public HeuristicTermPackage reset() {
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
			if (ignore)
				return "";
			//TODO: fix formatting - doesn't current support %1.2f and the like - just %s/%f/%d, etc.
			if (!hasBeenHandled)
				return Formatter.format("[%1.2f+%1.2f?]%s", termsScore, pendingScore, isEventualCombine ? "`" : "");
			return Formatter.format("[%1.2f+%1.2f]%s", termsScore, pendingScore, isEventualCombine ? "`" : "");
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

	//Variables that will be reused to prevent unneccessary allocations
	private static List<HeuristicTermPackage> leftSideTerms = new ArrayList<IdealEquationSolver.HeuristicTermPackage>(
			MAX_TERMS_PER_SIDE);
	private static List<HeuristicTermPackage> rightSideTerms = new ArrayList<IdealEquationSolver.HeuristicTermPackage>(
			MAX_TERMS_PER_SIDE);

	private static Set<Integer> skipIndicies = new HashSet<Integer>();
	private static List<Integer> timeses = new ArrayList<Integer>(MAX_TERMS_PER_SIDE);
	private static Set<HeuristicTermPackage> potentialTimesHandled = new HashSet<IdealEquationSolver.HeuristicTermPackage>();
	private static Set<HeuristicTermPackage> potentialNumbersHandled = new HashSet<IdealEquationSolver.HeuristicTermPackage>();

	static {
		for (int i = 0; i < MAX_TERMS_PER_SIDE; i++) {
			leftSideTerms.add(new HeuristicTermPackage());
			rightSideTerms.add(new HeuristicTermPackage());
		}
	}

	public static double heuristic(Equation eq) {

		double score = 0;

		Iterator<BaseBlock> leftSideIterator = eq.leftSide().iterator();
		Iterator<BaseBlock> rightSideIterator = eq.rightSide().iterator();

		int generalLeftTerms = 0;
		int generalRightTerms = 0;
		int leftVarTerms = 0;
		int rightVarTerms = 0;

		// Dump the terms into our lists and perform some counts of the various terms
		for (int i = 0; i < MAX_TERMS_PER_SIDE; i++) {
			HeuristicTermPackage leftTerm = leftSideTerms.get(i).reset();
			HeuristicTermPackage rightTerm = rightSideTerms.get(i).reset();

			if (leftSideIterator.hasNext()) {
				leftTerm.ignore = false;
				leftTerm.term = leftSideIterator.next();
				if (!(leftTerm.term instanceof BlockHolder))
					generalLeftTerms++;
				if (leftTerm.term instanceof VariableBlock)
					leftVarTerms++;
			}
			if (rightSideIterator.hasNext()) {
				rightTerm.ignore = false;
				rightTerm.term = rightSideIterator.next();
				if (!(rightTerm.term instanceof BlockHolder))
					generalRightTerms++;
				if (rightTerm.term instanceof VariableBlock)
					rightVarTerms++;
			}
		}

		// discourage emptying out one side of the equation
		if (generalRightTerms == 0 || generalLeftTerms == 0) {
			score += .1;
		}

		// discourage having variable terms on both sides
		if (leftVarTerms > 0 && rightVarTerms > 0) {
			score += 1.5;
		}

		double leftSideEasyCombinationScore = handleEasilyCombinableTerms(leftSideTerms);
		score += leftSideEasyCombinationScore;
		double rightSideEasyCombinationScore = handleEasilyCombinableTerms(rightSideTerms);
		score += rightSideEasyCombinationScore;

		double leftSideEventualCombinationScore = accountForEventualCombinableTerms(leftSideTerms);
		score += leftSideEventualCombinationScore;
		double rightSideEventualCombinationScore = accountForEventualCombinableTerms(rightSideTerms);
		score += rightSideEventualCombinationScore;

		if (debugHeuristic) {
			Debug.write("\t\tLSECS=%1.2f LSDCS=%1.2f | RSECS=%1.2f RSDCS=%1.2f ", leftSideEasyCombinationScore,
					leftSideEventualCombinationScore, rightSideEasyCombinationScore, rightSideEventualCombinationScore);
		}

		handleIsolatedTerms(leftSideTerms);
		handleIsolatedTerms(rightSideTerms);

		handleDependentTerms(leftSideTerms, rightSideTerms, leftVarTerms);
		handleDependentTerms(rightSideTerms, leftSideTerms, rightVarTerms);

		if (debugHeuristic) {
			for (int i = 0; i < MAX_TERMS_PER_SIDE; i++) {
				if (leftSideTerms.get(i).ignore)
					continue;
				System.out.print(leftSideTerms.get(i) + " + ");
			}
			System.out.print(" = ");
			for (int i = 0; i < MAX_TERMS_PER_SIDE; i++) {
				if (rightSideTerms.get(i).ignore)
					continue;
				System.out.print(rightSideTerms.get(i) + " + ");
			}
		
			Debug.write("\tgL = %d, gR = %d, vL = %d, vR = %d%n", generalLeftTerms, generalRightTerms,
					leftVarTerms, rightVarTerms);
		}

		for (int i = 0; i < MAX_TERMS_PER_SIDE; i++) {
			if (!leftSideTerms.get(i).ignore)
				score += leftSideTerms.get(i).finalizeScore().termsScore;
			if (!rightSideTerms.get(i).ignore)
				score += rightSideTerms.get(i).finalizeScore().termsScore;
		}

		if (score <= 0)
			return 0;
		return score;

	}

	private static List<Block> tempBlockList = new ArrayList<Block>();
	
	/**
	 * Handles combinable terms like 
	 * 3x` + 2x` = 5  (known as potentialTimes)
	 * 2x = 17` - 23`	(known as potential Numbers)
	 * 
	 * `combinable term.
	 * 
	 * the passed in heuristics will not have their scores updated, but may be marked as handled.
	 * A representative score for this side of the equation will be returned.
	 */
	private static double handleEasilyCombinableTerms(List<HeuristicTermPackage> side) {
		//it takes at least two potentially combinable terms to combine, so we want to hold references
		//to candidates before we tally up the scores
		potentialNumbersHandled.clear();
		potentialTimesHandled.clear();
		timeses.clear();

		for (int i = 0; i < side.size(); i++) {
			HeuristicTermPackage thisTerm = side.get(i);
			if (thisTerm.ignore)
				break;
			if (thisTerm.hasBeenHandled)
				continue;
			BaseBlock thisBlock = thisTerm.term;

			tempBlockList.clear();
			List<Block> attachedBlocks = thisBlock.getAllBlocks(tempBlockList);
			if (thisBlock instanceof VariableBlock && attachedBlocks.size() <= 2) {
				// Either the block is solo or three is one times block at index 1
				if (attachedBlocks.size() <= 1 || attachedBlocks.get(1) instanceof TimesBlock) {
					potentialTimesHandled.add(thisTerm);
					if (attachedBlocks.size() == 1) {
						timeses.add(1);
					} else {
						timeses.add(((ModifierBlock) attachedBlocks.get(1)).value());
					}

				}
			} else if (thisBlock instanceof NumberBlock && attachedBlocks.size() == 1) {
				potentialNumbersHandled.add(thisTerm);
			}
		}
		double score = 0;
		// one move per n-1 blocks to move together and one move per n-1 blocks
		// to combine numbers
		// e.g. [3]+[3]+[3] would be resolved in 4 moves
		// [3+3]+[3]+[]
		// [6]+ [3] +[]
		// [6+3] + [] + []
		// [9] + [] + []
		if (potentialNumbersHandled.size() > 1) {
			score += (potentialNumbersHandled.size() - 1) * 2;
			for (HeuristicTermPackage h : potentialNumbersHandled)
				h.hasBeenHandled = true;
		}

		if (potentialTimesHandled.size() > 1) {
			// one move per n-1 blocks to move together (combine happens automatically or instantaneously)
			score += potentialTimesHandled.size() - 1;

			// if the end sum is not 1 or 0, we will have to divide as well
			int sum = sumList(timeses);
			if (!(sum == 1 || sum == 0))
				score += 2; // 1 for the drag over, 1 for the simplify

			for (HeuristicTermPackage h : potentialTimesHandled)
				h.hasBeenHandled = true;
		}

		return score;
	}

	/**
	 * Detects "eventual combines", for cases like 
	 * [10x] + [8x + 6] = [-97 + 13] + [ ]
	 * where we won't have to divide out
	 * the 10x and 8x, because we'll be able to add them.
	 * 
	 * This will find those terms and mark them as "eventual combine" so they aren't 
	 * treated like multiplication that will need to be undone by dividing out
	 */
	private static double accountForEventualCombinableTerms(List<HeuristicTermPackage> side) {
		double score = 0;

		potentialTimesHandled.clear();
		potentialNumbersHandled.clear();
		timeses.clear();

		for (int i = 0; i < side.size(); i++) {
			HeuristicTermPackage thisTerm = side.get(i);
			if (thisTerm.ignore)
				break;
			// we do not need to skip over the terms that might have been
			// combined in the easy case,
			// in fact, those might be part of the combination
			// e.g. [10x]` + [8x + 6] + [-17x]` = 34
			// e.g. the 10x and -17x would have already been handled, but they
			// will be part of the
			// eventual combination (down to 1x, no less) and so, we must
			// account for them
			// We just won't re-update their values
			BaseBlock thisBlock = thisTerm.term;

			// if the block and
			tempBlockList.clear();
			List<Block> attachedBlocks = thisBlock.getAllBlocks(tempBlockList);
			if (thisBlock instanceof VariableBlock) {
				if (attachedBlocks.size() <= 1) {
					potentialTimesHandled.add(thisTerm);
					timeses.add(1);
				} else if (attachedBlocks.size() > 2 && attachedBlocks.get(1) instanceof TimesBlock
						&& !(attachedBlocks.get(2) instanceof TimesBlock)) {
					potentialTimesHandled.add(thisTerm);
					timeses.add(((ModifierBlock) attachedBlocks.get(1)).value());
				}
			} else if (thisBlock instanceof NumberBlock) {
				potentialNumbersHandled.add(thisTerm);
			}
		}

		if (potentialTimesHandled.size() > 1) {
			// if the end sum is 1 or 0, we save a step, so subtract
			int sum = sumList(timeses);
			if ((sum == 1 || sum == 0))
				score -= 1; 

			for (HeuristicTermPackage h : potentialTimesHandled)
				if (!h.hasBeenHandled)
					h.isEventualCombine = true;
		}

		if (potentialNumbersHandled.size() > 1) {
			// eventually, we will have to combine these blocks using n-1 drags
			// and n-1 combines
			score += 2 * (potentialNumbersHandled.size() - 1);
			
			//Potential side effect do we need to mark these numbers as handled?
		}

		return score;
	}

	/**
	 * Goes through each term on this side of the equation and pretends they are "isolated",
	 * that is, "If this term were the only thing in this expression, how long would it take to
	 * isolate 'x' or simplify this to one number?"
	 */
	private static void handleIsolatedTerms(List<HeuristicTermPackage> terms) {
		for (HeuristicTermPackage thisTerm : terms) {
			if (thisTerm.ignore)
				break;
			if (thisTerm.hasBeenHandled || thisTerm.term instanceof BlockHolder)
				continue;

			tempBlockList.clear();
			List<Block> attachedBlockList = thisTerm.term.getAllBlocks(tempBlockList);
			
			if (thisTerm.term instanceof VariableBlock) {	
				handleIsolatedVariableBlock(thisTerm, attachedBlockList);
			} else { 
				// simply numbers
				// We will only have to simplify these out, so this is just one step
				// for every thing attached to the number block
				thisTerm.termsScore += attachedBlockList.size() - 1;

				// discourage large number blocks (not too human-like)
				if (attachedBlockList.size() > 3)
					thisTerm.termsScore += .05 * attachedBlockList.size();
				thisTerm.hasBeenHandled = true;
			}

		}
	}

	//helper for handleIsolatedTerms
	private static void handleIsolatedVariableBlock(HeuristicTermPackage thisTerm, List<Block> attachedBlockList) {
		// Iterate through everything attached to this block in reverse order
		Collections.reverse(attachedBlockList);
		skipIndicies.clear();
		
		int endOfAttachedBlockIndex = attachedBlockList.size() - 1;
		for (int i = 0; i < endOfAttachedBlockIndex; i++) {
			if (skipIndicies.contains(i))
				continue;
			Block block = attachedBlockList.get(i);
			if (isBlockTimesOrDivide(block)) {
				Block nextBlock = attachedBlockList.get(i + 1);
				int divideOutOffset = doesDivideOut(block,
						attachedBlockList.subList(i + 1, endOfAttachedBlockIndex));
				
				if (-1 != divideOutOffset) {
					thisTerm.termsScore += adjustScoreForDividingOut(block, attachedBlockList.get(i + divideOutOffset));
					// we essentially got two steps at once, so skip this step
					skipIndicies.add(i + divideOutOffset);
				} else if (timesMightCombine(block, nextBlock)) {
					// this is like combining 7*3(x-5) -> 21(x-5), which is one step
					thisTerm.termsScore += 1;
				} else if (thisTerm.isEventualCombine && i == attachedBlockList.size() - 2) {
					thisTerm.termsScore += 1; // we won't have to divide out the last term
				}
				// will depend on others
			} else { // if addition or subtraction, we'll have to slide it over and combine it with whatever is there
				thisTerm.termsScore += 2;
			}
		}
	}

	/**
	 * Goes through the terms and, now that all the scores are known for each term in isolation,
	 * approximates how many steps it will take to solve a given term whilst working around other terms.
	 * 
	 * given 
	 * [(x) / 12] + [4(x - 2)] = [ 90 ] + [    ]
	 * [  0.00  ] + [  2.00  ] = [0.00] + [0.00]		//scores after isolated run run
	 * 
	 * For the first term, undoing the multiply by 12 will require us to dance around the second block
	 * and the third block, so we add 1 point for undoing the division, the score + 1 of every variable
	 * term we dance around (once to combine in the undoing) and a normalized amount for dealing with
	 * number blocks.  These scores are put in the HeuristicTermPackage's queue up score so that the changes
	 * to the first block don't adversely affect the later ones.  At the end, we have:
	 * [0.00+4.50] + [2.00+2.50] = [0.00+0.00] + [0.00+0.00]
	 */
	private static void handleDependentTerms(List<HeuristicTermPackage> theseTerms,
			List<HeuristicTermPackage> otherTerms, int varTermsThisSide) {

		for (HeuristicTermPackage thisTerm : theseTerms) {
			if (thisTerm.ignore)
				break;
			if (thisTerm.hasBeenHandled || thisTerm.term instanceof BlockHolder)
				continue;
			thisTerm.hasBeenHandled = true;

			skipIndicies.clear();

			tempBlockList.clear();
			List<Block> attachedBlockList = thisTerm.term.getAllBlocks(tempBlockList);
			Collections.reverse(attachedBlockList);

			double conditionalScore = 0; // score to be added if there is a times or over eventually
			// e.g. x + 2(x-12)/3 + 47 = -21 is farther away than x + 2(x-12)/3 = -21-47

			if (thisTerm.term instanceof VariableBlock && attachedBlockList.size() >= 2) // ignore solo blocks
			{
				for (int i = 0; i < attachedBlockList.size() - 1; i++) {
					if (skipIndicies.contains(i))
						continue;
					Block block = attachedBlockList.get(i);

					if (isBlockTimesOrDivide(block)) {
						thisTerm.queueUpScore(conditionalScore);
						conditionalScore = 0;
						Block nextBlock = attachedBlockList.get(i + 1);

						int divideOutOffset = doesDivideOut(block,
								attachedBlockList.subList(i + 1, attachedBlockList.size() - 1));
						if (-1 != divideOutOffset) {
							skipIndicies.add(i + divideOutOffset);
							continue;
						} else if (timesMightCombine(block, nextBlock)) {
							skipIndicies.add(i + 1);
							continue;
						} else {
							for (HeuristicTermPackage otherTerm : theseTerms) {
								if (otherTerm.ignore)
									break;
								if (otherTerm == thisTerm || otherTerm.term instanceof BlockHolder)
									continue;
								if (otherTerm.term instanceof NumberBlock) {
									thisTerm.queueUpScore(1.0 / varTermsThisSide);
									continue;
								}
								thisTerm.queueUpScore(otherTerm.termsScore + 1); // plus 1 to handle this multiplication/division
							}

							for (HeuristicTermPackage otherTerm : otherTerms) {
								if (otherTerm.ignore)
									break;
								if (otherTerm.term instanceof BlockHolder)
									continue;
								if (otherTerm.term instanceof NumberBlock) {
									thisTerm.queueUpScore(1.0 / varTermsThisSide);
									continue;
								}
								thisTerm.queueUpScore(otherTerm.termsScore + 1); // plus 1 to handle this multiplication/division
							}
							thisTerm.queueUpScore(1); // and one turn to execute the task (clicking the over/times or dragging it)
							break; // only do the first dependent block on a term
						}
					}
					// else is a plus or a minus
					else {
						conditionalScore++;
					}
				}
			}
		}
	}

	private static int sumList(List<Integer> timeses) {
		int sum = 0;
		for (Integer i : timeses)
			sum += i;
		return sum;
	}

	private static double adjustScoreForDividingOut(Block block, Block nextBlock) {
		ModifierBlock mBlock = (ModifierBlock) block;
		ModifierBlock mNextBlock = (ModifierBlock) nextBlock;
		if (mBlock.value() == mNextBlock.value()) {
			return 0; // 1 turn discount to make these more lucrative  XXX a negative value makes some equations hang.  Unsure of cause.
		} else if (mBlock.value() == -mNextBlock.value()) {
			return 1; // in the case of negatives, it takes two turns, but with the one turn discount, 1 point
		}
		return 2;
	}

	private static boolean canTwoBlocksDivideOut(Block block, Block nextBlock) {
		ModifierBlock mBlock = (ModifierBlock) block;
		ModifierBlock mNextBlock = (ModifierBlock) nextBlock;
		return mBlock.value() == mNextBlock.value() || mBlock.value() == -mNextBlock.value();
	}

	/**
	 *  Searches for future blocks that this block might divide out with.  
	 *  
	 *  return -1 if no possible division, index in the attached list otherwise
	 */
	private static int doesDivideOut(Block block, List<Block> remainingBlocks) {
		if (isBlockTimesOrDivide(block)) {
			boolean thisBlockIsOver = block instanceof OverBlock;
			int offset = 0;
			for (Block otherBlock : remainingBlocks) {
				offset++;
				if (isBlockTimesOrDivide(otherBlock)) {
					if (thisBlockIsOver && otherBlock instanceof TimesBlock || !thisBlockIsOver
							&& otherBlock instanceof OverBlock) {
						if (canTwoBlocksDivideOut(block, otherBlock)) {
							return offset;
						}
						// perhaps there is another match, like in the case of 
						//new VariableBlock("x").minus(4).{times(3)}.times(2).[over(3)]
						//where [] is the passed in block and { } is the eventually found match
					}
				} else {
					// if we have bumped into an addition or subtraction, we can't divide anything out
					// e.g. new VariableBlock("x").times(3).minus(4).[over(3)]
					return -1;
				}
			}
		}
		return -1;		//this shouldn't be the case, but if block isn't an over/times block, we can't divide it out
	}

	private static boolean isBlockTimesOrDivide(Block block) {
		return block instanceof TimesBlock || block instanceof OverBlock;
	}

	private static boolean timesMightCombine(Block block, Block nextBlock) {
		return (block instanceof TimesBlock && nextBlock instanceof TimesBlock);
	}

	public static class Step {
		// sometimes steps have multiple actions associated with them, such as
		// starting and ending a simplification (also the start step has no actions)
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
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(equationString);
			if (actions.size() > 0) {
				sb.append(" - ");
				sb.append(actions);
			}
			return sb.toString();
		}
	}

}
