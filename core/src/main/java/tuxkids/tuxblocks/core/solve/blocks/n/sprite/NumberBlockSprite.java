package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

public class NumberBlockSprite extends BaseBlockSprite {

	protected int value;
	
	public NumberBlockSprite(int value) {
		super("" + value);
		this.value = value;
	}
	
	@Override
	protected String text() {
		return "" + value;
	}

}
