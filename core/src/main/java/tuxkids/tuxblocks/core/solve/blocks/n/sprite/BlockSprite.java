package tuxkids.tuxblocks.core.solve.blocks.n.sprite;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font;
import playn.core.Image;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.ImageLayerLike;
import tuxkids.tuxblocks.core.layers.ImageLayerLike.Factory;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite.BlockListener;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public abstract class BlockSprite extends Sprite implements Hashable {

	protected ImageLayerLike layer;
	protected BlockListener blockListener;
	
	protected static TextFormat textFormat;
	protected static Factory factory;
	
	protected abstract String text();
	protected abstract float defaultWidth();
	protected abstract float defaultHeight();
	protected abstract boolean canRelease(boolean openSpace);
	
	public BlockSprite() {
		if (textFormat == null) {
			Font font = PlayN.graphics().createFont(Constant.FONT_NAME, Font.Style.PLAIN, 20);
			textFormat = new TextFormat().withFont(font);
		}
		if (factory == null) {
			factory = new Factory() {
				@Override
				public ImageLayerLike create(Image image) {
					return new ImageLayerTintable(image);
				}
			};
		}
	}
	
	public void interpolateDefaultRect(Clock clock) {
		interpolateRect(layer().tx(), layer().ty(), defaultWidth(), defaultHeight(), lerpBase(), clock.dt());
	}
	
	public void interpolateRect(float x, float y, float width, float height, float base, float dt) {
		float snap = 1f;
		layer().setTx(lerpTime(layer().tx(), x, base, dt, snap));
		layer().setTy(lerpTime(layer().ty(), y, base, dt, snap));
		layer.setWidth(lerpTime(width(), width, base, dt, snap));
		layer.setHeight(lerpTime(height(), height, base, dt, snap));
	}
	
	@Override
	public Layer layer() {
		return layer.layerAddable();
	}

	@Override
	public float width() {
		return layer.width();
	}

	@Override
	public float height() {
		return layer.height();
	}

//	protected ImageLayerLike generateNinepatch(String text, int color) {
//		TextLayout layout = PlayN.graphics().layoutText(text, textFormat);
//		int sides = 2;
//		int width = sides * 4 + (int)layout.width();
//		int height = sides * 4 + (int)layout.height();
//		
//		int[] widthDims = new int[] { sides, sides, width - sides * 4, sides, sides };
//		int[] heightDims = new int[] { sides, sides, height - sides * 4, sides, sides };
//		
//		CanvasImage image = CanvasUtils.createRect(width, height, Color.withAlpha(color, 255), 1, Colors.DARK_GRAY);
//		
//		float textX = (image.width() - layout.width()) / 2;
//		float textY = (image.height() - layout.height()) / 2;
//		image.canvas().setFillColor(Colors.BLACK);
//		image.canvas().fillText(layout, textX, textY);
//		
//		
//		NinepatchLayer ninePatch = new NinepatchLayer(factory, image, widthDims, heightDims);
//		return ninePatch;
//	}
	
	protected ImageLayerLike generateNinepatch(String text, int color) {
		return new BlockLayer(text, 10, 10);
	}
	
	public void addBlockListener(BlockListener listener) {
		blockListener = listener;
		layer.addListener(new Listener() {
			boolean dragging;
			
			@Override
			public void onPointerStart(Event event) {
				if (canRelease(true)) {
					dragging = true;
					blockListener.wasGrabbed(BlockSprite.this, event);
				}
//				if (canRelease) {
//					dragging = getDragging();
//					group = null;
//					
//					dragOffX = (event.x() - getGlobalTx(layer.layerAddable())) / width();
//					dragOffY = (event.y() - getGlobalTy(layer.layerAddable())) / height();
//					lastTouchX = event.x(); lastTouchY = event.y();
//					
//					
//					blockListener.wasGrabbed(dragging, event.x(), event.y());
//				}
			}
			
			@Override
			public void onPointerEnd(Event event) {
				if (dragging) {
					dragging = false;
					blockListener.wasReleased(event);
				}
//				if (group == null) {
//					Layer draggingLayer = dragging.layer();
//					if (blockListener.wasReleased(dragging, dragging.centerX(), dragging.centerY())) {
//						draggingLayer.setTranslation(draggingLayer.tx() - getGlobalTx(group.layer()), 
//								draggingLayer.ty() - getGlobalTy(group.layer()));
//					} else {
//						draggingLayer.setTranslation(draggingLayer.tx() - getGlobalTx(group.layer()), 
//								draggingLayer.ty() - getGlobalTy(group.layer()));
//					}
//					dragging = null;
//				}
			}
			
			@Override
			public void onPointerDrag(Event event) {
				if (dragging) {
					blockListener.wasMoved(event);
				}
//				if (group == null) {
//					lastTouchX = event.x(); lastTouchY = event.y();
//					blockListener.wasMoved(dragging, dragging.centerX(), dragging.centerY());
//					updateTranslation();
//				}
			}
			
			@Override
			public void onPointerCancel(Event event) {
				
			}
		});
	}
	
	protected BlockSprite getDraggingSprite() {
		return this;
	}
	
	@Override
	public String toString() {
		return text();
	}
	
	public void remove() {
	}
}
