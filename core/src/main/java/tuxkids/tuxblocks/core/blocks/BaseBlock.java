package tuxkids.tuxblocks.core.blocks;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.TextFormat;
import tuxkids.tuxblocks.core.expression.Expression;
import tuxkids.tuxblocks.core.expression.ModificationOperation;

public abstract class BaseBlock extends Block {

	protected GroupLayer groupLayer;
	protected List<ModifierBlock> modifiers = new ArrayList<ModifierBlock>();
	protected Expression baseExpression;
	
	public ModifierBlock getLastModifier() {
		if (modifiers.isEmpty()) return null;
		return modifiers.get(modifiers.size() - 1);
	}

	public Expression getTopLevelExpression() {
		if (modifiers.isEmpty()) return baseExpression;
		return getLastModifier().getModifier();
	}
	
	public boolean hasModifier() {
		return !modifiers.isEmpty();
	}
	
	public float getGroupWidth() {
		Block lastModifier = getLastModifier();
		if (lastModifier == null) return width();
		return lastModifier.width() + lastModifier.sprite.tx();
	}
	
	public float getGroupHeight() {
		Block lastModifier = getLastModifier();
		if (lastModifier == null) return height();
		return -lastModifier.sprite.ty();
	}
	
	public GroupLayer getGroupLayer() {
		return groupLayer;
	}
	
	public BaseBlock(Expression baseExpression) {
		this.baseExpression = baseExpression;
		groupLayer = PlayN.graphics().createGroupLayer();
	}
	
	@Override
	protected ImageLayer generateSprite(int width, int height, String text, int color) {
		ImageLayer l = super.generateSprite(width, height, text, color);
		l.setTy(-l.height());
		groupLayer.add(l);
		return l;
	}

	public void addModifier(ModificationOperation mod) {
		ModifierBlock modBlock;
		if (mod.getPrecedence() == Expression.PREC_ADD) {
			modBlock = new ModifierBlock(mod, MOD_SIZE, (int)getGroupHeight());
			modBlock.sprite.setTx(getGroupWidth());
			modBlock.sprite.setTy(-getGroupHeight());
			groupLayer.add(modBlock.sprite);
		} else {
			modBlock = new ModifierBlock(mod, (int)getGroupWidth(), MOD_SIZE);
			modBlock.sprite.setTy(-getGroupHeight() - modBlock.height());
		}
		mod.setOperand(getTopLevelExpression());
		modifiers.add(modBlock);
		groupLayer.add(modBlock.sprite);
	}

	public ModifierBlock pop() {
		if (modifiers.size() == 0) return null;
		ModifierBlock modBlock = modifiers.remove(modifiers.size() - 1);
		modBlock.getModifier().setOperand(null);
		groupLayer.remove(modBlock.sprite);
		return modBlock;
	}
	
	@Override
	public String toString() {
		return modifiers.toString();
	}

	public Object toMathString() {
		return getTopLevelExpression().toMathString();
	}
}
