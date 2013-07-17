package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

public class VariableBlockSprite extends BaseBlockSprite {

	protected String symbol;
	
	public VariableBlockSprite(String symbol) {
		super(symbol);
		this.symbol = symbol;
	}
	
	@Override
	protected String text() {
		return symbol;
	}

}
