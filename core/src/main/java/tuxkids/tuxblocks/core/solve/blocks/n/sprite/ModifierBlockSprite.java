package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Color;
import playn.core.Layer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite.BlockListener;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public abstract class ModifierBlockSprite extends BlockSprite {
	
	protected int timeElapsed;
	protected boolean canRelease;
	protected ModifierGroup group;
	protected int value;
	
	protected abstract String operator();
	
	public ModifierGroup group() {
		return group;
	}
	
	public ModifierBlockSprite(int value) {
		this.value = value;
		layer = generateNinepatch(text(), Colors.WHITE);
	}
	
	@Override
	public void update(int delta) {
		if (canRelease != canRelease(true)) {
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
	
//	private void updateTranslation() {
//		dragging.layer().setTranslation(lastTouchX - dragOffX * dragging.width(), 
//				lastTouchY - dragOffY * height());
//		blockListener.wasMoved(dragging, dragging.centerX(), dragging.centerY());
//	}
	
	@Override
	public void remove() {
		group = null;
	}

	
	public String text() {
		return operator() + value;
	}
	
	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(value);
	}
}
