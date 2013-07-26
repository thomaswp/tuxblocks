package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Color;

public class MinusBlock extends HorizontalModifierBlock {
	
	public MinusBlock(int value) {
		super(value);
	}
	
	protected MinusBlock(PlusBlock inverse) {
		super(inverse);
	}

	@Override
	protected String operator() {
		return "-";
	}

	@Override
	protected int color() {
//		return getColor(180);
		return Color.rgb(0x11, 0x4C, 0xA3);
	}

	@Override
	protected int plusValue() {
		return -value;
	}

	@Override
	protected ModifierBlock inverseChild() {
		return new PlusBlock(this);
	}

	@Override
	protected ModifierBlock copyChild() {
		return new MinusBlock(value);
	}

	@Override
	protected void setPlusValue(int value) {
		setValue(-value);
	}

}
