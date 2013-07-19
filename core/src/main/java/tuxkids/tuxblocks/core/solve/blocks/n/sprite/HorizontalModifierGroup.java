package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

public class HorizontalModifierGroup extends ModifierGroup {

	
	public HorizontalModifierGroup(Sprite parent) {
		super(parent);
	}

	@Override
	protected void updateChildren(float base, float dt) {
		float x = parentRect.maxX();
		for (ModifierBlockSprite child : children) {
			child.interpolateRect(x, parentRect.y, modSize(), parentRect.height, base, dt);
			//x = child.right();
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
		return new VerticalModifierGroup(this);
	}

	@Override
	protected boolean canAdd(ModifierBlockSprite sprite) {
		return sprite instanceof HorizontalModifierSprite;
	}

	@Override
	public boolean addExpression(NumberBlockSprite sprite, boolean snap) {	
		List<VerticalModifierSprite> sharedMods = getSharedModifiersForAdd(sprite);
		if (sharedMods == null) {
			return super.addExpression(sprite, snap);
		}
		
		List<ModifierBlockSprite> outsideModifiers = null;
		if (sprite.modifiers.modifiers != null &&
				sprite.modifiers.modifiers.modifiers != null) {
			outsideModifiers = sprite.modifiers.modifiers.modifiers.children;
		}
		
		if (modifiers == null || sharedMods.size() == modifiers.children.size()) {
			addChild(sprite.proxyFor());
			for (ModifierBlockSprite mod : sprite.modifiers.children) addChild(mod);
		} else {
			if (super.addExpression(sprite, snap)) {
				return true;
			}
			
			ModifierGroup modMods = modifiers.removeModifiers();
			modifiers.addNewModifiers();
			modifiers.modifiers.addChild(sprite.proxyFor());
			for (ModifierBlockSprite mod : sprite.modifiers.children) {
				modifiers.modifiers.addChild(mod);
			}
			modifiers.modifiers.addNewModifiers();
			for (VerticalModifierSprite sharedMod : sharedMods) {		
				ModifierBlockSprite m = modifiers.removeChild(sharedMod);
				modifiers.modifiers.modifiers.addChild(m);
			}
			modifiers.modifiers.modifiers.setModifiers(modMods);
		}
		
		if (outsideModifiers != null) {
			for (ModifierBlockSprite mod : outsideModifiers) {
				addModifier(mod, false);
			}
		}
		
		if (snap) snapChildren(); //TODO: maybe better implementation, if I ever use this option
		sprite.layer().destroy();
		
		return true;
	}

	public boolean canAddExpression(NumberBlockSprite sprite) {
		if (getSharedModifiersForAdd(sprite) != null) {
			return true;
		}
		return super.canAddExpression(sprite);
	}
	
	private List<VerticalModifierSprite> getSharedModifiersForAdd(NumberBlockSprite sprite) {
		if (modifiers != null || sprite.modifiers.modifiers == null) {
			
			if (sprite.modifiers.modifiers != null &&
					sprite.modifiers.modifiers.modifiers != null &&
					sprite.modifiers.modifiers.modifiers.modifiers != null) {
				return null; //you can have n(x + n) + n, but not  n(n(x + n) + n)
			}
			
			List<VerticalModifierSprite> myMods = new ArrayList<VerticalModifierSprite>(),
					myIndirectMods = new ArrayList<VerticalModifierSprite>(),
					spriteMods = new ArrayList<VerticalModifierSprite>();
	
			addVerticalModifiers(myMods);
			if (modifiers != null) modifiers.addVerticalModifiers(myIndirectMods);
			sprite.modifiers.addVerticalModifiers(spriteMods);
			
			for (VerticalModifierSprite mod : myIndirectMods) {
				int index = myMods.lastIndexOf(mod); // the last one will be the outermost
				myMods.remove(index);
				if (!spriteMods.remove(mod)) {
					// can't add because there's a factor on this expression that can't be
					// rearranged that the given sprite doesn't share
					return null;
				}
			}
			
			List<VerticalModifierSprite> sharedMods = myIndirectMods; // reuse a list
			sharedMods.clear();
			
			for (VerticalModifierSprite mod : spriteMods) {
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
	protected void updateSimplify() {
		for (int i = 1; i < children.size(); i++) {
			ModifierBlockSprite sprite = children.get(i);
			ModifierBlockSprite before = children.get(i - 1);
			if (sprite.inverse().equals(before)) {
				getSimplifyButton(sprite).setTranslation(sprite.x(), sprite.centerY());
			}
		}
	}

	@Override
	protected void simplify(ModifierBlockSprite sprite) {
		for (int i = 1; i < children.size(); i++) {
			if (sprite != children.get(i)) continue;
			ModifierBlockSprite before = children.get(i - 1);
			removeChild(sprite, true);
			removeChild(before, true);
		}
	}
	
	@Override
	public void addNegative() {
		if (modifiers != null) {
			modifiers.addNegative();
		} else {
			addNewModifiers();
			modifiers.addNegative();
		}
	}
}
