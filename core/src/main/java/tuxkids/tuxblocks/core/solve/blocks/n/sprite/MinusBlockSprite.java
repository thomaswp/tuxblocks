package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

public class MinusBlockSprite extends HorizontalModifierSprite {
	
	public MinusBlockSprite(int value) {
		super(value);
	}

	@Override
	protected String operator() {
		return "-";
	}

}
