package tuxkids.tuxblocks.core.solve.blocks;

public class NumberBlockProxy extends NumberBlock {

	HorizontalModifierBlock proxyFor;
	
	@Override
	public ModifierBlock proxyFor() {
		return proxyFor == null ? super.proxyFor() : alignProxy(proxyFor);
	}
	
	public NumberBlockProxy(int value, HorizontalModifierBlock proxyFor) {
		super(value);
		this.proxyFor = proxyFor;
	}

	@Override
	public void showInverse() {
		super.showInverse();
		proxyFor = null;
	}
	
	@Override
	public void setValue(int value) {
		super.setValue(value);
		proxyFor = null;
	}
}
