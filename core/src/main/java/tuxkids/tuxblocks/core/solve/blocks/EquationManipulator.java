package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.solve.blocks.BlockController.Side;

public class EquationManipulator {
	private MutableEquation equation;
	
	public Equation equation() {
		return equation;
	}
	
	private EquationManipulator(Equation equation) {
		this.equation = equation.mutableCopy();
	}
	
	/** 
	 * Returns whether or not BaseBlocks on the given side of the equation have multiple expressions 
	 * and therefore cannot directly manipulate factors.  
	 */
	public static boolean isMultiExpression(Equation equation, List<BaseBlock> side) {
		int totalBlocks = 0;
		for (BaseBlock s : equation) {
			if (!(s instanceof BlockHolder)) totalBlocks++;
		}
		boolean multiExpression = totalBlocks > 2; // can't drag factors if there are >2 expressions
		if (!multiExpression) {
			int bb = 0;
			for (BaseBlock s : side) {
				if (!(s instanceof BlockHolder)) bb++;
			}
			if (bb > 1) multiExpression = true; // or if there is >1 expression on any given side
		}
		return multiExpression;
	}
	
	public List<Block> getDraggableBlocks() {
		List<Block> blocks = new ArrayList<Block>();
		blocks.addAll(getDraggableBlocks(equation.leftSideList()));
		blocks.addAll(getDraggableBlocks(equation.rightSideList()));
		return blocks;
	}
	
	private List<Block> getDraggableBlocks(List<BaseBlock> side) {
		List<Block> blocks = new ArrayList<Block>();
		boolean multiExpression = isMultiExpression(equation, side);
		for (BaseBlock baseBlock : side) {
			for (Block block : baseBlock.getAllBlocks()) {
				if (block.canRelease(multiExpression)) {
					blocks.add(block);
				}
			}
		}
		return blocks;
	}
	
	public Block drag(Block block) {
		BaseBlock parent = null;
		for (BaseBlock base : equation) {
			if (base.contains(block)) {
				parent = base;
				break;
			}
		}
		
		if (parent == null) return null;
		
		if (parent == block) {
			// Picking up a BaseBlock
			int index = equation.leftSideList().indexOf(block);
			if (index >= 0) {
				equation.leftSideList().set(index, new BlockHolder());
			} else {
				index = equation.rightSideList().indexOf(block);
				equation.rightSideList().set(index, new BlockHolder());
			}
		}
		
		block.remove();
		for (BaseBlock base : equation) {
			base.update(0);
		}
		
		return block.getDraggingSprite();
	}
	
	public List<BaseBlock> getDroppableBlocks(Block toDrop, Side startingSide) {
		List<BaseBlock> blocks = new ArrayList<BaseBlock>();
		List<BaseBlock> thisSide = startingSide == Side.Left ? equation.leftSide : equation.rightSide;
		List<BaseBlock> otherSide = startingSide == Side.Left ? equation.rightSide : equation.leftSide;
		blocks.addAll(getDroppableBlocks(toDrop, thisSide));
		Block inverse = (Block) toDrop.inverse().copy();
		inverse.showInverse();
		blocks.addAll(getDroppableBlocks(inverse, otherSide));
		return blocks;
	}
	
	private List<BaseBlock> getDroppableBlocks(Block toDrop, List<BaseBlock> side) {
		List<BaseBlock> blocks = new ArrayList<BaseBlock>();
		for (BaseBlock baseBlock : side) {
			if (baseBlock.canAccept(toDrop)) blocks.add(baseBlock);
		}
		return blocks;
	}
	
	public boolean drop(Block toDrop, BaseBlock dropOn) {
		return false;
	}
	
	public List<ModifierBlock> getNegatableBlocks() {
		List<ModifierBlock> blocks = new ArrayList<ModifierBlock>();
		
		return blocks;
	}
	
	public void negate(ModifierBlock block) {
		
	}
}
