package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

public class OverBlockSprite extends VerticalModifierSprite {

	public OverBlockSprite(int value) {
		super(value);
	}

	@Override
	protected String operator() {
		return "\u00F7";
	}

	@Override
	public ModifierBlockSprite copyChild() {
		return new OverBlockSprite(value);
	}

}
