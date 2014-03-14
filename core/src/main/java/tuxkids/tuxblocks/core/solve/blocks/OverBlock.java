package tuxkids.tuxblocks.core.solve.blocks;

import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * A {@link ModifierBlock} that represents division
 * by an integer.
 */
public class OverBlock extends VerticalModifierBlock {

	// There is absolutely nothing interesting here.
	// For once.
	
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
				return new OverBlock(1);
			}
		};
	}

}
