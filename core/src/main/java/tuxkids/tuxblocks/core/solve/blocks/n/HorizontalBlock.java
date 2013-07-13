package tuxkids.tuxblocks.core.solve.blocks.n;

import tuxkids.tuxblocks.core.utils.Formatter;

public abstract class HorizontalBlock extends ModifierBlock {
	
	
	public HorizontalBlock(int value) {
		super(value);
	}

	@Override
	public boolean canSimplify() {
		int index = group.blocks.indexOf(this);
		if (index > 0) {
//			ModifierBlock before = group.blocks.get(index - 1);
//			return !isInverseOperation(before);
			return true;
		} else {
			return group.modifiesNumber();	
		}
	}

	@Override
	protected String toMathString(String base) {
		return Formatter.format("%s %s %d", base, symbol(), value);
	}

	@Override
	public boolean canReduce() {
		int index = group.blocks.indexOf(this);
		if (index > 0) {
			ModifierBlock block = group.blocks.get(index - 1);
			if (isInverseOperation(block) && block.value == value) return true;
		}
		return false;
	}

	@Override
	public boolean canRelease(boolean openBlock) {
		return openBlock || !group.isModifiedVertically();
	}
}
