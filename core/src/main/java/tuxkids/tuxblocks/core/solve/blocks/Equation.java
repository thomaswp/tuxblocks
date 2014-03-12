package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.utils.MultiList;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * Represents an equation as two lists of {@link BaseBlock},
 * one on each side of the equals. Note that once these lists are
 * provided they cannot be changed. If you wish to build an Equation,
 * use the {@link Builder} class.
 */
public class Equation extends PlayNObject implements Persistable, Iterable<BaseBlock> {

	protected final List<BaseBlock> leftSide = new ArrayList<BaseBlock>(), 
			rightSide = new ArrayList<BaseBlock>();
	@SuppressWarnings("unchecked")
	protected final MultiList<BaseBlock> allBlocks = new MultiList<BaseBlock>(leftSide, rightSide);
	private Renderer renderer;
	
	/** Returns the left side of the equation. */
	public Iterable<BaseBlock> leftSide() {
		return leftSide;
	}
	
	public int leftCount() {
		return leftSide.size();
	}
	
	/** Returns the right side of the equation. */
	public Iterable<BaseBlock> rightSide() {
		return rightSide;
	}
	
	public int rightCount() {
		return rightSide.size();
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
	
	/** Returns a {@link Renderer} for this Equation */
	public Renderer renderer() {
		if (renderer == null) renderer = createRenderer();
		return renderer;
	}
	
	/** Gets or creates a plain text representation of this equation. */
	public String getPlainText() {
		return renderer().getPlainText();
	}
	
	protected Equation() { }
	
	/** Constructions an Equation from the given left and right sides */
	public Equation(List<BaseBlock> leftSide, List<BaseBlock> rightSide) {
		this.leftSide.addAll(leftSide);
		this.rightSide.addAll(rightSide);
	}
	
	protected Renderer createRenderer() {
		Renderer lhs = getRenderer(leftSide);
		Renderer rhs = getRenderer(rightSide);
		return new JoinRenderer(lhs, rhs, "=");
	}
	
	private Renderer getRenderer(List<BaseBlock> side) {
		Renderer renderer = null;
		for (BaseBlock base : side) {
			if (base instanceof BlockHolder) continue;
			Renderer toAdd = base.createRenderer();
			if (renderer == null) renderer = toAdd;
			else {
				renderer = new JoinRenderer(renderer, toAdd, "+");
			}
		}
		if (renderer == null) renderer = new BaseRenderer("0");
		return renderer;
	}

	private Builder createCopyBuilder() {
		Builder builder = new Builder();
		for (BaseBlock block : leftSide) {
			builder.addLeft((BaseBlock) block.copy());
		}
		for (BaseBlock block : rightSide) {
			builder.addRight((BaseBlock) block.copy());
		}
		return builder;
	}
	
	/** Returns a deep copy of this Equation */
	public Equation copy() {
		return createCopyBuilder().createEquation();
	}
	
	/** Returns a mutable deep copy of this Equation */
	public MutableEquation mutableCopy() {
		return createCopyBuilder().createMutableEquation();
	}
	
	
	public EquationBlockIndex indexOf(Block block) {
		int i = 0;
		for (BaseBlock expression : this) {
			ExpressionBlockIndex index = expression.indexOf(block);
			if (index != null) return new EquationBlockIndex(i, index);
			i++;
		}
		return null;
	}
	
	public Block getBlock(EquationBlockIndex index) {
		if (index.expressionIndex < leftSide.size()) {
			return leftSide.get(index.expressionIndex).getBlockAtIndex(index.blockIndex);
		} else if (index.expressionIndex - leftSide.size() < rightSide.size()) {
			return rightSide.get(index.expressionIndex - leftSide.size()).getBlockAtIndex(index.blockIndex);
		}
		return null;
	}
	
	public BaseBlock getBaseBlock(int index) {
		if (index < leftSide.size()) return leftSide.get(index);
		int rIndex = index - leftSide.size();
		if (rIndex < rightSide.size()) return rightSide.get(rIndex);
		return null;
	}

	/** An empty equation */
	public static final Equation NOOP = new Builder()
	.addLeft(new BlockHolder())
	.addRight(new BlockHolder())
	.createEquation();
	
	/** 
	 * Helper class for building {@link Equation}s.
	 * Use this class as follows:
	 * <pre>
	 * new Builder().addLeft(new Block()).addRight(new Block()).createEquation();
	 * </pre> 
	 */
	public static class Builder {
		private final List<BaseBlock> leftSide = new ArrayList<BaseBlock>(), 
					rightSide = new ArrayList<BaseBlock>();
		
		public Builder addLeft(BaseBlock block) {
			leftSide.add(block);
			return this;
		}

		public Builder addRight(BaseBlock block) {
			rightSide.add(block);
			return this;
		}
		
		public Equation createEquation() {
			return new Equation(leftSide, rightSide);
		}

		public MutableEquation createMutableEquation() {
			return new MutableEquation(leftSide, rightSide);
		}
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new Equation(new ArrayList<BaseBlock>(), 
						new ArrayList<BaseBlock>());
			}
		};
	}
	
	@Override
	public void persist(Data data) throws ParseDataException,
			NumberFormatException {
		data.persistList(leftSide);
		data.persistList(rightSide);
	}
}
