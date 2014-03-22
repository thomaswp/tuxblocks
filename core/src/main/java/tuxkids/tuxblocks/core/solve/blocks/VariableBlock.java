package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.solve.markup.BaseRenderer;
import tuxkids.tuxblocks.core.solve.markup.BlankRenderer;
import tuxkids.tuxblocks.core.solve.markup.JoinRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.solve.markup.TimesRenderer;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * Represents a variable in an equation.
 */
public class VariableBlock extends BaseBlock {

	// the symbol for this variable - so far is always "x"
	protected String symbol;
	// temp variable for storing vertical modifiers
	private final ArrayList<VerticalModifierBlock> verticalMods = new ArrayList<VerticalModifierBlock>();
	
	public VariableBlock(String symbol) {
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
	public Block inverse() {
		return this;
	}

	@Override
	protected Sprite copyChild() {
		return new VariableBlock(symbol);
	}
	
	@Override
	public boolean canAccept(Block sprite) {
		// variables can accept modifier blocks like any BaseBlock 
		if (super.canAccept(sprite)) {
			return true;
		}
		
		// they can also combine with other VariableBlocks
		if (sprite instanceof VariableBlock) {
			VariableBlock vBlock = (VariableBlock) sprite;
			if (vBlock.modifiers.isModifiedHorizontally() || vBlock.modifiers.children.size() != 0) {
				// can't add a variable plus its addends (no x + 3)
				return false;
			}

			//this block's vertical modifiers
			verticalMods.clear();
			//minus any pesky negatives
			modifiers.addVerticalModifiersTo(verticalMods);
			removeNegatives(verticalMods);
			if (verticalMods.size() <= 1) {
				if (verticalMods.size() == 1) {
					// this variable can't be divided (no x / 4)
					if (!(verticalMods.get(0) instanceof TimesBlock)) return false;
					// nor can it have any addends inside of its factor (no 3(x + 1))
					if (modifiers.children.size() != 0) return false;
				}
				
				verticalMods.clear();
				//the other block's vertical modifiers
				vBlock.modifiers.addVerticalModifiersTo(verticalMods);
				//also sans negatives
				removeNegatives(verticalMods);
				
				if (verticalMods.size() <= 1) {
					// can't add a variable that has divisors (no x / 4)
					if (verticalMods.size() == 1 && !(verticalMods.get(0) instanceof TimesBlock)) return false;
					return true;
				}
			}
		}
		return false;
	}
	
	// removes all "*-1" modifiers from the given list and returns the number removed
	private int removeNegatives(List<VerticalModifierBlock> modifiers) {
		int count = 0;
		for (int i = 0; i < modifiers.size(); i++) {
			ModifierBlock block = modifiers.get(i);
			if (block instanceof TimesBlock && block.value == -1) {
				modifiers.remove(i--);
				count++;
			}
		}
		return count;
	}
	
	@Override
	public ModifierBlock addBlock(final Block sprite, boolean snap) {
		if (sprite instanceof VariableBlock) {
			
			VariableBlock vBlock = (VariableBlock) sprite;
			TimesBlock spriteFactor = null;
			final TimesBlock myFactor;

			verticalMods.clear();
			// get this block's modifiers, sans negatives
			modifiers.addVerticalModifiersTo(verticalMods);
			int myNegatives = removeNegatives(verticalMods);
			// myFactor is the first modifier
			if (verticalMods.size() > 0) {
				myFactor = (TimesBlock) verticalMods.get(0);
			} else {
				myFactor = null;
			}

			// same with the block to add
			verticalMods.clear();
			vBlock.modifiers.addVerticalModifiersTo(verticalMods);
			int spriteNegatives = removeNegatives(verticalMods);
			if (verticalMods.size() > 0) spriteFactor = (TimesBlock) verticalMods.get(0);
			
			int myValue = myFactor == null ? 1 : myFactor.value;
			if (myNegatives == 1) myValue *= -1;
			
			int spriteValue = spriteFactor == null ? 1 : spriteFactor.value;
			if (spriteNegatives == 1) spriteValue *= -1;
			
			Renderer myRenderer = new BaseRenderer(text());
			Renderer spriteRenderer = new BaseRenderer(vBlock.text());
			
			if (myValue != 1) myRenderer = new TimesRenderer(myRenderer, new int[] { myValue });
			if (spriteValue != 1) spriteRenderer = new TimesRenderer(spriteRenderer, new int[] { spriteValue });
			
			Renderer lhs = new JoinRenderer(myRenderer, spriteRenderer, "+");
			Renderer rhs = new TimesRenderer(new BaseRenderer(text()), new BlankRenderer());
			Renderer problem = new JoinRenderer(lhs, rhs, "=");
			
			boolean mustSolve = myFactor != null && spriteFactor != null;
			boolean calcAnswer = !mustSolve || !previewAdd();
			
			//Don't say what the answer is if this is a preview
			final int answer = calcAnswer ? myValue + spriteValue : TimesRenderer.UNKNOWN_NUMBER;
			
			SimplifyListener r = new SimplifyListener() {
				@Override
				public void wasSimplified(boolean success) {
					if (success) {
						if (myFactor != null) {
							myFactor.setValue(answer);
							myFactor.setPreviewAdd(false); //creates highlight.. for a good reason I won't explain
							// remove any excess negative modifiers
							for (int i = 0; i < modifiers.modifiers.children.size(); i++) {
								ModifierBlock mod = modifiers.modifiers.children.get(i);
								if (mod != myFactor) { 
									modifiers.modifiers.removeChild(mod, true);
									i--;
								}
							}
						} else {
							ArrayList<ModifierBlock> modChildren = new ArrayList<ModifierBlock>();
							while (!modifiers.children.isEmpty()) {
								modChildren.add(modifiers.removeChild(modifiers.children.get(0)));
							}
							if (modifiers.modifiers != null) modifiers.removeModifiers();
							if (answer != 1) addModifier(new TimesBlock(answer), true);
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
			if (mustSolve) {
				if (previewAdd()) {
					r.wasSimplified(true); //show preview
				} else if (blockListener != null){
					blockListener.wasReduced(problem, answer, 
							myValue, Stat.Plus, Difficulty.rankPlus(myValue, spriteValue), r);
				}
			} else {
				r.wasSimplified(true);
			}
			
			
			return null;
		} else {
			return super.addBlock(sprite, snap);
		}
	}
	
	@Override
	public void persist(Data data) throws NumberFormatException, ParseDataException {
		super.persist(data);
		symbol = data.persist(symbol);
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new VariableBlock(null);
			}
		};
	}

	@Override
	protected int getBaseValue(int answer) {
		return answer;
	}
}
