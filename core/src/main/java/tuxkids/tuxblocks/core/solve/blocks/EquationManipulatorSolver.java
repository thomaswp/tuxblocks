package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import tuxkids.tuxblocks.core.solve.action.DragAction;

public class EquationManipulatorSolver extends EquationManipulator {
	
	private Stack<MutableEquation> stack = new Stack<MutableEquation>();
	
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
	
	public List<DragAction> getDragActions() {
		List<DragAction> actions = new ArrayList<DragAction>();
		List<EquationBlockIndex> draggables = getDraggableBlocks();
		for (EquationBlockIndex draggable : draggables) {
			Block block = equation.getBlock(draggable);
			List<Integer> droppables = getDroppableBases(block, draggable);
			for (Integer droppable : droppables) {
				if ((int) droppable != draggable.expressionIndex) {
					actions.add(new DragAction(draggable, droppable));
				}
			}
		}
		return actions;
	}
	
	public List<EquationBlockIndex> getDraggableBlocks() {
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
	
//	public Block drag(Block block) {
//		BaseBlock parent = null;
//		for (BaseBlock base : equation) {
//			if (base.contains(block)) {
//				parent = base;
//				break;
//			}
//		}
//		
//		if (parent == null) return null;
//		
//		if (parent == block) {
//			// Picking up a BaseBlock
//			int index = equation.leftSideList().indexOf(block);
//			if (index >= 0) {
//				equation.leftSideList().set(index, new BlockHolder());
//			} else {
//				index = equation.rightSideList().indexOf(block);
//				equation.rightSideList().set(index, new BlockHolder());
//			}
//		}
//		
//		block.remove();
//		parent.update(0);
//		
//		return block.getDraggingSprite();
//	}
	
	public List<Integer> getDroppableBases(Block toDrop, EquationBlockIndex startingIndex) {
		List<Integer> blocks = new ArrayList<Integer>();
		Side startingSide = startingIndex.expressionIndex < equation.leftSide.size() ? Side.Left : Side.Right;

		Block inverse = (Block) toDrop.inverse().copy();
		inverse.showInverse();
		Block leftDrop = startingSide == Side.Left ? toDrop : inverse;
		Block rightDrop = startingSide == Side.Right? toDrop : inverse;
		
		blocks.addAll(getDroppableBlocks(leftDrop, equation.leftSide, 0));
		blocks.addAll(getDroppableBlocks(rightDrop, equation.rightSide, equation.leftSide.size()));
		return blocks;
	}
	
	private List<Integer> getDroppableBlocks(Block toDrop, List<BaseBlock> side, int indexOffset) {
		List<Integer> blocks = new ArrayList<Integer>();
		for (int i = 0; i < side.size(); i++) {
			if (side.get(i).canAccept(toDrop)) blocks.add(i + indexOffset);
		}
		return blocks;
	}
	
	
//	public boolean drop(Block dragging, BaseBlock target) {
//		
//		if (target instanceof BlockHolder) {
//			// if dropped on a BlockHolder, replace it with the dropped Block
//			
//			if (dragging instanceof VerticalModifierBlock) {
//				// 0 * or / n = 0
//				return false;
//			} else {
//				if (dragging instanceof HorizontalModifierBlock) {
//					// turn a HorizontalModifier into a NumberBlock
//					NumberBlockProxy proxy = ((HorizontalModifierBlock) dragging).getProxy(false);
//					dragging = proxy;
//				}
//				
////				swapExpression(getContaining(target), target, (BaseBlock) dragging);
//				dragging.update(0);
//			}
//			
//		} else {
//			
//			ModifierBlock added = target.addBlock(dragging, false);
//			if (added == null) {
//				// this happens when the drop requires a trip to the SolveScreen to resolve
//				// when we get back, we use these values to reset the Block if the solve failed
////				tempDragging = dragging;
////				tempDraggingFrom = draggingFrom;
//			}
//			target.update(0);
//		}
//		
//		return false;
//	}
	
	public List<ModifierBlock> getNegatableBlocks() {
		List<ModifierBlock> blocks = new ArrayList<ModifierBlock>();
		
		return blocks;
	}
	
	public void performAction(DragAction action) {
		dragBlock(equation.getBlock(action.fromIndex));
		if (getSideFromBaseIndex(action.fromIndex.expressionIndex) != 
				getSideFromBaseIndex(action.toIndex)) {
			dragging = invertBlock(dragging);
		}
		dropBlock(equation.getBaseBlock(action.toIndex));
	}
}
