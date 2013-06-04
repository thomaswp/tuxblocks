package tuxkids.tuxblocks.core.blocks;

import playn.core.Color;

public class VariableBlock extends Block {

	private String symbol;
	
	public VariableBlock(String symbol) {
		this.symbol = symbol;
		sprite = generateSprite(NUM_SIZE, NUM_SIZE);
	}

	@Override
	public int getColor() {
		return Color.rgb(200, 0, 150);
	}

	@Override
	public String getText() {
		return symbol;
	}

	@Override
	public float getWidth() {
		return NUM_SIZE;
	}

	@Override
	public float getHeight() {
		return NUM_SIZE;
	}

}
