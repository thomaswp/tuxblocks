package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public class Equation extends PlayNObject implements Hashable {

	private final List<BaseBlock> leftSide, rightSide;
	private Renderer renderer;
	
	public List<BaseBlock> leftSide() {
		return leftSide;
	}
	
	public List<BaseBlock> rightSide() {
		return rightSide;
	}
	
	public Renderer renderer() {
		if (renderer == null) renderer = createRenderer();
		return renderer;
	}
	
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
			Renderer toAdd = base.createRenderer();
			if (renderer == null) renderer = toAdd;
			else {
				renderer = new JoinRenderer(renderer, toAdd, "+");
			}
		}
		if (renderer == null) renderer = new BaseRenderer("0");
		return renderer;
	}
	
	@Override
	public void addFields(HashCode hashCode) {
		//TODO: should BaseBlockSprites == or equal...
	}

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
	

	public static final Equation NOOP = new Builder()
	.addLeft(new BlockHolder())
	.addRight(new BlockHolder())
	.createEquation();
	
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
}
