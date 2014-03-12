package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.Aggregator;
import tuxkids.tuxblocks.core.solve.blocks.layer.SimplifyLayer.ButtonFactory;
import tuxkids.tuxblocks.core.solve.markup.AddRenderer;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.BlankRenderer;
import tuxkids.tuxblocks.core.solve.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * A {@link ModifierGroup} consisting of {@link HorizontalModifierBlock}s.
 * Handles special horizontal interactions such as adding a {@link NumberBlock} with
 * modifiers to another {@link BaseBlock}. 
 */
public class HorizontalModifierGroup extends ModifierGroup {

	@Override
	protected void updateChildren(float base, float dt) {
		float x = parentRect.maxX();
		for (ModifierBlock child : children) {
			child.interpolateRect(x, parentRect.y, modSize(), parentRect.height, base, dt);
			x += modSize();
		}
	}
	
	@Override
	protected void updateRect() {
		rect.y = parentRect.y;
		rect.x = parentRect.x;
		rect.width = parentRect.width + children.size() * modSize();
		rect.height = parentRect.height;
	}

	@Override
	protected ModifierGroup createModifiers() {
		return new VerticalModifierGroup();
	}

	@Override
	protected boolean canAdd(ModifierBlock sprite) {
		return sprite instanceof HorizontalModifierBlock;
	}

	@Override
	public ModifierBlock addExpression(NumberBlock sprite, boolean snap) {	
		List<VerticalModifierBlock> sharedMods = getSharedModifiersForAdd(sprite);
		if (sharedMods == null) {
			// if the NumberBlock isn't modified, we can
			// just add it as a regular modifier block
			return super.addExpression(sprite, snap);
		}
		
		List<ModifierBlock> outsideModifiers = null;
		if (sprite.modifiers.modifiers != null &&
				sprite.modifiers.modifiers.modifiers != null) {
			// extra horizontal modifier that can be popped off and re-added
			// at the end, once the two block have been merged
			outsideModifiers = sprite.modifiers.modifiers.modifiers.children;
		}
		
		ModifierBlock proxy;
		// if we have no modifiers, or all the same vertical modifiers
		if (modifiers == null || sharedMods.size() == modifiers.children.size()) {
			// just add the the block and all its horizontal children
			addChild(proxy = sprite.proxyFor());
			for (ModifierBlock mod : sprite.modifiers.children) addChild(mod);
		} else {
			// try adding propagating upwards
			ModifierBlock superMod = super.addExpression(sprite, snap);
			if (superMod != null) {
				// return if it's successful
				return superMod;
			}
			
			// replace our modifier's horizontal modifiers with a new one...
			ModifierGroup modMods = modifiers.removeModifiers();
			modifiers.addNewModifiers();
			// and add the block to it...
			modifiers.modifiers.addChild(proxy = sprite.proxyFor());
			for (ModifierBlock mod : sprite.modifiers.children) {
				// and all it's horizontal children
				modifiers.modifiers.addChild(mod);
			}
			
			//now move all the shared modifiers to modify that new horizontal group
			modifiers.modifiers.addNewModifiers();
			for (VerticalModifierBlock sharedMod : sharedMods) {		
				ModifierBlock m = modifiers.removeChild(sharedMod);
				modifiers.modifiers.modifiers.addChild(m);
			}
			// and tack our old modifier's modifiers onto the end
			modifiers.modifiers.modifiers.setModifiers(modMods);
			
			// PS if you don't understand any of this, just smile and nod and be glad it works
			// PPS if you think there's one too many/few "modifier.", that's
			// perfectly natural, but I think you're probably wrong
		}
		
		if (outsideModifiers != null) {
			for (ModifierBlock mod : outsideModifiers) {
				addModifier(mod, false);
			}
		}
		
		if (snap) snapChildren(); //TODO: maybe better implementation, if I ever use this option
		if (sprite.hasSprite()) sprite.layer().destroy();
		
		return proxy;
	}

	@Override
	public boolean canAddExpression(NumberBlock sprite) {
		if (getSharedModifiersForAdd(sprite) != null) {
			return true;
		}
		return super.canAddExpression(sprite);
	}
	
