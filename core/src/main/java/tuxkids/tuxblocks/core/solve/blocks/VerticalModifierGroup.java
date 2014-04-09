package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.Aggregator;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.ButtonFactory;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.BlankRenderer;
import tuxkids.tuxblocks.core.solve.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.markup.OverRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.solve.markup.TimesRenderer;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * Represents a group of {@link VerticalModifierBlock}s, attached
 * to some base expression.
 */
public class VerticalModifierGroup extends ModifierGroup {

	// keep track of which children are TimeBlocks and which are OverBlocks
	protected List<ModifierBlock> timesBlocks;
	protected List<ModifierBlock> overBlocks;
	
	public VerticalModifierGroup() {
		timesBlocks = new ArrayList<ModifierBlock>();
		overBlocks = new ArrayList<ModifierBlock>();
	}
	
	@Override
	public double evaluate(double base) {
		for (ModifierBlock times : timesBlocks) {
			base *= times.value;
		}
		for (ModifierBlock over : overBlocks) {
			base /= over.value;
		}
		if (modifiers != null) base = modifiers.evaluate(base);
		return base;
	}

	@Override
	protected void updateChildren(float base, float dt) {
		// position TimesBlocks
		float y = parentRect.y - modSize();
		for (ModifierBlock block : timesBlocks) {
			block.interpolateRect(rect.x, y, rect.width, parentRect.maxY() - y, base, dt);
			y -= modSize();
		}

		// position OverBlocks
		y = parentRect.maxY();
		for (ModifierBlock block : overBlocks) {
			block.interpolateRect(rect.x, y, rect.width, modSize(), base, dt);
			y += block.height();
		}
	}

	@Override
	protected void updateRect() {
		// the group's y extends up for every TimesBlock
		rect.y = parentRect.y - timesBlocks.size() * modSize();
		
		rect.x = parentRect.x;
		rect.width = parentRect.width;
		if (timesBlocks.size() > 0) {
			// if we have times blocks, we have to extends
			// out x and width for the wrapping arm of the times
			rect.x -= wrapSize();
			rect.width += 2 * wrapSize();
		}
		
		// the group's height extends down for every OverBlock
		rect.height = parentRect.height + children.size() * modSize();
	}
	
	@Override
	protected ModifierBlock removeChild(ModifierBlock child) {
		ModifierBlock mod = super.removeChild(child);
		// Be careful to remove the actual item removed by super, not just one that .equal()s
		for (int i = 0; i < timesBlocks.size(); i++) {
			if (timesBlocks.get(i) == mod) {
				timesBlocks.remove(i); 
				break;
			}
		}
		for (int i = 0; i < overBlocks.size(); i++) {
			if (overBlocks.get(i) == mod) {
				overBlocks.remove(i); 
				break;
			}
		}
		return mod;
	}
	
	@Override
	protected void addChild(ModifierBlock child) {
		super.addChild(child);
		// also add the child to out internal lists
		if (child instanceof TimesBlock) {
			timesBlocks.add(child);
		} else {
			overBlocks.add(child);
		}
	}

	@Override
	protected ModifierGroup createModifiers() {
		// vertical groups are modified by horizontal groups
		return new HorizontalModifierGroup();
	}

	@Override
	protected boolean canAdd(ModifierBlock sprite) {
		return sprite instanceof VerticalModifierBlock;
	}
	
	private enum Tag {
		CancelTimes, CancelOver, Times, Over;
	}
	
	@Override
	public void addSimplifiableBlocks(Aggregator ag) {
		for (int i = 0; i < timesBlocks.size(); i++) {
			ModifierBlock sprite = timesBlocks.get(i);
			for (ModifierBlock div : overBlocks) {
				if (div.equals(sprite.inverse())) {
					// if we have a Times- and OverBlock that cancel out, allow
					// the player to simplify them
					ag.add(sprite, div, Tag.CancelTimes);
				} else if (areDivisible(sprite.value, div.value)) {
					// if we have a Time- and OverBLock which can be reduced to
					// eliminate one, allow the player to simplify
					ag.add(sprite, div, Tag.CancelOver);
					continue;
				}
			}
			
			if (i > 0) {
				// allow the player to combine TimesBlocks
				ag.add(sprite, timesBlocks.get(i - 1), Tag.Times);
				continue;
			}
		}
		
		for (int i = 0; i < overBlocks.size(); i++) {
			// allow the player to combine OverBlocks
			ModifierBlock sprite = overBlocks.get(i);
			if (i > 0) {
				ag.add(sprite, overBlocks.get(i - 1), Tag.Over);
				continue;
			}
		}
	}
	
