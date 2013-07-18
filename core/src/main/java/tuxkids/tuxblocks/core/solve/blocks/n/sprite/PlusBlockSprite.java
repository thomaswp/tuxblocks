package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

public class PlusBlockSprite extends HorizontalModifierSprite {

	public PlusBlockSprite(int value) {
		super(value);
	}

	@Override
	protected String operator() {
		return "+";
	}

	@Override
	protected int getPlusValue() {
		return value;
	}
}
