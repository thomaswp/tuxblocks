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
import tuxkids.tuxblocks.core.solve.action.StartSimplifyVariablesAction;
import tuxkids.tuxblocks.core.solve.action.StartSimplifyingBlocksAction;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.BlockListener;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.SimplifyListener;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.Aggregator;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.Simplifiable;
import tuxkids.tuxblocks.core.solve.markup.Renderer;

public class EquationManipulatorSolver extends EquationManipulator implements BlockListener {
	
	private Stack<MutableEquation> stack = new Stack<MutableEquation>();
	private List<SolveAction> extraActions;
	
	public MutableEquation equation() {
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
	
	public EquationManipulatorSolver copy() {
		return new EquationManipulatorSolver(equation);
	}
	
	public List<SolveAction> getAllActions() {
		List<SolveAction> actions = new ArrayList<SolveAction>();
		actions.addAll(getDragActions());
		actions.addAll(getReciprocalActions());
		actions.addAll(getSimplifyActions());
		return actions;
	}

	public List<DragAction> getDragActions() {
		List<DragAction> actions = new ArrayList<DragAction>();
		List<EquationBlockIndex> draggables = getDraggableBlocks();
		for (EquationBlockIndex draggable : draggables) {
			push();
			dragBlock(equation.getBlock(draggable));
			Block block = dragging;
			List<Integer> droppables = getDroppableBases(block, draggable);
			for (Integer droppable : droppables) {
				if ((int) droppable != draggable.expressionIndex) {
					actions.add(new DragAction(draggable, droppable, true));
				}
			}
			pop();
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
	
	public List<StartSimplifyingBlocksAction> getSimplifyActions() {
		final List<StartSimplifyingBlocksAction> actions = new ArrayList<StartSimplifyingBlocksAction>();
		int index = 0;
		for (BaseBlock base : equation) {
			int depth = 0;
			Simplifiable simplifiable;
			if (base instanceof Simplifiable) {
				simplifiable = (Simplifiable) base;
			} else {
				depth++;
				simplifiable = base.modifiers;
			}
			while (simplifiable != null) {
				final BaseBlock parent = base;
				final int fi = index;
				final int fd = depth;
				simplifiable.addSimplifiableBlocks(new Aggregator() {
					@Override
					public void add(ModifierBlock sprite, ModifierBlock pair, Object tag) {
						EquationBlockIndex spriteIndex = new EquationBlockIndex(fi, parent.indexOf(sprite));
						EquationBlockIndex pairIndex = (pair == null ? null : new EquationBlockIndex(fi, parent.indexOf(pair)));
						actions.add(new StartSimplifyingBlocksAction(spriteIndex, pairIndex, fd));
					}
				});
				if (simplifiable instanceof BaseBlock) {
					simplifiable = ((BaseBlock) simplifiable).modifiers;
				} else {
					simplifiable = ((ModifierGroup) simplifiable).modifiers;
				}
				depth++;
			}
			index++;
		}
		return actions;
	}
	
	public List<SolveAction> performSolveAction(SolveAction action) {
		String eq = equation.getPlainText();
		try {
			if (action instanceof DragAction) {
				return performAction((DragAction) action);
			} else if (action instanceof ReciprocalAction) {
				performAction((ReciprocalAction) action);
			} else if (action instanceof StartSimplifyingBlocksAction) {
				return performAction((StartSimplifyingBlocksAction) action);
			}
		} catch (Exception e) {
			debug("Failed on: " + eq + ": " + action);
			debug(e);
		}
		return null;
	}
	
	public List<SolveAction> performAction(DragAction action) {
//		Equation copy = equation.copy();
		
		dragBlock(equation.getBlock(action.fromIndex));
		if (getSideFromBaseIndex(action.fromIndex.expressionIndex) != 
				getSideFromBaseIndex(action.toIndex)) {
			dragging = invertBlock(dragging);
		}
		BaseBlock target = equation.getBaseBlock(action.toIndex);
		
		extraActions = new ArrayList<SolveAction>();
		target.addBlockListener(this);
		Block result = dropBlock(target);
		if (!(target instanceof VariableBlock) && result == null) {
			throw new RuntimeException("Failed Drop!"); // + copy.getPlainText() + " - " + action);
		}
		equation.allBlocks.get(action.fromIndex.expressionIndex).update(0);
		
		List<SolveAction> extraActions = this.extraActions;
		this.extraActions = null;
		return extraActions;
	}
	
	public void performAction(ReciprocalAction action) {
		reciprocateBlock(equation.getBlock(action.index));
	}
	
	public List<SolveAction> performAction(StartSimplifyingBlocksAction action) {
		extraActions = new ArrayList<SolveAction>();
		
		BaseBlock base = equation.allBlocks.get(action.baseIndex.expressionIndex);
		int depth = action.modifierDepth;
		Simplifiable simplifiable = null;
		if (depth == 0) {
			simplifiable = (Simplifiable) base;
		} else {
			simplifiable = base.modifiers;
			depth--;
			while (depth > 0) {
				simplifiable = ((ModifierGroup) simplifiable).modifiers;
				depth--;
			}
		}
		
		base.addBlockListener(this);
		ModifierBlock sprite = (ModifierBlock) base.getBlockAtIndex(action.baseIndex.blockIndex);
		ModifierBlock pair = action.pairIndex == null ? null : (ModifierBlock) base.getBlockAtIndex(action.pairIndex.blockIndex); 
		simplifiable.simplify(sprite, pair);
		base.update(0);
				

		List<SolveAction> extraActions = this.extraActions;
		this.extraActions = null;
		return extraActions;
	}

	@Override
	public void wasReduced(Renderer problem, int answer, int startNumber,
			Stat stat, int level, SimplifyListener callback) {
		callback.wasSimplified(true);
		StartSimplifyVariablesAction startSimplify = new StartSimplifyVariablesAction(null);
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
