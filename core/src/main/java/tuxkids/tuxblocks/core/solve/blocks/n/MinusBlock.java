package tuxkids.tuxblocks.core.solve.blocks.n;

public class MinusBlock extends HorizontalBlock {

	public MinusBlock(int value) {
		super(value);
	}

	@Override
	protected boolean isInverseOperation(ModifierBlock block) {
		return block instanceof PlusBlock;
	}

	@Override
	protected String symbol() {
		return "-";
	}

}
