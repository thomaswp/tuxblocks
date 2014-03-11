package tuxkids.tuxblocks.core.solve.blocks;

import java.util.List;

import tuxkids.tuxblocks.core.solve.markup.Renderer;


public class MutableEquation extends Equation {

	public List<BaseBlock> leftSideList() {
		return leftSide;
	}
	
	public List<BaseBlock> rightSideList() {
		return rightSide;
	}
	
	public MutableEquation(List<BaseBlock> leftSide, List<BaseBlock> rightSide) {
		super(leftSide, rightSide);
	}
	
	public MutableEquation() {
		super();
	}
	
	/** Creates a {@link Renderer} for this Equation */
	@Override
	public Renderer renderer() { 
		return createRenderer();
	}
}
