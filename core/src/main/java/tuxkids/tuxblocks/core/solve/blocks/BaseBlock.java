package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Layer.HitTester;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import pythagoras.f.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.solve.expression.Expression;
import tuxkids.tuxblocks.core.solve.expression.ModificationOperation;
import tuxkids.tuxblocks.core.solve.expression.NonevaluatableException;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.Debug;

public abstract class BaseBlock extends Block {

	protected GroupLayer groupLayer;
	protected List<ModifierBlock> modifiers = new ArrayList<ModifierBlock>();
	protected Expression baseExpression;
	protected ModifierBlock previewBlock;
	protected ImageLayer simplifyCircle;
	protected OnSimplifyListener simplifyListener;
	
	public boolean isShowingPreview() {
		return previewBlock != null;
	}
	
	public void setSimplifyListener(OnSimplifyListener simplifyListener) {
		this.simplifyListener = simplifyListener;
	}
	
	public ModifierBlock lastModifier() {
		if (modifiers.isEmpty()) return null;
		return modifiers.get(modifiers.size() - 1);
	}

	public Expression topLevelExpression() {
		if (modifiers.isEmpty()) return baseExpression;
		return lastModifier().getModifier();
	}
	
	public boolean hasModifier() {
		return !modifiers.isEmpty();
	}
	
	public float groupWidth() {
		Block lastModifier = lastModifier();
		if (lastModifier == null) return width();
		return lastModifier.width() + lastModifier.sprite.tx();
	}
	
	public float groupHeight() {
		Block lastModifier = lastModifier();
		if (lastModifier == null) return height();
		return -lastModifier.sprite.ty();
	}
	
	@Override
	public GroupLayer layer() {
		return groupLayer;
	}
	
	public BaseBlock(Expression baseExpression) {
		this.baseExpression = baseExpression;
		groupLayer = PlayN.graphics().createGroupLayer();
		
		final int rad = MOD_SIZE / 4, padding = MOD_SIZE / 4;
		CanvasImage simplifyImage = CanvasUtils.createCircle(rad, getColor(), 1, Colors.DARK_GRAY);
		simplifyCircle = graphics().createImageLayer(simplifyImage);
		simplifyCircle.setOrigin(simplifyImage.width() / 2, simplifyImage.height() / 2);
		simplifyCircle.setDepth(5);
		simplifyCircle.setTint(getColor());
		simplifyCircle.setAlpha(0.8f);
		simplifyCircle.addListener(new SimplifyListener());
		simplifyCircle.setHitTester(new HitTester() {
			@Override
			public Layer hitTest(Layer layer, Point p) {
				float r = rad;
				//if (modifiers.size() > 1) r += padding;
				r += padding;
				float dx = p.x - rad;
				float dy = p.y - rad;
				if (dx * dx + dy * dy <  r * r) {
					return layer;
				}
				return null;
			}
		});
		groupLayer.add(simplifyCircle);
		updateSimplify();
	}
	
	protected abstract boolean canSimplify();
	protected abstract String getText();
	
	private void updateSimplify() {
		if (!canSimplify() || modifiers.isEmpty()) {
			simplifyCircle.setVisible(false);
		} else {
			ModifierBlock block = modifiers.get(0);
			if (block.getModifier().getPrecedence() == Expression.PREC_ADD) {
				simplifyCircle.setTranslation(width(), -height() / 2);
			} else {
				simplifyCircle.setTranslation(width() / 2, -height());
			}
			simplifyCircle.setVisible(true);
		}
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
			modBlock = new ModifierBlock(mod, MOD_SIZE, (int)groupHeight());
			modBlock.sprite.setTx(groupWidth());
			modBlock.sprite.setTy(-groupHeight());
			groupLayer.add(modBlock.sprite);
		} else {
			modBlock = new ModifierBlock(mod, (int)groupWidth(), MOD_SIZE);
			modBlock.sprite.setTy(-groupHeight() - modBlock.height());
		}
		mod.setOperand(topLevelExpression());
		if (isPreview) {
			if (previewBlock != null) {
				groupLayer.remove(previewBlock.layer());
			} 
			previewBlock = modBlock;
			previewBlock.sprite.setAlpha(0.5f);
		} else {
			modifiers.add(modBlock);
		}
		groupLayer.add(modBlock.sprite);
		updateSimplify();
	}

	public ModifierBlock pop() {
		if (modifiers.size() == 0) return null;
		ModifierBlock modBlock = modifiers.remove(modifiers.size() - 1);
		modBlock.getModifier().setOperand(null);
		groupLayer.remove(modBlock.sprite);
		updateSimplify();
		return modBlock;
	}
	
	private Point nextBlockPos = new Point();
	public Point getNextBlockPos(int precidence) {
		if (precidence == Expression.PREC_ADD) {
			return nextBlockPos.set(groupLayer.tx() + groupWidth() + MOD_SIZE / 2,
					groupLayer.ty() - groupHeight() / 2);
		} else {
			return nextBlockPos.set(groupLayer.tx() + groupWidth() / 2,
					groupLayer.ty() - groupHeight() - MOD_SIZE / 2);
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
		if (previewBlock != null) groupLayer.remove(previewBlock.layer());
		previewBlock = null;
	}
	
	@Override
	public String toString() {
		return modifiers.toString();
	}

	public Object toMathString() {
		return topLevelExpression().toMathString();
	}
	
	public interface OnSimplifyListener {
		public void onSimplify(BaseBlock baseBlock, String expression, int answer, int start);
	}
	
	protected class SimplifyListener implements Listener {
		@Override
		public void onPointerStart(Event event) {
			float dx = simplifyCircle.width() / 2 - event.localX();
			float dy = simplifyCircle.height() / 2 - event.localY();
			float rad = simplifyCircle.width() / 2;
			if (dx * dx + dy * dy > rad * rad) {
				//Propogate...
			}
		}

		@Override
		public void onPointerEnd(Event event) {
			float dx = simplifyCircle.width() / 2 - event.localX();
			float dy = simplifyCircle.height() / 2 - event.localY();
			float rad = simplifyCircle.width() * 2;
			if (dx * dx + dy * dy < rad * rad) {
				if (simplifyListener != null && !modifiers.isEmpty()) {
					String exp = modifiers.get(0).getModifier().toMathString();
					int answer = 0;
					int start = 0;
					try {
						answer = modifiers.get(0).getModifier().evaluate();
						start = baseExpression.evaluate();
					} catch (NonevaluatableException e) {
						e.printStackTrace();
					}
					simplifyListener.onSimplify(BaseBlock.this, exp + " = %", answer, start);
				}
			}
		}

		@Override
		public void onPointerDrag(Event event) {
		}

		@Override
		public void onPointerCancel(Event event) {
			
		}
	}

	public void simplfy(int to) {
		ModifierBlock remove = modifiers.remove(0);
		if (!modifiers.isEmpty()) {
			modifiers.get(0).getModifier().setOperand(baseExpression);
		}
		float width = sprite.width(), height = sprite.height();
		if (remove.getModifier().getPrecedence() == Expression.PREC_ADD) {
			width += MOD_SIZE;
		} else {
			height += MOD_SIZE;
		}
		((NumberBlock)this).setValue(to);
		sprite.destroy();
		remove.layer().destroy();
		sprite = generateSprite((int)width, (int)height, getText(), getColor());
		updateSimplify();
	}
}
