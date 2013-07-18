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
		layer.addAt(expression.layerAddable(), x, y);
		baseBlocks.add(expression);
		expression.addBlockListener(listener);
	}
	
	public void update(int delta) {
		for (BaseBlockSprite sprite : baseBlocks) {
			sprite.update(delta);
		}
		if (dragging != null) dragging.update(delta);
	}
	
	public void paint(Clock clock) {
		for (BaseBlockSprite sprite : baseBlocks) {
			sprite.paint(clock);
		}
		if (dragging != null) dragging.paint(clock);
		updatePosition();
	}
	
	private void updatePosition() {
		if (dragging != null) {
			dragging.layer().setTranslation(lastTouchX - dragging.width() * blockAnchorPX, 
					lastTouchY - dragging.height() * blockAnchorPY);
		}
	}
	
	private class Listener implements BlockListener {
		
//		@Override
//		public void wasGrabbed(BlockSprite sprite, float gx, float gy) {
//			for (BaseBlockSprite base : baseBlocks) {
//				if (base.contains(gx, gy)) {
//					draggingFrom = base;
//				}
//			}
//			layer.add(sprite.layer());
//			sprite.layer().setDepth(1);
//			dragging = sprite;
//		}
//
//		@Override
//		public void wasMoved(BlockSprite sprite, float gx, float gy) {
//			for (BaseBlockSprite base : baseBlocks) {
//				base.setPreview(base.contains(gx, gy) && base.canAccept(sprite));
//			}
//		}
//
//		@Override
//		public boolean wasReleased(BlockSprite sprite, float gx, float gy) {
//			dragging = null;
//			BaseBlockSprite r = null;
//			for (BaseBlockSprite base : baseBlocks) {
//				base.clearPreview();
//				if (base.contains(gx, gy) && base.canAccept(sprite)) r = base;
//			}
//			if (r != null) {
//				r.addBlock(sprite, false);
//				return true;
//			} else {
//				draggingFrom.addBlock(sprite, false);
//				return false;
//			}
//		}

		@Override
		public void wasGrabbed(BlockSprite sprite, Event event) {
			for (BaseBlockSprite base : baseBlocks) {
				if (base.contains(event.x(), event.y())) {
					draggingFrom = base;
					break;
				}
			}
			
			
			dragging = sprite.getDraggingSprite();
			blockAnchorPX = (event.x() - getGlobalTx(sprite.layer())) / sprite.width();
			blockAnchorPY = (event.y() - getGlobalTy(sprite.layer())) / sprite.height();
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
			updatePosition();
			
			BaseBlockSprite target = null;
			for (BaseBlockSprite base : baseBlocks) {
				base.clearPreview();
				if (base.contains(event.x(), event.y()) && base.canAccept(dragging)) {
					target = base;
					break;
				}
			}
			
			if (target == null) target = draggingFrom;
			
			dragging.layer().setTranslation(dragging.layer().tx() - getGlobalTx(target.layer()), 
					dragging.layer().ty() - getGlobalTy(target.layer()));
			target.addBlock(dragging, false);
			
			dragging = null;
			draggingFrom = null;
		}

		@Override
		public void wasMoved(Event event) {
			lastTouchX = event.x();
			lastTouchY = event.y();
			updatePosition();
			
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
