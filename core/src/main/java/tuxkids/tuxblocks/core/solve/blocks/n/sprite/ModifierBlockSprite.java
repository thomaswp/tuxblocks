package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.Color;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite.BlockListener;

public abstract class ModifierBlockSprite extends BlockSprite {
	
	protected BlockListener blockListener;
	protected int timeElapsed;
	protected boolean canRelease;
	protected ModifierGroup group;
	protected int value;
	
	protected abstract String operator();
	protected abstract void interpolateDefaultRect(Clock clock);
	protected abstract boolean canRelease(boolean openSpace);
	
	public ModifierGroup group() {
		return group;
	}
	
	public ModifierBlockSprite(int value) {
		this.value = value;
		layer = generateNinepatch(text(), Colors.WHITE);
	}
	
	public void interpolateRect(float x, float y, float width, float height, float base, float dt) {
		float snap = 1f;
		layer().setTx(lerpTime(layer().tx(), x, base, dt, snap));
		layer().setTy(lerpTime(layer().ty(), y, base, dt, snap));
		layer.setWidth(lerpTime(width(), width, base, dt, snap));
		layer.setHeight(lerpTime(height(), height, base, dt, snap));
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
			updateTranslation();
		}
	}
	
	private float dragOffX;
	private float dragOffY;
	private float lastTouchX, lastTouchY;
	
	private void updateTranslation() {
		layer.setTranslation(lastTouchX - dragOffX * width(), 
				lastTouchY - dragOffY * height());
		blockListener.wasMoved(ModifierBlockSprite.this, left() + width() / 2, top() + height() / 2);
	}

	public void addBlockListener(BlockListener listener) {
		blockListener = listener;
		layer.addListener(new Listener() {
			
			@Override
			public void onPointerStart(Event event) {
				if (canRelease) {
					group = null;
					
					dragOffX = (event.x() - getGlobalTx(layer.layerAddable())) / width();
					dragOffY = (event.y() - getGlobalTy(layer.layerAddable())) / height();
					lastTouchX = event.x(); lastTouchY = event.y();
					
					blockListener.wasGrabbed(ModifierBlockSprite.this, event.x(), event.y());
				}
			}
			
			@Override
			public void onPointerEnd(Event event) {
				if (group == null) {
					if (blockListener.wasReleased(ModifierBlockSprite.this, left() + width() / 2, top() + height() / 2)) {
						layer.setTranslation(layer.tx() - getGlobalTx(group.layer()), 
								layer.ty() - getGlobalTy(group.layer()));
					} else {
						layer.setTranslation(layer.tx() - getGlobalTx(group.layer()), 
								layer.ty() - getGlobalTy(group.layer()));
					}
				}
			}
			
			@Override
			public void onPointerDrag(Event event) {
				if (group == null) {
					lastTouchX = event.x(); lastTouchY = event.y();
					blockListener.wasMoved(ModifierBlockSprite.this, left() + width() / 2, top() + height() / 2);
				}
			}
			
			@Override
			public void onPointerCancel(Event event) {
				
			}
		});
	}
	
	public String text() {
		return operator() + value;
	}
}
