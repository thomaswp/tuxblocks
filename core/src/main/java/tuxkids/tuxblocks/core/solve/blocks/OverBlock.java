package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Color;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.utils.persist.Persistable;
import tuxkids.tuxblocks.core.utils.persist.Persistable.Constructor;

public class OverBlock extends VerticalModifierBlock {

	public OverBlock(int value) {
		super(value);
	}
	
	public OverBlock(TimesBlock inverse) {
		super(inverse);
	}

	@Override
	protected String operator() {
		return Constant.DIVIDE_SYMBOL;
	}

	@Override
	public ModifierBlock copyChild() {
		return new OverBlock(value);
	}

	@Override
	protected ModifierBlock inverseChild() {
		return new TimesBlock(this);
	}

	@Override
	protected int color() {
		return COLOR_OVER;
	}
	
	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new OverBlock(0);
			}
		};
	}

}