	@Override
	public void placeButton(Block sprite, ModifierBlock pair, Object tag, ButtonFactory factory) {
		if (tag == Tag.CancelTimes) {
			factory.getSimplifyButton(sprite, pair)
			.setTranslation(sprite.x() + wrapSize(), parentRect.maxY());
		} else if (tag == Tag.CancelOver) {
			factory.getSimplifyButton(sprite, pair, -1)
			.setTranslation(sprite.x() + wrapSize(), parentRect.maxY());
		} else if (tag == Tag.Times) {
			factory.getSimplifyButton(sprite, pair)
			.setTranslation(sprite.centerX(), sprite.y() + modSize());
		} else {
			factory.getSimplifyButton(sprite, pair)
			.setTranslation(sprite.centerX(), sprite.y());
		}
	}
	
	private boolean areDivisible(int a, int b) {
		if (a != 0 && b % a == 0) return true;
		if (b != 0 && a % b == 0) return true;
		return false;
	}

	@Override
	public void simplify(Block base, ModifierBlock pair) {
		if (base.inverse().equals(pair)) {
			// if the two cancel out, just remove them
			blockListener.wasSimplified(base, pair, this, 0, true);
			removeChild((ModifierBlock) base, true);
			removeChild(pair, true);
		} else {
			// otherwise, we either we're either combining or reducing
			boolean spriteTimes = base instanceof TimesBlock;
			boolean pairTimes = pair instanceof TimesBlock;
			if (spriteTimes == pairTimes) {
				// combine two Times- or OverBlocks
				reduceSame((ModifierBlock) base, pair, spriteTimes);
			} else {
				// reduce a Times- and OverBlock to just one
				reduceDif((ModifierBlock) base, pair, spriteTimes);
			}
		}
	}

	// reduce a Times- and OverBlock to just one
	private void reduceDif(final ModifierBlock a, final ModifierBlock b, final boolean aTimes) {
		if (Math.abs(a.value) < Math.abs(b.value)) {
			// we want a to be greater than b
			reduceDif(b, a, !aTimes);
			return;
		}
		
		// create the Renderer for the problem
		Renderer lhs = new BaseRenderer("x"), rhs;
		if (aTimes) {
			lhs = new OverRenderer(new TimesRenderer(lhs, new int[] { a.value }), new int[] { b.value });
			rhs = new TimesRenderer(new BaseRenderer("x"), new BlankRenderer());
		} else {
			lhs = new OverRenderer(new TimesRenderer(lhs, new int[] { b.value }), new int[] { a.value });
			rhs = new OverRenderer(new BaseRenderer("x"), new BlankRenderer());
		}
		
		Renderer problem = new JoinRenderer(lhs, rhs, "=");
		final int answer = a.value / b.value;
		
		SimplifyListener listener = new SimplifyListener() {
			@Override
			public void wasSimplified(int fails, boolean success) {
				ModifierBlock sprite = aTimes ? a : b;
				ModifierBlock pair = aTimes ? b : a;
				blockListener.wasSimplified(sprite, pair, VerticalModifierGroup.this, fails, success);
				if (success) {
					// set A's new smaller value and remove B 
					a.setValue(answer);
					removeChild(b, true);
				}
			}
		};
		
		if (a.value == 1 || b.value == 1) {
			// just automatically simplify if one of the terms is 1
			listener.wasSimplified(0, true);
		} else {
			// otherwise show the NumberSelectScreen for the problem
			blockListener.wasReduced(problem, answer, 0, Stat.Over, 
					Difficulty.rankOver(a.value, b.value), listener);
		}
		
	}

