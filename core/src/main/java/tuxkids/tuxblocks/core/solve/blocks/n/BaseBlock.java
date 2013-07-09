package tuxkids.tuxblocks.core.solve.blocks.n;

public abstract class BaseBlock extends HorizontalGroup {

	public abstract boolean isNumber();
	public abstract String toMathString();

	public boolean canAccept(BaseBlock baseBlock) {
		if (baseBlock.isNumber()) return false;
		return false;
	}

	public void accept(BaseBlock baseBlock) {

	}

	@Override
	public boolean modifiesNumber() {
		return isNumber();
	}
	
	@Override
	public boolean canRelease(boolean openBlock) {
		return openBlock;
	}
}
