package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.Layer;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite.BlockListener;

public class BlockController extends PlayNObject {
	private GroupLayer layer;
	private List<BaseBlockSprite> baseBlocks = new ArrayList<BaseBlockSprite>();
	private BaseBlockSprite draggingFrom;
	private BlockSprite dragging;
	private Listener listener = new Listener();
	private float blockAnchorPX, blockAnchorPY;
	private float lastTouchX, lastTouchY;
	
	public Layer layer() {
		return layer;
	}
	
	public BlockController() {
		layer = graphics().createGroupLayer();
	}
	
	public void addExpression(BaseBlockSprite expression, float x, float y) {
		addExpression(expression, x, y, baseBlocks.size());
	}
	
	public void addExpression(BaseBlockSprite expression, float x, float y, int index) {
		layer.addAt(expression.layerAddable(), x, y);
		expression.layer().setDepth(0);
		baseBlocks.add(index, expression);
		expression.addBlockListener(listener);
	}
	
	protected void swapExpression(BaseBlockSprite original, BaseBlockSprite newExp) {
		int index = baseBlocks.indexOf(original);
		baseBlocks.remove(index);
		addExpression(newExp, original.layer().tx(), original.layer().ty(), index);
	}
	
	public void update(int delta) {
		for (BaseBlockSprite sprite : baseBlocks) {
			sprite.update(delta);
		}
		if (dragging != null) dragging.update(delta);
	}
	
	public void paint(Clock clock) {
		float totalWidth = 0;
		for (BaseBlockSprite sprite : baseBlocks) {
			sprite.paint(clock);
			totalWidth += sprite.totalWidth();
		}
		
		float dx = (graphics().width() - totalWidth) / (baseBlocks.size() + 1);
		float x = dx;
		int i = 1;
		for (BaseBlockSprite sprite : baseBlocks) {
			x = i++ * graphics().width() / (baseBlocks.size() + 1) - sprite.totalWidth() / 2 - sprite.offsetX();
			sprite.layer().setTx(lerpTime(sprite.layer().tx(), x, 0.98f, clock.dt(), 1f));
			x += sprite.totalWidth() + dx;
		}
		
		if (dragging != null) dragging.paint(clock);
		updatePosition();
	}
	
	private void updatePosition() {
		if (dragging != null) {
			float x = lerpTime(dragging.layer().tx(), lastTouchX - dragging.width() * blockAnchorPX, 0, 1);
			float y = lerpTime(dragging.layer().ty(), lastTouchY - dragging.height() * blockAnchorPY, 0, 1);
			dragging.layer().setTranslation(x, y);
		}
	}
	
	private class Listener implements BlockListener {
		
		@Override
		public void wasGrabbed(BlockSprite sprite, Event event) {
			for (BaseBlockSprite base : baseBlocks) {
				if (base.contains(event.x(), event.y())) {
					draggingFrom = base;
					break;
				}
			}
			
			if (draggingFrom == null) {
				debug("BIG PROBLEM!");
			}
			
			blockAnchorPX = (event.x() - getGlobalTx(sprite.layer())) / sprite.width();
			blockAnchorPY = (event.y() - getGlobalTy(sprite.layer())) / sprite.height();
			
			if (sprite == draggingFrom) {
				debug("base");
				BlockHolder holder = new BlockHolder();
				swapExpression(draggingFrom, holder);
				draggingFrom = holder;
			}
			debug("%s, %s", sprite, draggingFrom.hashCode());
			
			
			dragging = sprite.getDraggingSprite();
			layer.add(dragging.layer());
			dragging.layer().setDepth(1);

			sprite.remove();
			if (sprite != dragging) {
				sprite.layer().setVisible(false);
			}
			
			lastTouchX = event.x();
			lastTouchY = event.y();
			updatePosition();
			
			
		}

		@Override
		public void wasReleased(Event event) {
			lastTouchX = event.x();
			lastTouchY = event.y();
			
			BaseBlockSprite target = null;
			for (BaseBlockSprite base : baseBlocks) {
				base.clearPreview();
				if (base.contains(event.x(), event.y()) && base.canAccept(dragging)) {
					target = base;
				}
			}
			
			if (target == null) target = draggingFrom;
			debug(target.hierarchy());
			
			if (target instanceof BlockHolder) {
				if (dragging instanceof HorizontalModifierSprite) {
					NumberBlockSpriteProxy proxy = ((HorizontalModifierSprite) dragging).getProxy(false);
					dragging.layer().setVisible(false);
					dragging = proxy;
				}
				
				swapExpression(target, (BaseBlockSprite) dragging);
				target.layer().destroy();
			} else {
				dragging.layer().setTranslation(dragging.layer().tx() - getGlobalTx(target.layer()), 
						dragging.layer().ty() - getGlobalTy(target.layer()));
				target.addBlock(dragging, false);
			}
			
			dragging = null;
			draggingFrom = null;
			
			debug(target.hierarchy());
		}

		@Override
		public void wasMoved(Event event) {
			lastTouchX = event.x();
			lastTouchY = event.y();
			
			for (BaseBlockSprite base : baseBlocks) {
				if (base.contains(event.x(), event.y()) && base.canAccept(dragging)) {
					base.setPreview(true);
				} else {
					base.setPreview(false);
				}
			}
		}
	}
}
