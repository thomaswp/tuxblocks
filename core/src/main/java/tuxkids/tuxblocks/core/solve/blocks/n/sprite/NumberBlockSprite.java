package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;

import playn.core.Color;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.AddRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.BlankRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.OverRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.TimesRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.SimplifyLayer.Simplifiable;
import tuxkids.tuxblocks.core.utils.HashCode;

public class NumberBlockSprite extends BaseBlockSprite implements Simplifiable {

	protected int value;
	protected SimplifyLayer simplifyLayer;
	
	@Override
	public NumberBlockSprite inverse() {
		return this;
	}
	
	public NumberBlockSprite(int value) {
		this.value = value;
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
	protected String text() {
		return "" + value;
	}

	public ModifierBlockSprite proxyFor() {
		ModifierBlockSprite proxy;
		if (value >= 0) {
			proxy = alignProxy(new PlusBlockSprite(value));
		} else {
			proxy = alignProxy(new MinusBlockSprite(-value));
		}
		proxy.addBlockListener(blockListener);
		return proxy;
	}
	
	protected ModifierBlockSprite alignProxy(ModifierBlockSprite proxy) {
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
			ArrayList<VerticalModifierSprite> vMods = new ArrayList<VerticalModifierSprite>();
			modifiers.addVerticalModifiersTo(vMods);
			VerticalModifierSprite last = vMods.size() > 0 ? vMods.get(vMods.size() - 1) : null;
			if (last != null && last instanceof TimesBlockSprite && last.value == -1) {
				modifiers.addNegative();
			} else {
				value = -value;
				if (hasSprite()) ((BlockLayer) layer).setText("" + value);
			}
		} else {
			super.showInverse();
		}
	}

	@Override
	protected Sprite copyChild() {
		return new NumberBlockSprite(value);
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
	public void simplify(final ModifierBlockSprite sprite) {
		if (blockListener != null) {
			final int answer;
			Renderer renderer = new BaseRenderer("" + value);
			int[] operands = new int[] { sprite.value };
			if (sprite instanceof TimesBlockSprite) {
				TimesBlockSprite times = (TimesBlockSprite) sprite;
				answer = value * times.value;
				renderer = new TimesRenderer(renderer, operands);
			} else if (sprite instanceof OverBlockSprite) {
				OverBlockSprite times = (OverBlockSprite) sprite;
				answer = value / times.value;
				renderer = new OverRenderer(renderer, operands);
			} else if (sprite instanceof HorizontalModifierSprite) {
				HorizontalModifierSprite times = (HorizontalModifierSprite) sprite;
				answer = value + times.plusValue();
				renderer = new AddRenderer(renderer, operands);
			} else {
				return;
			}
			
			renderer = new JoinRenderer(renderer, new BlankRenderer(), "=");
			
			blockListener.wasReduced(renderer, answer, value, new SimplifyListener()  {
				@Override
				public void wasSimplified(boolean success) {
					if (success) {
						value = answer;
						((BlockLayer) layer).setText(text());
						sprite.group.removeChild(sprite, true);
						blockListener.wasSimplified();
						//do we need to worry about proxies?
					}
				}
			});
		}
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		simplifyLayer.update();
	}
}
