package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Color;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.HashCode;

public abstract class ModifierBlockSprite extends BlockSprite {
	
	protected int timeElapsed;
	protected boolean canRelease;
	protected ModifierGroup group;
	protected int value;
	protected ModifierBlockSprite inverse;
	
	protected abstract String operator();
	protected abstract ModifierBlockSprite inverseChild();
	
	public ModifierGroup group() {
		return group;
	}
	
	public final ModifierBlockSprite inverse() {
		return inverse;
	}
	
	public ModifierBlockSprite(int value) {
		this.value = value;
		inverse = inverseChild();
	}
	
	protected ModifierBlockSprite(ModifierBlockSprite inverse) {
		this.value = inverse.value;
		this.inverse = inverse;
		if (inverse.blockListener != null) {
			addBlockListener(inverse.blockListener);
		}
	}
	
	@Override
	protected void initSpriteImpl() {
		super.initSpriteImpl();
		
		layer = generateNinepatch(text(), Colors.WHITE);
		if (inverse.layer == null) inverse.initSprite();
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if (canRelease != canRelease(multiExpression)) {
			canRelease = !canRelease;
			if (!canRelease) {
				layer.setTint(Colors.WHITE);
			}
		}
	}
	
	@Override
	public void paint(Clock clock) {
		timeElapsed += clock.dt();
		if (canRelease) {
			layer.setTint(Color.rgb(200, 200, 255), Colors.WHITE, FloatMath.pow(FloatMath.sin(timeElapsed / 1250f * 2 * FloatMath.PI) / 2 + 0.5f, 0.7f));
		}
		if (group == null) {
			interpolateDefaultRect(clock);
		}
	}
	
	@Override
	public void addBlockListener(BlockListener listener) {
		super.addBlockListener(listener);
		if (listener != null && inverse.blockListener == null) {
			inverse.addBlockListener(listener);
		}
	}
	
	@Override
	public void remove() {
		if (group != null) group.removeChild(this);
		group = null;
	}

	@Override
	public String text() {
		return operator() + value;
	}
	
	public boolean canSimplify() {
		if (group == null) return false;
		return group.children.contains(inverse);
	}

	public boolean canAddInverse() {
		if (group == null) return false;
		return !canSimplify() && group.modifiers == null;
	}
	
	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(value);
	}
	
	public void destroy() {
		layer().destroy();
	}
}
