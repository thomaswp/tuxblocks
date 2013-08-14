package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;

import playn.core.Color;
import playn.core.Pointer.Listener;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.blocks.layer.BlockLayerDefault;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.Simplifiable;
import tuxkids.tuxblocks.core.solve.markup.AddRenderer;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.BlankRenderer;
import tuxkids.tuxblocks.core.solve.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.markup.OverRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.solve.markup.TimesRenderer;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.HashCode;

public class NumberBlock extends BaseBlock implements Simplifiable {

	protected int value;
	protected SimplifyLayer simplifyLayer;
	
	@Override
	public NumberBlock inverse() {
		return this;
	}
	
	public NumberBlock(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	@Override
	public void initSpriteImpl() {
		super.initSpriteImpl();
		simplifyLayer = new SimplifyLayer(this);
		groupLayer.add(simplifyLayer.layerAddable());
		simplifyLayer.setDepth(SIMPLIFY_DEPTH);
	}
	
	@Override
	public int color() {
		if (value == 0) {
			return Colors.GRAY;
		} else if (value > 0) {
			return Color.rgb(0xF7, 0x04, 0x04);
		} else {
			return Color.rgb(0x11, 0x4C, 0xA3);
		}
	}
	
	@Override
	public boolean showSimplify() {
		return blockListener != null && !blockListener.inBuildMode();
	}
	
	@Override
	protected String text() {
		return "" + value;
	}

	public ModifierBlock proxyFor() {
		ModifierBlock proxy;
		if (value >= 0) {
			proxy = alignProxy(new PlusBlock(value));
		} else {
			proxy = alignProxy(new MinusBlock(-value));
		}
		proxy.addBlockListener(blockListener);
		return proxy;
	}
	
	protected ModifierBlock alignProxy(ModifierBlock proxy) {
		if (hasSprite()) {
			proxy.initSprite();
			proxy.interpolateRect(groupLayer.tx(), groupLayer.ty(), width(), height(), 0, 1);
		}
		return proxy;
	}

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(value);
	}

	@Override
	public void showInverse() {
		if ((modifiers.modifiers == null || modifiers.modifiers.modifiers == null) && modifiers.children.size() == 0) {
			ArrayList<VerticalModifierBlock> vMods = new ArrayList<VerticalModifierBlock>();
			modifiers.addVerticalModifiersTo(vMods);
			VerticalModifierBlock last = vMods.size() > 0 ? vMods.get(vMods.size() - 1) : null;
			if (last != null && last instanceof TimesBlock && last.value == -1) {
				modifiers.addNegative();
			} else {
				value = -value;
				if (hasSprite()) ((BlockLayerDefault) layer).setText("" + value);
			}
		} else {
			super.showInverse();
		}
	}

	@Override
	protected Sprite copyChild() {
		return new NumberBlock(value);
	}

	@Override
	public void updateSimplify() {
		if (modifiers.children.size() > 0) {
			simplifyLayer.getSimplifyButton(modifiers.children.get(0))
			.setTranslation(width(), height() / 2);
		} else if (modifiers.modifiers != null) {
			VerticalModifierGroup mods = (VerticalModifierGroup) modifiers.modifiers;
			if (mods.timesBlocks.size() > 0) {
				simplifyLayer.getSimplifyButton(mods.timesBlocks.get(0))
				.setTranslation(width() / 2, 0);
			}
			if (mods.divBlocks.size() > 0) {
				if (value % mods.divBlocks.get(0).value == 0) {
					simplifyLayer.getSimplifyButton(mods.divBlocks.get(0))
					.setTranslation(width() / 2, height());
				}
			}
		}
	}

	@Override
	public void simplify(final ModifierBlock sprite, ModifierBlock pair) { //ignore pair
		if (blockListener != null) {
			final int answer;
			boolean autoAnswer;
			Renderer renderer = new BaseRenderer("" + value);
			int level;
			Stat stat;
			int[] operands = new int[] { sprite.value };
			if (sprite instanceof TimesBlock) {
				TimesBlock times = (TimesBlock) sprite;
				answer = value * times.value;
				renderer = new TimesRenderer(renderer, operands);
				stat = Stat.Times;
				level = Difficulty.rankTimes(value, times.value);
				autoAnswer = value == 1 || times.value == 1;
			} else if (sprite instanceof OverBlock) {
				OverBlock over = (OverBlock) sprite;
				answer = value / over.value;
				renderer = new OverRenderer(renderer, operands);
				stat = Stat.Over;
				level = Difficulty.rankOver(value, over.value);
				autoAnswer = over.value == 1;
			} else if (sprite instanceof HorizontalModifierBlock) {
				HorizontalModifierBlock plus = (HorizontalModifierBlock) sprite;
				answer = value + plus.plusValue();
				operands[0] = plus.plusValue();
				renderer = new AddRenderer(renderer, operands);
				stat = plus.plusValue() >= 0 ? Stat.Plus : Stat.Minus;
				level = Difficulty.rankPlus(value, plus.value);
				autoAnswer = plus.value == 0 || -value == plus.plusValue();
			} else {
				return;
			}
			
			renderer = new JoinRenderer(renderer, new BlankRenderer(), "=");
			
			SimplifyListener listener = new SimplifyListener() {
				@Override
				public void wasSimplified(boolean success) {
					if (success) {
						setValue(answer);
						sprite.group.removeChild(sprite, true);
						blockListener.wasSimplified();
					}
				}
			};
			
			if (autoAnswer) {
				listener.wasSimplified(true);
			} else {
				blockListener.wasReduced(renderer, answer, value, stat, level, listener);
			}
		}
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		simplifyLayer.update();
	}

	public void setValue(int value) {
		this.value = value;
		((BlockLayerDefault) layer).setText(text());
	}

}
