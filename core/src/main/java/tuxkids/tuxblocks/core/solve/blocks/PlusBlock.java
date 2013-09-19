package tuxkids.tuxblocks.core.solve.blocks;

import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * A {@link ModifierBlock} that represents addition of
 * an integer.
 */
public class PlusBlock extends HorizontalModifierBlock {

	// Nothing to see here, move along.
	
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
		return COLOR_PLUS;
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
