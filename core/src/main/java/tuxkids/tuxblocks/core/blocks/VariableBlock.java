package tuxkids.tuxblocks.core.blocks;

import playn.core.Color;
import tuxkids.tuxblocks.core.expression.Variable;

public class VariableBlock extends BaseBlock {

	private String symbol;
	
	public VariableBlock(Variable variable) {
		super(variable);
		this.symbol = variable.getName();
		sprite = generateSprite(BASE_SIZE, BASE_SIZE, symbol, Color.rgb(200, 0, 200));
	}

}
