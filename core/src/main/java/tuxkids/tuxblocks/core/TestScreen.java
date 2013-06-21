package tuxkids.tuxblocks.core;

import playn.core.Color;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class TestScreen extends GameScreen {
	
	public TestScreen(ScreenStack screens, GameState state) {
		super(screens, state);
	}
	
	int tint = Color.rgb(125, 255, 0);
	ImageLayer l1;
	ImageLayerTintable l2;

	@Override
	public void wasAdded() {
		
		layer.add(graphics().createImageLayer(CanvasUtils.createRect(width(), height(), Colors.LIGHT_GRAY)).setDepth(-1));

		Image test = CanvasUtils.createCircle(50, Colors.PINK, 3, Colors.GREEN);
		l1 = graphics().createImageLayer(test);
		l1.setTint(tint);
		layer.add(l1);
		
		l2 = new ImageLayerTintable(test);
		layer.add(l2.layer());
		l2.setTint(tint);
		l2.setTx(width() - l2.width());
	}
	
	int total;
	@Override
	public void paint(Clock clock) {
		total += clock.dt();
		float alpha = (float)Math.cos(total / 1000f * Math.PI) / 2 + 0.5f;
		l1.setTint(Colors.blend(Colors.WHITE, tint, 1 - alpha));
		l2.setTint(Colors.WHITE, tint, alpha);
	}
}
