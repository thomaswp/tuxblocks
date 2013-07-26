package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Color;

public class MinusBlockSprite extends HorizontalModifierSprite {
	
	public MinusBlockSprite(int value) {
		super(value);
	}
	
	protected MinusBlockSprite(PlusBlockSprite inverse) {
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
	protected ModifierBlockSprite inverseChild() {
		return new PlusBlockSprite(this);
	}

	@Override
	protected ModifierBlockSprite copyChild() {
		return new MinusBlockSprite(value);
	}

	@Override
	protected void setPlusValue(int value) {
		setValue(-value);
	}

}
