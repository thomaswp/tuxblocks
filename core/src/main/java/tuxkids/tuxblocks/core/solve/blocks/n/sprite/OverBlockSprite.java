package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import tuxkids.tuxblocks.core.Constant;

public class OverBlockSprite extends VerticalModifierSprite {

	public OverBlockSprite(int value) {
		super(value);
	}
	
	public OverBlockSprite(TimesBlockSprite inverse) {
		super(inverse);
	}

	@Override
	protected String operator() {
		return Constant.DIVIDE_SYMBOL;
	}

	@Override
	public ModifierBlockSprite copyChild() {
		return new OverBlockSprite(value);
	}

	@Override
	protected ModifierBlockSprite inverseChild() {
		return new TimesBlockSprite(this);
	}

}
