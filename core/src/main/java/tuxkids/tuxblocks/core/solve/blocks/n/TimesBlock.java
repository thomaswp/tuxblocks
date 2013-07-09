package tuxkids.tuxblocks.core.solve.blocks.n;

public class TimesBlock extends VerticalBlock {

	public TimesBlock(int value) {
		super(value);
	}

	@Override
	protected boolean isInverseOperation(ModifierBlock block) {
		return block instanceof OverBlock;
	}
	
	@Override
	protected String toMathString(String base) {
		return value + "(" + base + ")";
	}

}
