package tuxkids.tuxblocks.core.solve.blocks;

/**
 * A special type of {@link NumberBlock} that is created when
 * a {@link HorizontalModifierBlock} is removed from its 
 * {@link ModifierGroup} and must become a {@link BaseBlock}.
 * This class holds a reference to the original modifier block
 * (which is a "proxy for") so that if it need to transform back
 * it can do so with minimal overhead.
 */
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