	// Returns the vertical modifiers this group shares with a NumberBlock
	// such that they could theoretically by combined underneath them
	// for example 3(x + 2) and 3(4) can be combined into 3(x + 2 + 4).
	// Returns null if this block cannot be added (not the empty list)
	private List<VerticalModifierBlock> getSharedModifiersForAdd(NumberBlock sprite) {
		if (modifiers != null || sprite.modifiers.modifiers == null) {
			
			if (sprite.modifiers.modifiers != null &&
					sprite.modifiers.modifiers.modifiers != null &&
					sprite.modifiers.modifiers.modifiers.modifiers != null) {
				return null; //you can have n(x + n) + n, but not  n(n(x + n) + n)
			}
			
			List<VerticalModifierBlock> myMods = new ArrayList<VerticalModifierBlock>(),
					myIndirectMods = new ArrayList<VerticalModifierBlock>(),
					spriteMods = new ArrayList<VerticalModifierBlock>();
	
			addVerticalModifiersTo(myMods);
			if (modifiers != null) modifiers.addVerticalModifiersTo(myIndirectMods);
			sprite.modifiers.addVerticalModifiersTo(spriteMods);
			
			for (VerticalModifierBlock mod : myIndirectMods) {
				int index = myMods.lastIndexOf(mod); // the last one will be the outermost
				myMods.remove(index);
				if (!spriteMods.remove(mod)) {
					// can't add because there's a factor on this expression that can't be
					// rearranged that the given sprite doesn't share
					return null;
				}
			}
			
			List<VerticalModifierBlock> sharedMods = myIndirectMods; // reuse a list
			sharedMods.clear();
			
			for (VerticalModifierBlock mod : spriteMods) {
				int index = myMods.lastIndexOf(mod);
				if (index < 0) {
					// can't add because the given sprite has a factor that this expression doesn't share
					return null;
				} else {
					sharedMods.add(myMods.get(index)); // add the mod from /this/ sprite
					myMods.remove(index);
				}
			}
			return sharedMods; // return the mods shared by the two sprites
		}
		return null;
	}
	
	@Override
	public void addSimplifiableBlocks(Aggregator ag) {
		// all horizontal children can simplify (a + b - c can all combine)
		for (int i = 1; i < children.size(); i++) {
			ModifierBlock sprite = children.get(i);
			ag.add(sprite, children.get(i - 1), null);
		}
	}
	
	@Override
	public void placeButton(ModifierBlock sprite, ModifierBlock pair,
			Object tag, ButtonFactory factory) {
		factory.getSimplifyButton(sprite, pair)
		.setTranslation(sprite.x(), sprite.centerY());
	}

	@Override
	public void simplify(final ModifierBlock sprite, ModifierBlock pair) {
		HorizontalModifierBlock hSprite = (HorizontalModifierBlock) sprite;
		final HorizontalModifierBlock before = (HorizontalModifierBlock) pair;
		if (sprite.inverse().equals(before)) {
			// if the two are inverses (2 - 2) just simplify
			removeChild(sprite, true);
			removeChild(before, true);
			blockListener.wasSimplified();
		} else {
			// create a problem to combine the two blocks
			Renderer problem = new JoinRenderer(
					new JoinRenderer(new BaseRenderer("" + before.plusValue()), 
							new BaseRenderer("" + hSprite.value), hSprite instanceof PlusBlock ? "+" : "-"), 
					new BlankRenderer(), "=");
			final int answer = before.plusValue() + hSprite.plusValue();
			Stat stat = hSprite.plusValue() >= 0 ? Stat.Plus : Stat.Minus;
			int level = Difficulty.rankPlus(before.plusValue(), hSprite.plusValue());
			int start = before.plusValue();
			// show it to the player
			blockListener.wasReduced(problem, answer, start, stat, level, new SimplifyListener() {
				@Override
				public void wasSimplified(boolean success) {
					if (success) {
						// and if the succeed, remove the modifier block and add it to the other one
						before.setPlusValue(answer);
						removeChild(sprite, true);
						blockListener.wasSimplified();
					}
				}
			});
		}
	}
	
	@Override
	public void simplifyModifiers() {
		super.simplifyModifiers();
		// combine all children into one + or - block
		int total = 0;
		while (!children.isEmpty()) {
			HorizontalModifierBlock child = (HorizontalModifierBlock) children.get(0);
			total += child.plusValue();
		}
		if (total > 0) {
			addChild(new PlusBlock(total));
		} else if (total < 0) {
			addChild(new MinusBlock(-total));
		}
	}
	
	@Override
	public void addNegative() {
		// Essentially, propagate upward.
		// Forcibly.
		if (modifiers != null) {
			modifiers.addNegative();
		} else {
			addNewModifiers();
			modifiers.addNegative();
		}
	}

	@Override
	protected Renderer createRenderer(Renderer base) {
		if (children.size() != 0) {
			int[] operands = new int[children.size()];
			boolean[] highlights = new boolean[operands.length];
			// add all children to the renderer
			for (int i = 0; i < operands.length; i++) {
				operands[i] = ((HorizontalModifierBlock) children.get(i)).plusValue();
				highlights[i] = children.get(i).previewAdd();
			}
			base = new AddRenderer(base, operands, highlights);
		}
		if (modifiers == null) {
			return base;
		} else {
			return modifiers.createRenderer(base);
			
		}
	}

	@Override
	protected Sprite copyChild() {
		return new HorizontalModifierGroup();
	}
	
	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new HorizontalModifierGroup();
			}
		};
	}
}
