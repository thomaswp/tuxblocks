package tuxkids.tuxblocks.core.solve.blocks.n;

public class HorizontalGroup extends BlockGroup<HorizontalBlock> {

	@Override
	protected BlockGroup<?> createModifiers() {
		return new VerticalGroup();
	}

	@Override
	public boolean canAdd(ModifierBlock block) {
		return block instanceof HorizontalBlock;
	}

}
