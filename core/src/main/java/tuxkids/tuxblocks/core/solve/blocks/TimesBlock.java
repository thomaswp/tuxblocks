package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.Color;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.solve.blocks.layer.BlockLayer;
import tuxkids.tuxblocks.core.solve.blocks.layer.TimesLayer;
import tuxkids.tuxblocks.core.utils.Persistable;
import tuxkids.tuxblocks.core.utils.Persistable.Constructor;
import tuxkids.tuxblocks.core.utils.Persistable.Data;
import tuxkids.tuxblocks.core.utils.Persistable.ParseDataException;

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
	
//	@Override
//	protected float defaultHeight() {
//		return modSize();
//	}
	
	@Override
	public int color() {
		if (value == -1) {
			return Color.rgb(150, 150, 150);
		} else {
			return Color.rgb(0xF7, 0x9D, 0x04);
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
