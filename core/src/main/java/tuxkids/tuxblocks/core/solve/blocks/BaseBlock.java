package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.TextFormat;
import pythagoras.f.Point;
import tuxkids.tuxblocks.core.solve.expression.Expression;
import tuxkids.tuxblocks.core.solve.expression.ModificationOperation;

public abstract class BaseBlock extends Block {

	protected GroupLayer groupLayer;
	protected List<ModifierBlock> modifiers = new ArrayList<ModifierBlock>();
	protected Expression baseExpression;
	protected ModifierBlock previewBlock;
	
	public boolean isShowingPreview() {
		return previewBlock != null;
	}
	
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
		addModifier(mod, false);
	}
	
	public void addModifier(ModificationOperation mod, boolean isPreview) {
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
		if (isPreview) {
			if (previewBlock != null) {
				groupLayer.remove(previewBlock.getSprite());
			} 
			previewBlock = modBlock;
			previewBlock.sprite.setAlpha(0.3f);
		} else {
			modifiers.add(modBlock);
		}
		groupLayer.add(modBlock.sprite);
	}

	public ModifierBlock pop() {
		if (modifiers.size() == 0) return null;
		ModifierBlock modBlock = modifiers.remove(modifiers.size() - 1);
		modBlock.getModifier().setOperand(null);
		groupLayer.remove(modBlock.sprite);
		return modBlock;
	}
	
	private Point nextBlockPos = new Point();
	public Point getNextBlockPos(int precidence) {
		if (precidence == Expression.PREC_ADD) {
			return nextBlockPos.set(groupLayer.tx() + getGroupWidth() + MOD_SIZE / 2,
					groupLayer.ty() - getGroupHeight() / 2);
		} else {
			return nextBlockPos.set(groupLayer.tx() + getGroupWidth() / 2,
					groupLayer.ty() - getGroupHeight() - MOD_SIZE / 2);
		}
	}
	
	public void updateShowPreview(float cx, float cy, ModificationOperation mod) {
		Point nextPos = getNextBlockPos(mod.getPrecedence());
		boolean showPreview = nextPos.distance(cx, cy) < MOD_SIZE * 2;
		if (!showPreview) {
			stopShowingPreview();
		} else if (previewBlock == null || previewBlock.getModifier() != mod) {
			addModifier(mod, true);
		}
	}
	
	public void stopShowingPreview() {
		if (previewBlock != null) groupLayer.remove(previewBlock.getSprite());
		previewBlock = null;
	}
	
	@Override
	public String toString() {
		return modifiers.toString();
	}

	public Object toMathString() {
		return getTopLevelExpression().toMathString();
	}
}
