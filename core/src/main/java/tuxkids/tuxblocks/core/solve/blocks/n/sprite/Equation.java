package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public class Equation extends PlayNObject implements Hashable {

	private final List<BaseBlockSprite> leftSide, rightSide;
	private Renderer renderer;
	
	public List<BaseBlockSprite> leftSide() {
		return leftSide;
	}
	
	public List<BaseBlockSprite> rightSide() {
		return rightSide;
	}
	
	public Renderer renderer() {
		if (renderer == null) renderer = createRenderer();
		return renderer;
	}
	
	public Equation(List<BaseBlockSprite> leftSide, List<BaseBlockSprite> rightSide) {
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}
	
	private Renderer createRenderer() {
		Renderer lhs = getRenderer(leftSide);
		Renderer rhs = getRenderer(rightSide);
		return new JoinRenderer(lhs, rhs, "=");
	}
	
	private Renderer getRenderer(List<BaseBlockSprite> side) {
		Renderer renderer = null;
		for (BaseBlockSprite base : side) {
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
	
	
	public static class Builder {
		private final List<BaseBlockSprite> leftSide = new ArrayList<BaseBlockSprite>(), 
					rightSide = new ArrayList<BaseBlockSprite>();
		
		public Builder addLeft(BaseBlockSprite block) {
			leftSide.add(block);
			return this;
		}
		
		public Builder addRight(BaseBlockSprite block) {
			rightSide.add(block);
			return this;
		}
		
		public Equation createEquation() {
			return new Equation(leftSide, rightSide);
		}
	}
}
