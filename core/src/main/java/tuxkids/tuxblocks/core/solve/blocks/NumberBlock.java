package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Color;
import tuxkids.tuxblocks.core.solve.expression.Expression;

public class NumberBlock extends BaseBlock {
	
	public NumberBlock(tuxkids.tuxblocks.core.solve.expression.Number number) {
		super(number);
		sprite = generateSprite(BASE_SIZE, BASE_SIZE, "" + number.getValue(), 
				Color.rgb(200, 200, 0));
	}
}
