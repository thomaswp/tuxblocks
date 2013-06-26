package tuxkids.tuxblocks.core.solve.blocks;

import playn.core.ImageLayer;
import tuxkids.tuxblocks.core.solve.expression.ModificationOperation;
import tuxkids.tuxblocks.core.utils.Formatter;

public class ModifierBlock extends Block {
	
	private ModificationOperation modifier, inverseModifier;
	private boolean isInverted;
	private ImageLayer inverseSprite;
	
	public ModificationOperation getModifier() {
		return isInverted ? inverseModifier : modifier;
	}
	
	public ModificationOperation getOriginalModifier() {
		return modifier;
	}
	
	public ImageLayer layer() {
		return isInverted ? inverseSprite : sprite;
	}
	
	public boolean isInverted() {
		return isInverted;
	}

	public void setInverted(boolean inverted) {
		if (isInverted != inverted) {
			invert();
		}
	}
	
	public ModifierBlock(ModificationOperation op, int width, int height) {
		this.modifier = op;
		inverseModifier = op.getInverse();
		sprite = generateSprite(width, height,
				Formatter.format("%s%d", op.getSymbol(), op.getValue()), 
				op.getColor());
		inverseSprite = generateSprite(width, height, 
				Formatter.format("%s%d", inverseModifier.getSymbol(), inverseModifier.getValue()), 
				inverseModifier.getColor());
	}
	
	public void invert() {
		isInverted = !isInverted;
		if (isInverted) {
			sprite.parent().add(inverseSprite);
			sprite.parent().remove(sprite);
			inverseSprite.setTranslation(sprite.tx(), sprite.ty());
		} else {
			inverseSprite.parent().add(sprite);
			inverseSprite.parent().remove(inverseSprite);
			sprite.setTranslation(inverseSprite.tx(), inverseSprite.ty());
		}
	}
	
	@Override
	public String toString() {
		return Formatter.format("%s%d", modifier.getSymbol(), modifier.getValue());
	}

	@Override
	public int getColor() {
		return modifier.getColor();
	}
}
