package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

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
	protected int getPlusValue() {
		return value;
	}

	@Override
	protected ModifierBlockSprite inverseChild() {
		return new MinusBlockSprite(this);
	}

	@Override
	protected ModifierBlockSprite copyChild() {
		return new PlusBlockSprite(value);
	}
}
