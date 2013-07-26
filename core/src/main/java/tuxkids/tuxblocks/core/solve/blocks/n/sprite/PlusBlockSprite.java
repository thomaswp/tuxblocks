package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Color;

public class PlusBlockSprite extends HorizontalModifierSprite {

	public PlusBlockSprite(int value) {
		super(value);
	}
	
	protected PlusBlockSprite(MinusBlockSprite inverse) {
		super(inverse);
	}

	@Override
	protected String operator() {
		return "+";
	}

	@Override
	protected int plusValue() {
		return value;
	}

	@Override
	protected int color() {
//		return getColor(0);
		return Color.rgb(0xF7, 0x04, 0x04);
	}

	@Override
	protected ModifierBlockSprite inverseChild() {
		return new MinusBlockSprite(this);
	}

	@Override
	protected ModifierBlockSprite copyChild() {
		return new PlusBlockSprite(value);
	}

	@Override
	protected void setPlusValue(int value) {
		setValue(value);
	}
}
