package tuxkids.tuxblocks.core.blocks;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Layer;
import playn.core.PlayN;

public class NumberBlock extends Block {

	private int value;
	
	public NumberBlock(int value) {
		this.value = value;
		sprite = generateSprite(NUM_SIZE, NUM_SIZE);
	}

	@Override
	public int getColor() {
		return Color.rgb(200, 150, 0);
	}

	@Override
	public String getText() {
		return "" + value;
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
