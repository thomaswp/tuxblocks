package tuxkids.tuxblocks.core.solve.blocks;

import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.solve.blocks.layer.BlockLayer;
import tuxkids.tuxblocks.core.solve.blocks.layer.TimesLayer;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

public class TimesBlock extends VerticalModifierBlock{
	
	public TimesBlock(int value) {
		super(value);
	}
	
	protected TimesBlock(OverBlock inverse) {
		super(inverse);
	}
	
	protected TimesBlock(TimesBlock inverse) {
		super(inverse);
	}

	@Override
	protected String operator() {
		return Constant.TIMES_SYMBOL;
	}
	
	@Override
	public int color() {
		if (value == -1) {
			return COLOR_NEUTRAL;
		} else {
			return COLOR_TIMES;
		}
	}
	
	@Override
	public String text() {
		if (value == -1) {
			return "-";
		} else {
			return super.text();
		}
	}
	
	@Override
	public boolean canSimplify() {
		if (value == -1) {
			if (group == null) return false;
			return group.children.lastIndexOf(inverse) != group.children.indexOf(inverse);
		}
		return super.canSimplify();
	}

	@Override
	protected BlockLayer generateImage(String text) {
		return new TimesLayer(textFormat, text());
	}

	@Override
	public ModifierBlock copyChild() {
		return new TimesBlock(value);
	}

	@Override
	protected ModifierBlock inverseChild() {
		if (value == -1) return new TimesBlock(this);
		return new OverBlock(this);
	}
	
	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new TimesBlock(0);
			}
		};
	}
}
