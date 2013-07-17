package tuxkids.tuxblocks.core.solve.blocks.n;

public class VerticalGroup extends BlockGroup<VerticalBlock> {

	@Override
	protected BlockGroup<?> createModifiers() {
		return new HorizontalGroup();
	}

	@Override
	public boolean canAdd(ModifierBlock block) {
		return block instanceof VerticalBlock;
	}

}
