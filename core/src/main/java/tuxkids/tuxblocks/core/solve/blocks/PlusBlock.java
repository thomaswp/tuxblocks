package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Color;
import tuxkids.tuxblocks.core.utils.Persistable;

public class PlusBlock extends HorizontalModifierBlock {

	public PlusBlock(int value) {
		super(value);
	}
	
	protected PlusBlock(MinusBlock inverse) {
		super(inverse);
	}

	@Override
	protected String operator() {
		return "+";
	}

	@Override
	protected int plusValue() {
		return value;
	}

	@Override
	protected int color() {
//		return getColor(0);
		return Color.rgb(0xF7, 0x04, 0x04);
	}

	@Override
	protected ModifierBlock inverseChild() {
		return new MinusBlock(this);
	}

	@Override
	protected ModifierBlock copyChild() {
		return new PlusBlock(value);
	}

	@Override
	protected void setPlusValue(int value) {
		setValue(value);
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new PlusBlock(0);
			}
		};
	}
}
