package tuxkids.tuxblocks.core.solve.blocks.n;

public class PlusBlock extends HorizontalBlock {

	public PlusBlock(int value) {
		super(value);
	}

	@Override
	protected boolean isInverseOperation(ModifierBlock block) {
		return block instanceof MinusBlock;
	}

	@Override
	protected String toMathString(String base) {
		return base + " + " + value;
	}
}
