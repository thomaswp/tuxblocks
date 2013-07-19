package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import tuxkids.tuxblocks.core.utils.HashCode;

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

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(symbol);
	}
	
	@Override
	public boolean canRelease(boolean multiExpression) {
		return false;
	}

	@Override
	public BlockSprite inverse() {
		return null;
	}

}
