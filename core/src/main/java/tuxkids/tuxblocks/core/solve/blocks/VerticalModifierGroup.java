package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.BlankRenderer;
import tuxkids.tuxblocks.core.solve.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.markup.OverRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.solve.markup.TimesRenderer;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.Persistable;
import tuxkids.tuxblocks.core.utils.Persistable.Constructor;
import tuxkids.tuxblocks.core.utils.Persistable.Data;
import tuxkids.tuxblocks.core.utils.Persistable.ParseDataException;

public class VerticalModifierGroup extends ModifierGroup {

	protected List<ModifierBlock> timesBlocks;
	protected List<ModifierBlock> divBlocks;
	
	public VerticalModifierGroup() {
		timesBlocks = new ArrayList<ModifierBlock>();
		divBlocks = new ArrayList<ModifierBlock>();
	}

	@Override
	protected void updateChildren(float base, float dt) {
		float y = parentRect.y - modSize();
		for (ModifierBlock block : timesBlocks) {
			block.interpolateRect(rect.x, y, rect.width, parentRect.maxY() - y, base, dt);
			y -= modSize();
		}

//		y = parentRect.maxY() + modSize(); // for times-like wrap shape
		y = parentRect.maxY();
		for (ModifierBlock block : divBlocks) {
//			block.interpolateRect(rect.x, parentRect.centerY(), rect.width, y - parentRect.centerY(), base, dt);
			block.interpolateRect(rect.x, y, rect.width, modSize(), base, dt);
			y += block.height();
		}
	}

	@Override
	protected void updateRect() {
		rect.y = parentRect.y - timesBlocks.size() * modSize();
		
		rect.x = parentRect.x;
		rect.width = parentRect.width;
		if (timesBlocks.size() > 0) {
			rect.x -= wrapSize();
			rect.width += 2 * wrapSize();
		}
		rect.height = parentRect.height + children.size() * modSize();
	}
	
	@Override
	protected ModifierBlock removeChild(ModifierBlock child) {
		ModifierBlock mod = super.removeChild(child);
		// Be careful to remove the actual item removed by super, not just one that .equal()
		for (int i = 0; i < timesBlocks.size(); i++) {
			if (timesBlocks.get(i) == mod) {
				timesBlocks.remove(i); 
				break;
			}
		}
		for (int i = 0; i < divBlocks.size(); i++) {
			if (divBlocks.get(i) == mod) {
				divBlocks.remove(i); 
				break;
			}
		}
		return mod;
	}
	
	@Override
	protected void addChild(ModifierBlock child) {
		super.addChild(child);
		if (child instanceof TimesBlock) {
			timesBlocks.add(child);
		} else {
			divBlocks.add(child);
		}
	}

	@Override
	protected ModifierGroup createModifiers() {
		return new HorizontalModifierGroup();
	}

	@Override
	protected boolean canAdd(ModifierBlock sprite) {
		return sprite instanceof VerticalModifierBlock;
	}

	@Override
	public void updateSimplify() {
		for (int i = 0; i < timesBlocks.size(); i++) {
			ModifierBlock sprite = timesBlocks.get(i);
			for (ModifierBlock div : divBlocks) {
				if (div.equals(sprite.inverse())) {
					simplifyLayer.getSimplifyButton(sprite, div).setTranslation(sprite.x() + wrapSize(), parentRect.maxY());
					continue;
				} else if (areDivisible(sprite.value, div.value)) {
					simplifyLayer.getSimplifyButton(sprite, div, -1).setTranslation(sprite.x() + wrapSize(), parentRect.maxY());
					continue;
				}
			}
			// reduce/cancel out -1s
			if (i > 0) {
//				if ((sprite.value == -1) != (timesBlocks.get(i - 1).value == -1)) continue; //-1 can only simplify with another -1
				simplifyLayer.getSimplifyButton(sprite, timesBlocks.get(i - 1)).setTranslation(sprite.centerX(), sprite.y() + modSize());
				continue;
			}
		}
		for (int i = 0; i < divBlocks.size(); i++) {
			ModifierBlock sprite = divBlocks.get(i);
			if (i > 0) {
				simplifyLayer.getSimplifyButton(sprite, divBlocks.get(i - 1)).setTranslation(sprite.centerX(), sprite.y());
				continue;
			}
		}
	}
	
	private boolean areDivisible(int a, int b) {
		if (a != 0 && b % a == 0) return true;
		if (b != 0 && a % b == 0) return true;
		return false;
	}

