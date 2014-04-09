package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;

import playn.core.ImageLayer;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.action.FinishSimplifyAction;
import tuxkids.tuxblocks.core.solve.blocks.layer.BlockLayerDefault;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.Aggregator;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.ButtonFactory;
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
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * A {@link BaseBlock} that is a single integer (positive or negative).
 */
public class NumberBlock extends BaseBlock implements Simplifiable {

	protected int value;
	// these are the only Blocks which can be simplified
	protected SimplifyLayer simplifyLayer;
	
	@Override
	public NumberBlock inverse() {
		return this;
	}
	
	public NumberBlock(int value) {
		this.value = value;
	}

	/** Returns the number held by this NumberBlock */
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
			return COLOR_NEUTRAL;
		} else if (value > 0) {
			return COLOR_PLUS;
		} else {
			return COLOR_MINUS;
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

	/** 
	 * Returns a {@link HorizontalModifierBlock} that this blocks could be
	 * the proxy for. This could occur if a HorizontalModifierBlock is turned
	 * into a NumberBlock at some point; it is then stored as a proxy within that
	 * NumberBlock. By default, this will just return a new ModifierBlock, but 
	 * {@link NumberBlockProxy} actually stores an old HorizontalModifierBlock
	 * and will return it. 
	 */
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
	
	/** Initializes a proxy block and aligns it into place */
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
		// if there are no horizontal modifiers on this block...
		if ((modifiers.modifiers == null || modifiers.modifiers.modifiers == null) 
				&& modifiers.children.size() == 0) {
			// get the vertical modifiers
			ArrayList<VerticalModifierBlock> vMods = new ArrayList<VerticalModifierBlock>();
			modifiers.addVerticalModifiersTo(vMods);

			// if the last one is a "-"...
			VerticalModifierBlock last = vMods.size() > 0 ? vMods.get(vMods.size() - 1) : null;
			if (last instanceof TimesBlock && last.value == -1) {
				// just pop it
				modifiers.addNegative();
			} else {
				// otherwise, flip our sign
				value = -value;
				if (hasSprite()) ((BlockLayerDefault) layer).setText("" + value);
			}
		} else {
			// otherwise, propagate upwards
			super.showInverse();
		}
	}

	@Override
	protected Sprite copyChild() {
		return new NumberBlock(value);
	}

	private enum Tag {
		Horizontal, Times, Over
	}
	
	@Override
	public void addSimplifiableBlocks(Aggregator ag) {
		if (modifiers.children.size() > 0) {
			// simplify with the closest HorizontalModifierBlock
			ag.add(this, modifiers.children.get(0), Tag.Horizontal);
		} else if (modifiers.modifiers != null) {
			// or if there isn't one, simplify with any direct VerticalModifierBlock
			VerticalModifierGroup mods = (VerticalModifierGroup) modifiers.modifiers;
			if (mods.timesBlocks.size() > 0) {
				ag.add(this, mods.timesBlocks.get(0), Tag.Times);
			}
			if (mods.overBlocks.size() > 0) {
				if (value % mods.overBlocks.get(0).value == 0) {
					// only add divisible OverBlocks
					ag.add(this, mods.overBlocks.get(0), Tag.Over);
				}
			}
		}
	}
	
	@Override
	public void placeButton(Block sprite, ModifierBlock pair,
			Object tag, ButtonFactory factory) {
		ImageLayer simplifyButton = factory.getSimplifyButton(sprite, pair);
		if (tag == Tag.Horizontal) {
			simplifyButton.setTranslation(width(), height() / 2);
		} else if (tag == Tag.Times) {
			simplifyButton.setTranslation(width() / 2, 0);
		} else {
			simplifyButton.setTranslation(width() / 2, height());
		}
	}

	@Override
	public void simplify(final Block base, final ModifierBlock pair) { //ignore pair argument
		if (blockListener != null) { // again, not sure why this check is necessary but...

			final int answer; // the answer to the problem 
			boolean autoAnswer; // should this go to the NumberSelectScreen?
			int level; // the level of the problem
			Stat stat; // the stat of the problem
			int start = value; // the start value for the NumberSelectScreen
			
			// create the renderer
			Renderer renderer = new BaseRenderer("" + value);
			int[] operands = new int[] { pair.value };
			if (pair instanceof TimesBlock) {
				TimesBlock times = (TimesBlock) pair;
				answer = value * times.value;
				renderer = new TimesRenderer(renderer, operands);
				stat = Stat.Times;
				level = Difficulty.rankTimes(value, times.value);
				autoAnswer = value == 1 || times.value == 1;
				if (value * times.value < 0) start = 0;
			} else if (pair instanceof OverBlock) {
				OverBlock over = (OverBlock) pair;
				answer = value / over.value;
				renderer = new OverRenderer(renderer, operands);
				stat = Stat.Over;
				level = Difficulty.rankOver(value, over.value);
				start = 0;
				autoAnswer = over.value == 1;
			} else if (pair instanceof HorizontalModifierBlock) {
				HorizontalModifierBlock plus = (HorizontalModifierBlock) pair;
				answer = value + plus.plusValue();
				operands[0] = plus.plusValue();
				renderer = new AddRenderer(renderer, operands);
				stat = plus.plusValue() >= 0 ? Stat.Plus : Stat.Minus;
				level = Difficulty.rankPlus(value, plus.value);
				autoAnswer = plus.value == 0 || -value == plus.plusValue();
			} else {
				// something went wrong
				return;
			}
			
			renderer = new JoinRenderer(renderer, new BlankRenderer(), "=");
			
			SimplifyListener listener = new SimplifyListener() {
				@Override
				public void wasSimplified(int fails, boolean success) {
					if (blockListener != null) {
						blockListener.wasSimplified(base, pair, null, fails, success);
					}
					if (success) {
						// change this block's value, remove the modifier
						setValue(answer);
						pair.group.removeChild(pair, true);
					}
				}
			};
			
			if (autoAnswer) {
				listener.wasSimplified(FinishSimplifyAction.AUTO_SIMPLIFY, true);
			} else {
				blockListener.wasReduced(renderer, answer, start, stat, level, listener);
			}
		}
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if (hasSprite()) simplifyLayer.update();
	}

	public void setValue(int value) {
		this.value = value;
		if (hasSprite()) ((BlockLayerDefault) layer).setText(text());
	}

	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		super.persist(data);
		value = data.persist(value);
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new NumberBlock(0);
			}
		};
	}

	@Override
	protected int getBaseValue(int answer) {
		return value;
	}
}
