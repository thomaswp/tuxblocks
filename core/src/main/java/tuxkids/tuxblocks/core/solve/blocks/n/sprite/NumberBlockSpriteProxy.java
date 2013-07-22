package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

public class NumberBlockSpriteProxy extends NumberBlockSprite {

	HorizontalModifierSprite proxyFor;
	
	@Override
	public ModifierBlockSprite proxyFor() {
		return proxyFor == null ? super.proxyFor() : alignProxy(proxyFor);
	}
	
	public NumberBlockSpriteProxy(int value, HorizontalModifierSprite proxyFor) {
		super(value);
		this.proxyFor = proxyFor;
	}

	@Override
	public void showInverse() {
		super.showInverse();
		proxyFor = null;
	}
}
