package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Color;
import tuxkids.tuxblocks.core.solve.expression.Variable;

public class VariableBlock extends BaseBlock {

	private Variable variable;
	
	public VariableBlock(Variable variable) {
		super(variable);
		this.variable = variable;
		layer = generateSprite(BASE_SIZE, BASE_SIZE, variable.getName(), getColor());
	}

	@Override
	protected boolean canSimplify() {
		return false;
	}

	@Override
	public int getColor() {
		return Color.rgb(200, 0, 200);
	}

	@Override
	protected String getText() {
		return variable.toMathString();
	}

}
