package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Color;
import tuxkids.tuxblocks.core.utils.persist.Persistable;
import tuxkids.tuxblocks.core.utils.persist.Persistable.Constructor;

public class MinusBlock extends HorizontalModifierBlock {
	
	public MinusBlock(int value) {
		super(value);
	}
	
	protected MinusBlock(PlusBlock inverse) {
		super(inverse);
	}

	@Override
	protected String operator() {
		return "-";
	}

	@Override
	protected int color() {
		return COLOR_MINUS;
	}

	@Override
	protected int plusValue() {
		return -value;
	}

	@Override
	protected ModifierBlock inverseChild() {
		return new PlusBlock(this);
	}

	@Override
	protected ModifierBlock copyChild() {
		return new MinusBlock(value);
	}

	@Override
	protected void setPlusValue(int value) {
		setValue(-value);
	}
	
	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new MinusBlock(0);
			}
		};
	}

}
