package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Color;
import tuxkids.tuxblocks.core.solve.expression.Expression;

public class NumberBlock extends BaseBlock {
	
	private tuxkids.tuxblocks.core.solve.expression.Number number;
	
	public NumberBlock(tuxkids.tuxblocks.core.solve.expression.Number number) {
		super(number);
		this.number = number;
		sprite = generateSprite(BASE_SIZE, BASE_SIZE, "" + number.getValue(), getColor());
	}

	@Override
	protected boolean canSimplify() {
		return true;
	}

	@Override
	public int getColor() {
		return Color.rgb(200, 0, 200);
	}
	
}
