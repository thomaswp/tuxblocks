package tuxkids.tuxblocks.core.solve.blocks.n;

public class NumberBlock extends BaseBlock {

	private int value;
	
	public NumberBlock(int value) {
		this.value = value;
	}
	
	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public String text() {
		return "" + value;
	}

}