	@Override
	public void simplify(ModifierBlock sprite, ModifierBlock pair) {
		if (sprite.inverse().equals(pair)) {
			removeChild(sprite, true);
			removeChild(pair, true);
			blockListener.wasSimplified();
		} else {
			boolean spriteTimes = sprite instanceof TimesBlock;
			boolean pairTimes = pair instanceof TimesBlock;
			if (spriteTimes == pairTimes) {
				reduceSame(sprite, pair, spriteTimes);
			} else {
				reduceDif(sprite, pair, spriteTimes);
			}
		}
//		for (int i = 0; i < timesBlocks.size(); i++) {
//			if (timesBlocks.get(i) != sprite) continue;
//			
//			int index = divBlocks.lastIndexOf(sprite.inverse());
//			ModifierBlockSprite pair;
//			if (index < 0) {
//				if (i > 0) {
//					pair = timesBlocks.get(i - 1);
//					//-1's simplify
//					if (sprite.value != -1 || pair.value != -1) {
//						//others reduce
//						reduce(sprite, pair, true); 
//						return;
//					}
//				} else {
//					return;
//				}
//			} else {
//				pair = divBlocks.get(index);
//			}
//			return;
//		}
//		for (int i = 0; i < divBlocks.size(); i++) {
//			if (divBlocks.get(i) != sprite) continue;
//			if (i > 0) {
//				ModifierBlockSprite pair = divBlocks.get(i - 1);
//				if (!pair.equals(sprite.inverse())) {
//					reduce(sprite, pair, false);
//					return;
//				}
//			} else {
//				return;
//			}
//		}
	}
	
	private void reduceDif(final ModifierBlock a, final ModifierBlock b, boolean aTimes) {
		if (a.value < b.value) {
			reduceDif(b, a, !aTimes);
			return;
		}
		
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
			public void wasSimplified(boolean success) {
				if (success) {
					a.setValue(answer);
					removeChild(b, true);
					blockListener.wasSimplified();
				}
			}
		};
		
		if (a.value == 1 || b.value == 1) {
			listener.wasSimplified(true);
		} else {
			blockListener.wasReduced(problem, answer, a.value, Stat.Over, 
					Difficulty.rankOver(a.value, b.value), listener);
		}
		
	}

	protected void reduceSame(final ModifierBlock a, final ModifierBlock b, boolean times) {
		if (blockListener != null) {
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
				public void wasSimplified(boolean success) {
					if (success) {
						b.setValue(answer);
						removeChild(a, true);
						blockListener.wasSimplified();
					}
				}
			};
			
			if (a.value == 1 || b.value == 1) {
				listener.wasSimplified(true);
			} else {
				blockListener.wasReduced(problem, answer, b.value, Stat.Times, 
						Difficulty.rankTimes(a.value, b.value), listener);
			}
		}
	}

	@Override
	public void simplifyModifiers() {
		super.simplifyModifiers();
		int times = 1, over = 1;
		while (!timesBlocks.isEmpty()) {
			ModifierBlock child = timesBlocks.get(0);
			removeChild(child);
			times *= child.value;
		}
		while (!divBlocks.isEmpty()) {
			ModifierBlock child = divBlocks.get(0);
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
			modifiers.addNegative();
		} else {
			for (int i = 0; i < timesBlocks.size(); i++) {
				ModifierBlock mod = timesBlocks.get(i);
				if (mod.value == -1) {
					removeChild(mod, true);
					return;
				}
			}
			TimesBlock neg = new TimesBlock(-1);
			addChild(neg);
		}
	}

	@Override
	protected Renderer createRenderer(Renderer base) {
		if (children.size() > 0) {
			if (timesBlocks.size() > 0) {
				int[] operands = new int[timesBlocks.size()];
				boolean[] highlights = new boolean[operands.length];
				for (int i = 0; i < operands.length; i++) {
					//invert the times block order
					ModifierBlock times = timesBlocks.get(operands.length - 1 - i);
					operands[i] = times.value;
					highlights[i] = times.previewAdd();
				}
				base = new TimesRenderer(base, operands, highlights);
			}
			if (divBlocks.size() > 0) {
				int[] operands = new int[divBlocks.size()];
				boolean[] highlights = new boolean[operands.length];
				for (int i = 0; i < operands.length; i++) {
					operands[i] = divBlocks.get(i).value;
					highlights[i] = divBlocks.get(i).previewAdd();
				}
				base = new OverRenderer(base, operands, highlights);
			}
		}
		if (modifiers == null) {
			return base;
		} else {
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
		if (!data.writeMode()) {
			timesBlocks.clear();
			divBlocks.clear();
			for (ModifierBlock mod : children) {
				if (mod instanceof TimesBlock) {
					timesBlocks.add(mod);
				} else {
					divBlocks.add(mod);
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
