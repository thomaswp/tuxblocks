package tuxkids.tuxblocks.core.tutorial;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.util.Callback;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Align;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Indicator;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class IndicatorLayer extends LayerWrapper {

	private final GroupLayer layer;
	private final ImageLayer foregroundLayer, backgroundLayer;
	
	private boolean set;
	
	public IndicatorLayer() {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		foregroundLayer = graphics().createImageLayer();
		backgroundLayer = graphics().createImageLayer();
		backgroundLayer.setDepth(-1);
		
		layer.add(foregroundLayer);
		layer.add(backgroundLayer);
	}
	
	public boolean isSet() {
		return set;
	}
	
	public void clear() {
		foregroundLayer.setImage(null);
		backgroundLayer.setImage(null);
		set = false;
	}
	
	public void set(final Indicator indicator, final int themeColor) {
		set = true;
		
		layer.setTranslation(indicator.x, indicator.y);
		
		foregroundLayer.setImage(assets().getImage(
				Constant.TUTORIAL_IMAGE_PATH + indicator.name + ".png"));
		foregroundLayer.setVisible(false);
		
//		backgroundLayer.setImage(assets().getImage(
//				Constant.TUTORIAL_IMAGE_PATH + indicator.name + "_bg.png"));
//		backgroundLayer.setVisible(false);
//
//		backgroundLayer.image().addCallback(new Callback<Image>() {
//
//			@Override
//			public void onSuccess(Image result) {
////				backgroundLayer.setImage(CanvasUtils.tintImage(result, Colors.WHITE));
////				backgroundLayer.setVisible(true);
//			}
//
//			@Override
//			public void onFailure(Throwable cause) {
//				cause.printStackTrace();
//			}
//		});
		
		
		
		foregroundLayer.image().addCallback(new Callback<Image>() {
			@Override
			public void onSuccess(Image result) {
				
				foregroundLayer.setVisible(true);
				if (indicator.color != Colors.WHITE) {
					foregroundLayer.setImage(CanvasUtils.tintImage(result, indicator.color));
				}
				if (indicator.height < 0) {
					layer.setScale(indicator.width / result.width());
				} else if (indicator.width < 0) {
					layer.setScale(indicator.height / result.height());
				} else {
					layer.setScale(indicator.width, indicator.height);
				}
				if (indicator.align == Align.Center) {
					layer.setOrigin(result.width() / 2, result.height() / 2);
				} else if (indicator.align == Align.TopRight) {
					layer.setOrigin(result.width(), 0);
				} else if (indicator.align == Align.BottomLeft) {
					layer.setOrigin(0, result.height());
				} else if (indicator.align == Align.ButtonRight) {
					layer.setOrigin(result.width(), result.height());
				} else {
					layer.setOrigin(0, 0);
				}
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
	}
	
}
