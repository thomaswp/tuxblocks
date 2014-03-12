package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import playn.core.Pointer.Event;

import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.action.DragAction;
import tuxkids.tuxblocks.core.solve.action.FinishSimplifyAction;
import tuxkids.tuxblocks.core.solve.action.ReciprocalAction;
import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.action.StartSimplifyAction;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.BlockListener;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.SimplifyListener;
import tuxkids.tuxblocks.core.solve.markup.Renderer;

public class EquationManipulatorSolver extends EquationManipulator implements BlockListener {
	
	private Stack<MutableEquation> stack = new Stack<MutableEquation>();
	private List<SolveAction> extraActions;
	
	public Equation equation() {
		return equation;
	}
	
	@Override
	protected boolean hasSprites() {
		return false;
	}
	
	public EquationManipulatorSolver(Equation equation) {
		this.equation = equation.mutableCopy();
	}
	
	public void push() {
		stack.push(equation);
		equation = equation.mutableCopy();
		clearDragData();
	}
	
	public MutableEquation pop() {
		MutableEquation eq = equation;
		equation = stack.pop();
		clearDragData();
		return eq;
	}
	
	public List<SolveAction> getAllActions() {
		List<SolveAction> actions = new ArrayList<SolveAction>();
		actions.addAll(getDragActions());
		actions.addAll(getReciprocalActions());
		return actions;
	}
	
	public List<DragAction> getDragActions() {
		List<DragAction> actions = new ArrayList<DragAction>();
		List<EquationBlockIndex> draggables = getDraggableBlocks();
		for (EquationBlockIndex draggable : draggables) {
			Block block = equation.getBlock(draggable);
			List<Integer> droppables = getDroppableBases(block, draggable);
			for (Integer droppable : droppables) {
				if ((int) droppable != draggable.expressionIndex) {
					actions.add(new DragAction(draggable, droppable, true));
				}
			}
		}
		return actions;
	}
	
	private List<EquationBlockIndex> getDraggableBlocks() {
		List<EquationBlockIndex> blocks = new ArrayList<EquationBlockIndex>();
		blocks.addAll(getDraggableBlocks(equation.leftSideList(), 0));
		blocks.addAll(getDraggableBlocks(equation.rightSideList(), equation.leftSideList().size()));
		return blocks;
	}
	
	private List<EquationBlockIndex> getDraggableBlocks(List<BaseBlock> side, int indexOffset) {
		List<EquationBlockIndex> blocks = new ArrayList<EquationBlockIndex>();
		boolean multiExpression = isMultiExpression(equation, side);
		int i = 0;
		for (BaseBlock baseBlock : side) {
			for (Block block : baseBlock.getAllBlocks()) {
				if (block.canRelease(multiExpression)) {
					blocks.add(new EquationBlockIndex(i + indexOffset, baseBlock.indexOf(block)));
				}
			}
			i++;
		}
		return blocks;
	}
	
	private List<Integer> getDroppableBases(Block toDrop, EquationBlockIndex startingIndex) {
		List<Integer> blocks = new ArrayList<Integer>();
		Side startingSide = startingIndex.expressionIndex < equation.leftSide.size() ? Side.Left : Side.Right;

		Block inverse = (Block) toDrop.inverse().copy();
		inverse.showInverse();
		Block leftDrop = startingSide == Side.Left ? toDrop : inverse;
		Block rightDrop = startingSide == Side.Right? toDrop : inverse;
		
		blocks.addAll(getDroppableBases(leftDrop, equation.leftSide, 0));
		blocks.addAll(getDroppableBases(rightDrop, equation.rightSide, equation.leftSide.size()));
		return blocks;
	}
	
	private List<Integer> getDroppableBases(Block toDrop, List<BaseBlock> side, int indexOffset) {
		List<Integer> blocks = new ArrayList<Integer>();
		for (int i = 0; i < side.size(); i++) {
			if (side.get(i).canAccept(toDrop)) blocks.add(i + indexOffset);
		}
		return blocks;
	}
	
	public List<ReciprocalAction> getReciprocalActions() {
		List<ReciprocalAction> actions = new ArrayList<ReciprocalAction>();
		int index = 0;
		for (BaseBlock base : equation) {
			for (Block block : base.getAllBlocks()) {
				if (block instanceof VerticalModifierBlock) {
					VerticalModifierBlock modBlock = (VerticalModifierBlock) block;
					if (modBlock.canAddInverse()) {
						ReciprocalAction action = new ReciprocalAction(
								new EquationBlockIndex(index, base.indexOf(block)), true);
						actions.add(action);
					}
				}
			}
			index++;
		}
		return actions;
	}
	
	public List<SolveAction> performAction(SolveAction action) {
		if (action instanceof DragAction) {
			return performAction((DragAction) action);
		} else if (action instanceof ReciprocalAction) {
			performAction((ReciprocalAction) action);
		}
		return null;
	}
	
	public List<SolveAction> performAction(DragAction action) {
		dragBlock(equation.getBlock(action.fromIndex));
		if (getSideFromBaseIndex(action.fromIndex.expressionIndex) != 
				getSideFromBaseIndex(action.toIndex)) {
			dragging = invertBlock(dragging);
		}
		BaseBlock target = equation.getBaseBlock(action.toIndex);
		
		extraActions = new ArrayList<SolveAction>();
		target.addBlockListener(this);
		dropBlock(target);
		List<SolveAction> extraActions = this.extraActions;
		this.extraActions = null;
		return extraActions;
	}
	
	public void performAction(ReciprocalAction action) {
		reciprocateBlock(equation.getBlock(action.index));
	}

	@Override
	public void wasReduced(Renderer problem, int answer, int startNumber,
			Stat stat, int level, SimplifyListener callback) {
		callback.wasSimplified(true);
		StartSimplifyAction startSimplify = new StartSimplifyAction(null);
		extraActions.add(startSimplify);
		FinishSimplifyAction finishSimplify = new FinishSimplifyAction(null, answer, true);
		extraActions.add(finishSimplify);
	}

	@Override
	public void wasGrabbed(Block sprite, Event event) { }

	@Override
	public void wasReleased(Event event) { }

	@Override
	public void wasMoved(Event event) { }

	@Override
	public void wasDoubleClicked(Block sprite, Event event) { }

	@Override
	public void wasSimplified() { }

	@Override
	public void wasCanceled() { }

	@Override
	public boolean inBuildMode() {
		return false;
	}
}
