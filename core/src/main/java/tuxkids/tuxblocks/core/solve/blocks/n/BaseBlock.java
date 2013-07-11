package tuxkids.tuxblocks.core.solve.blocks.n;

public abstract class BaseBlock extends Block {

	public abstract boolean isNumber();
	public abstract String toMathString();
	
	private HorizontalGroup modifiers = new HorizontalGroup();
	
	public BaseBlock() {
		modifiers.base = this;
	}

	public boolean canAccept(BaseBlock baseBlock) {
		if (baseBlock.isNumber()) return false;
		return false;
	}

	public void accept(BaseBlock baseBlock) {

	}
	
	public void addModifier(ModifierBlock block) {
		modifiers.addModifier(block);
	}

	public boolean modifiesNumber() {
		return isNumber();
	}
	
	@Override
	public boolean canRelease(boolean openBlock) {
		return openBlock;
	}
	
	@Override 
	protected String toMathString(String base) {
		return modifiers.toMathString(base);
	}
}
