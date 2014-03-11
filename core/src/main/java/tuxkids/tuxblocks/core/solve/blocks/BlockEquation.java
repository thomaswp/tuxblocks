package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;
import tuxkids.tuxblocks.core.utils.MultiList;

public class BlockEquation implements Hashable, Iterable<BaseBlock> {

	private List<BaseBlock> leftSide = new ArrayList<BaseBlock>(), 
			rightSide = new ArrayList<BaseBlock>();
	@SuppressWarnings("unchecked")
	private MultiList<BaseBlock> allBlocks = new MultiList<BaseBlock>(leftSide, rightSide);
	
	public List<BaseBlock> leftSide() {
		return leftSide;
	}
	
	public List<BaseBlock> rightSide() {
		return rightSide;
	}
	
	public Iterable<BaseBlock> allBlocks() {
		return allBlocks;
	}
	
	/** Clears this equation and destroys any associated {@link Sprite}s */
	public void clear() {
		for (BaseBlock sprite : allBlocks) {
			sprite.destroy();
		}
		leftSide.clear();
		rightSide.clear();
	}

	/** Returns the number of {@link BaseBlock}s in this equation. */
	public int count() {
		return allBlocks.size();
	}

	@Override
	public Iterator<BaseBlock> iterator() {
		return allBlocks.iterator();
	}
	
	/** Converts this to a plain Equation */
	public Equation toEquation() {
		ArrayList<BaseBlock> lhs = new ArrayList<BaseBlock>(),
				rhs = new ArrayList<BaseBlock>();
		for (BaseBlock sprite : leftSide) {
			lhs.add((BaseBlock) sprite.copy());
		}
		for (BaseBlock sprite : rightSide) {
			rhs.add((BaseBlock) sprite.copy());
		}
		return new Equation(lhs, rhs);
	}
	
	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(leftSide);
		hashCode.addField(rightSide);
	}

}
