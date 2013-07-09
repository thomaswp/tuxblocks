package tuxkids.tuxblocks.core.solve.blocks.n;

public class OverBlock extends VerticalBlock {

	public OverBlock(int value) {
		super(value);
	}

	@Override
	protected boolean isInverseOperation(ModifierBlock block) {
		return block instanceof TimesBlock;
	}

	@Override
	protected String toMathString(String base) {
		return "(" + base + ") / " + value;
	}
}
