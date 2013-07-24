package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.solve.blocks.n.markup.OverGroupRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.TimesGroupRenderer;

public class VerticalModifierGroup extends ModifierGroup {

	protected List<ModifierBlockSprite> timesBlocks;
	protected List<ModifierBlockSprite> divBlocks;
	
	public VerticalModifierGroup() {
		timesBlocks = new ArrayList<ModifierBlockSprite>();
		divBlocks = new ArrayList<ModifierBlockSprite>();
	}

	@Override
	protected void updateChildren(float base, float dt) {
		float y = parentRect.y - modSize();
		for (ModifierBlockSprite block : timesBlocks) {
			block.interpolateRect(rect.x, y, rect.width, parentRect.maxY() - y, base, dt);
			y -= modSize();
		}

//		y = parentRect.maxY() + modSize(); // for times-like wrap shape
		y = parentRect.maxY();
		for (ModifierBlockSprite block : divBlocks) {
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
	protected ModifierBlockSprite removeChild(ModifierBlockSprite child) {
		ModifierBlockSprite mod = super.removeChild(child);
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
	protected void addChild(ModifierBlockSprite child) {
		super.addChild(child);
		if (child instanceof TimesBlockSprite) {
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
	protected boolean canAdd(ModifierBlockSprite sprite) {
		return sprite instanceof VerticalModifierSprite;
	}

	@Override
	protected void updateSimplify() {
		for (int i = 0; i < timesBlocks.size(); i++) {
			ModifierBlockSprite sprite = timesBlocks.get(i);
			if (divBlocks.contains(sprite.inverse())) {
				//getSimplifyButton(sprite).setTranslation(sprite.x() + wrapSize() / 2, sprite.bottom());
				getSimplifyButton(sprite).setTranslation(sprite.x() + wrapSize(), parentRect.maxY());
			}
			// cancel *-1's
			if (i > 0) {
				if (timesBlocks.get(i - 1).equals(sprite.inverse())) {
					getSimplifyButton(sprite).setTranslation(sprite.centerX(), sprite.y() + modSize());
				}
			}
		}
	}

	@Override
	protected void cancelOut(ModifierBlockSprite sprite) {
		for (int i = 0; i < timesBlocks.size(); i++) {
			if (timesBlocks.get(i) != sprite) continue;
			int index = divBlocks.lastIndexOf(sprite.inverse());
			ModifierBlockSprite pair;
			if (index < 0) {
				// doing some funky stuff here so that *-1 cancels out *-1
				index = timesBlocks.indexOf(sprite.inverse());
				if (index == i) index = timesBlocks.lastIndexOf(sprite.inverse());
				if (index == i || index < 0) return;
				pair = timesBlocks.get(index);
			} else {
				pair = divBlocks.get(index);
			}
			removeChild(sprite, true);
			removeChild(pair, true);
		}
	}

	@Override
	public void addNegative() {
		if (modifiers != null) {
			modifiers.addNegative();
		} else {
			for (int i = 0; i < timesBlocks.size(); i++) {
				ModifierBlockSprite mod = timesBlocks.get(i);
				if (mod.value == -1) {
					removeChild(mod, true);
					return;
				}
			}
			TimesBlockSprite neg = new TimesBlockSprite(-1);
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
					operands[i] = timesBlocks.get(i).value;
					highlights[i] = timesBlocks.get(i).previewAdd();
				}
				base = new TimesGroupRenderer(base, operands, highlights);
			}
			if (divBlocks.size() > 0) {
				int[] operands = new int[divBlocks.size()];
				boolean[] highlights = new boolean[operands.length];
				for (int i = 0; i < operands.length; i++) {
					operands[i] = divBlocks.get(i).value;
					highlights[i] = divBlocks.get(i).previewAdd();
				}
				base = new OverGroupRenderer(base, operands, highlights);
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
}
