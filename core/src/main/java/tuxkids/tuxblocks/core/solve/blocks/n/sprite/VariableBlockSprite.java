package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;

import tuxkids.tuxblocks.core.solve.blocks.n.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.BlankRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.TimesRenderer;
import tuxkids.tuxblocks.core.utils.HashCode;

public class VariableBlockSprite extends BaseBlockSprite {

	protected String symbol;
	private final ArrayList<VerticalModifierSprite> verticalMods = new ArrayList<VerticalModifierSprite>();
	
	public VariableBlockSprite(String symbol) {
		this.symbol = symbol;
	}
	
	@Override
	protected String text() {
		return symbol;
	}

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(symbol);
	}
	
	@Override
	public boolean canRelease(boolean multiExpression) {
		return true;
	}

	@Override
	public BlockSprite inverse() {
		return this;
	}

	@Override
	protected Sprite copyChild() {
		return new VariableBlockSprite(symbol);
	}
	
	@Override
	public boolean canAccept(BlockSprite sprite) {
		if (super.canAccept(sprite)) {
			return true;
		}
		
		if (sprite instanceof VariableBlockSprite) {
			VariableBlockSprite vBlock = (VariableBlockSprite) sprite;
			if (vBlock.modifiers.isModifiedHorizontally() || vBlock.modifiers.children.size() != 0) {
				// can't add a variable plus its addends (no x + 3)
				return false;
			}

			verticalMods.clear();
			modifiers.addVerticalModifiersTo(verticalMods);
			if (verticalMods.size() <= 1) {
				if (verticalMods.size() == 1) {
					// this variable can't divided (no x / 4)
					if (!(verticalMods.get(0) instanceof TimesBlockSprite)) return false;
					// nor can it have any addends inside of its factor (no 3(x + 1))
					if (modifiers.children.size() != 0) return false;
				}
				verticalMods.clear();
				vBlock.modifiers.addVerticalModifiersTo(verticalMods);
				if (verticalMods.size() <= 1) {
					// can't add a variable that has divisors (no x / 4)
					if (verticalMods.size() == 1 && !(verticalMods.get(0) instanceof TimesBlockSprite)) return false;
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public ModifierBlockSprite addBlock(final BlockSprite sprite, boolean snap) {
		if (sprite instanceof VariableBlockSprite) {
			
			VariableBlockSprite vBlock = (VariableBlockSprite) sprite;
			TimesBlockSprite spriteFactor = null;
			final TimesBlockSprite myFactor;

			verticalMods.clear();
			modifiers.addVerticalModifiersTo(verticalMods);
			if (verticalMods.size() > 0) {
				myFactor = (TimesBlockSprite) verticalMods.get(0);
			} else {
				myFactor = null;
			}

			verticalMods.clear();
			vBlock.modifiers.addVerticalModifiersTo(verticalMods);
			if (verticalMods.size() > 0) spriteFactor = (TimesBlockSprite) verticalMods.get(0);
			
			int myValue = myFactor == null ? 1 : myFactor.value;
			int spriteValue = spriteFactor == null ? 1 : spriteFactor.value;
			
			Renderer myRenderer = new BaseRenderer(text());
			Renderer spriteRenderer = new BaseRenderer(vBlock.text());
			
			if (myFactor != null) myRenderer = new TimesRenderer(myRenderer, new int[] { myFactor.value });
			if (spriteFactor != null) spriteRenderer = new TimesRenderer(spriteRenderer, new int[] { spriteFactor.value });
			
			Renderer lhs = new JoinRenderer(myRenderer, spriteRenderer, "+");
			Renderer rhs = new TimesRenderer(new BaseRenderer(text()), new BlankRenderer());
			Renderer problem = new JoinRenderer(lhs, rhs, "=");
			
			//Don't say what the answer is if this is a preview
			final int answer = hasSprite() ? myValue + spriteValue : TimesRenderer.UNKNOWN_NUMBER;
			
			SimplifyListener r = new SimplifyListener() {
				@Override
				public void wasSimplified(boolean success) {
					if (success) {
						if (myFactor != null) {
							myFactor.setValue(answer);
							myFactor.setPreviewAdd(false); //creates highlight.. for a good reason I won't explain
						} else {
							ArrayList<ModifierBlockSprite> modChildren = new ArrayList<ModifierBlockSprite>();
							while (!modifiers.children.isEmpty()) {
								modChildren.add(modifiers.removeChild(modifiers.children.get(0)));
							}
							addModifier(new TimesBlockSprite(answer), true);
							while (!modChildren.isEmpty()) {
								addBlock(modChildren.remove(0), false);
							}
						}
						if (sprite.hasSprite()) sprite.layer().destroy();
						if (blockListener != null) {
							blockListener.wasSimplified();
						}
					} else {
						blockListener.wasCanceled();
					}
				}
			};
			 
			//if it's not just a +1, make them solve it
			if (myFactor != null || spriteFactor != null) {
				if (!hasSprite()) {
					r.wasSimplified(true); //show preview
				} else {
					blockListener.wasReduced(problem, answer, myValue, r);
				}
			} else {
				r.wasSimplified(true);
			}
			
			
			return null;
		} else {
			return super.addBlock(sprite, snap);
		}
	}
}
