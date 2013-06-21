package tuxkids.tuxblocks.core;

import playn.core.Color;
import playn.core.Image;
import playn.core.ImageLayer;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class TestScreen extends GameScreen {
	
	public TestScreen(ScreenStack screens, GameState state) {
		super(screens, state);
	}

	@Override
	public void wasAdded() {
		int tint = Color.rgb(125, 255, 0);
		
		layer.add(graphics().createImageLayer(CanvasUtils.createRect(width(), height(), Colors.LIGHT_GRAY)).setDepth(-1));
		
		Image test = CanvasUtils.createCircle(50, Colors.PINK, 3, Colors.GREEN);
		ImageLayer l1 = graphics().createImageLayer(test);
		l1.setTint(tint);
		layer.add(l1);
		
		Image shift = CanvasUtils.tintImage(test, tint, 1);
		ImageLayer l2 = graphics().createImageLayer(shift);
		layer.add(l2);
		l2.setTx(width() - shift.width());
		
//		
//		
//		
//		Image test = CanvasUtils.createCircle(50, Colors.PINK, 3, Colors.GREEN);
//		ImageLayer l1 = graphics().createImageLayer(test);
//		l1.setTint(tint);
//		layer.add(l1);
//		
//		ImageLayerTintable l2 = new ImageLayerTintable(test);
//		layer.add(l2.layer());
//		l2.setTint(tint);
//		l2.setTx(width() - shift.width());
	}
}
