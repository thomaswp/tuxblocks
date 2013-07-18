package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import tuxkids.tuxblocks.core.utils.HashCode;

public class NumberBlockSprite extends BaseBlockSprite {

	protected int value;
	
	public NumberBlockSprite(int value) {
		super("" + value);
		this.value = value;
	}
	
	@Override
	protected String text() {
		return "" + value;
	}

	public ModifierBlockSprite proxyFor() {
		if (value >= 0) {
			return alignProxy(new PlusBlockSprite(value));
		} else {
			return alignProxy(new MinusBlockSprite(value));
		}
	}
	
	protected ModifierBlockSprite alignProxy(ModifierBlockSprite proxy) {
		proxy.interpolateRect(groupLayer.tx(), groupLayer.ty(), width(), height(), 0, 1);
		return proxy;
	}

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(value);
	}

}