	// combine two Times- or OverBlocks
	protected void reduceSame(final ModifierBlock a, final ModifierBlock b, boolean times) {
		if (blockListener != null) { // not sure why I need this check.. but I'm scared to remove it
			
			// create the renderer
			Renderer lhs = new BaseRenderer("x"), rhs = new BaseRenderer("x");
			int[] operands = new int[] { b.value, a.value };
			if (times) {
				lhs = new TimesRenderer(lhs, operands);
				rhs = new TimesRenderer(rhs, new BlankRenderer());
			} else {
				lhs = new OverRenderer(lhs, operands);
				rhs = new OverRenderer(rhs, new BlankRenderer());
			}
			Renderer problem = new JoinRenderer(lhs, rhs, "=");
			
			final int answer = a.value * b.value;
			
			SimplifyListener listener = new SimplifyListener() {
				@Override
				public void wasSimplified(int fails, boolean success) {
					blockListener.wasSimplified(a, b, VerticalModifierGroup.this, fails, success);
					if (success) {
						// set B's value and remove A 
						b.setValue(answer);
						removeChild(a, true);
					}
				}
			};
			
			if (a.value == 1 || b.value == 1) {
				// automatically combine if one is 1
				listener.wasSimplified(0, true);
			} else {
				// show the NumberSelectScreen
				blockListener.wasReduced(problem, answer, b.value, Stat.Times, 
						Difficulty.rankTimes(a.value, b.value), listener);
			}
		}
	}

	@Override
	public void simplifyModifiers() {
		super.simplifyModifiers();
		int times = 1, over = 1;
		// combine all modifiers into one Times and one Over
		while (!timesBlocks.isEmpty()) {
			ModifierBlock child = timesBlocks.get(0);
			removeChild(child);
			times *= child.value;
		}
		while (!overBlocks.isEmpty()) {
			ModifierBlock child = overBlocks.get(0);
			removeChild(child);
			over *= child.value;
		}
		int gcd = gcd(times, over);
		times /= gcd;
		over /= gcd;
		
		if (times != 1) {
			addChild(new TimesBlock(times));
		}
		if (over != 1) {
			addChild(new OverBlock(over));
		}
	}
	
	// greatest common denominator
	private int gcd(int a, int b) {
		while (b != 0) {
			int t = b;
			b = a % t;
			a = t;
		}
		return a;
	}
	
	@Override
	public void addNegative() {
		if (modifiers != null) {
			// propagate upward if possible
			modifiers.addNegative();
		} else {
			for (int i = 0; i < timesBlocks.size(); i++) {
				ModifierBlock mod = timesBlocks.get(i);
				if (mod.value == -1) {
					// if possible, remove a negative
					removeChild(mod, true);
					return;
				}
			}
			// otherwise add one
			TimesBlock neg = new TimesBlock(-1);
			addChild(neg);
		}
	}

	@Override
	protected Renderer createRenderer(Renderer base) {
		if (children.size() > 0) {
			if (timesBlocks.size() > 0) {
				// add all the times modifiers
				int[] operands = new int[timesBlocks.size()];
				boolean[] highlights = new boolean[operands.length];
				for (int i = 0; i < operands.length; i++) {
					//invert the times block order (top->down instead of inside->out)
					ModifierBlock times = timesBlocks.get(operands.length - 1 - i);
					operands[i] = times.value;
					highlights[i] = times.previewAdd();
				}
				base = new TimesRenderer(base, operands, highlights);
			}
			if (overBlocks.size() > 0) {
				// add all the over modifiers
				int[] operands = new int[overBlocks.size()];
				boolean[] highlights = new boolean[operands.length];
				for (int i = 0; i < operands.length; i++) {
					operands[i] = overBlocks.get(i).value;
					highlights[i] = overBlocks.get(i).previewAdd();
				}
				base = new OverRenderer(base, operands, highlights);
			}
		}
		if (modifiers == null) {
			return base;
		} else {
			// recurse upwards, if possible
			return modifiers.createRenderer(base);
		}
	}

	@Override
	protected Sprite copyChild() {
		return new VerticalModifierGroup();
	}
	
	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		super.persist(data);
		if (data.readMode()) {
			// we don't persist the times/overBlocks list
			// so we recreate them upon reconstruction
			timesBlocks.clear();
			overBlocks.clear();
			for (ModifierBlock mod : children) {
				if (mod instanceof TimesBlock) {
					timesBlocks.add(mod);
				} else {
					overBlocks.add(mod);
				}
			}
		}
	}
	
	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new VerticalModifierGroup();
			}
		};
	}
}
