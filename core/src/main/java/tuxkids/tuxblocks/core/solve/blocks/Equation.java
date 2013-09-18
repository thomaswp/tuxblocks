package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * Represents an equation as two lists of {@link BaseBlock},
 * one on each side of the equals. Note that once these lists are
 * provided they cannot be changed. If you wish to build an Equation,
 * use the {@link Builder} class.
 */
public class Equation extends PlayNObject implements Persistable {

	private final List<BaseBlock> leftSide, rightSide;
	private Renderer renderer;
	
	/** Returns the left side of the equation. */
	public List<BaseBlock> leftSide() {
		return leftSide;
	}
	
	/** Returns the right side of the equation. */
	public List<BaseBlock> rightSide() {
		return rightSide;
	}
	
	/** Returns a {@link Renderer} for this Equation */
	public Renderer renderer() {
		if (renderer == null) renderer = createRenderer();
		return renderer;
	}
	
	/** Constructions an Equation from the given left and right sides */
	public Equation(List<BaseBlock> leftSide, List<BaseBlock> rightSide) {
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}
	
	private Renderer createRenderer() {
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

	/** Returns a deep copy of this Equation */
	public Equation copy() {
		Builder builder = new Builder();
		for (BaseBlock block : leftSide) {
			builder.addLeft((BaseBlock) block.copy());
		}
		for (BaseBlock block : rightSide) {
			builder.addRight((BaseBlock) block.copy());
		}
		return builder.createEquation();
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
