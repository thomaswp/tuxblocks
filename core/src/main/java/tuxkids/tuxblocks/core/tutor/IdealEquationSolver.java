package tuxkids.tuxblocks.core.tutor;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.Block;
import tuxkids.tuxblocks.core.solve.blocks.MutableEquation;
import tuxkids.tuxblocks.core.student.StudentAction;

public class IdealEquationSolver {
	
	
	
	public SolutionPackage getIdealSolution(MutableEquation e) {
		return null;
	}
	
	private List<Step> expandState(MutableEquation state) {
		List<Step> steps = new ArrayList<Step>();
		return steps;
	}
	
	private List<Step> getDragActions(MutableEquation state, boolean leftSide) {
		final List<Step> steps = new ArrayList<Step>();
		final List<BaseBlock> mySide = leftSide ? state.leftSideList() : state.rightSideList();
		final List<BaseBlock> otherSide = leftSide ? state.rightSideList() : state.leftSideList();
		
		for (int i = 0; i < mySide.size(); i++) {
			final BaseBlock dragFrom = mySide.get(i);
			List<Block> draggableBlocks = new ArrayList<Block>();
			for (Block block : dragFrom.getAllBlocks()) {
//				if (block.ca)
			}
		}
		
		return steps;
	}
	
	private static class Step {
		public final SolveAction action;
		public final MutableEquation result;
		
		public Step (SolveAction action, MutableEquation result) {
			this.action = action;
			this.result = result;
		}
	}
	
	public static class SolutionPackage{
		int numSteps;
		List<StudentAction> solutionOrientedActions;
	}

}
