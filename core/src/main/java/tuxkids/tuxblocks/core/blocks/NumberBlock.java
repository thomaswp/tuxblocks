package tuxkids.tuxblocks.core.blocks;

import playn.core.Color;
import tuxkids.tuxblocks.core.expression.Expression;

public class NumberBlock extends BaseBlock {
	
	public NumberBlock(tuxkids.tuxblocks.core.expression.Number number) {
		super(number);
		sprite = generateSprite(BASE_SIZE, BASE_SIZE, "" + number.getValue(), 
				Color.rgb(200, 200, 0));
	}
}
