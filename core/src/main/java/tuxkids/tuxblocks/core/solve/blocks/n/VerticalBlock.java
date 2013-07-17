package tuxkids.tuxblocks.core.solve.blocks.n;

public abstract class VerticalBlock extends ModifierBlock {

	public VerticalBlock(int value) {
		super(value);
	}

	@Override
	public boolean canSimplify() {
		int index = group.blocks.indexOf(this);
		if (index > 0) {
			for (int i = index - 1; i >= 0; i--) {
				ModifierBlock block = group.blocks.get(i);
				if (!isInverseOperation(block)) return true;
			}
			return false;
		} else {
			return group.modifiesNumber();	
		}
	}

	@Override
	public boolean canReduce() {
		for (ModifierBlock block : group.blocks) {
			if (isInverseOperation(block) && block.value == value) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canRelease(boolean openBlock) {
		if (group == null) return false;
		return !group.isModifiedHorizontally();
	}

}
