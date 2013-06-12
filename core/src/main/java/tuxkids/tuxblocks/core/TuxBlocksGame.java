package tuxkids.tuxblocks.core;

import static playn.core.PlayN.*;

import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Game;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.defense.DefenseScreen;
import tuxkids.tuxblocks.core.defense.NumberSelectScreen;
import tuxkids.tuxblocks.core.screen.FadeTransition;
import tuxkids.tuxblocks.core.solve.SolveScene;

public class TuxBlocksGame extends Game.Default {

	public static final int UPDATE_RATE = 33;
	
	protected final Clock.Source clock = new Clock.Source(UPDATE_RATE);
	

    protected final ScreenStack screens = new ScreenStack() {
//        @Override protected void handleError (RuntimeException error) {
//            log().warn("Screen failure", error);
//        }
//        @Override protected Transition defaultPushTransition () {
//        	return fade;
//            //return slide();
//        }
//        @Override protected Transition defaultPopTransition () {
//            //return new FadeTransition(screens); 
//            return slide().right();
//        }
    };
    
	private FadeTransition fade = new FadeTransition(screens);
	
	public TuxBlocksGame() {
		super(UPDATE_RATE); // call update every 33ms (30 times per second)
	}

	@Override
	public void init() {
		//screens.push(new SolveScene(screens));
		//screens.push(new DefenseScreen(screens));
		screens.push(new NumberSelectScreen(screens));
	}
	
	private int frames;
	private double lastUpdate;
	private ImageLayer fpsLayer;
	private void updateFPS() {
		frames++;
		if (lastUpdate < currentTime() - 1000) {
			lastUpdate = currentTime();
			CanvasImage image = graphics().createImage(40, 13);
			Canvas canvas = image.canvas();
			canvas.setFillColor(Color.rgb(50, 50, 50));
			canvas.drawText(frames + " FPS", 0, image.height());
			if (fpsLayer != null) 
				graphics().rootLayer().remove(fpsLayer);
			fpsLayer =  graphics().createImageLayer(image);
			fpsLayer.setDepth(10000);
			graphics().rootLayer().add(fpsLayer);
			frames = 0;
		}
	}

	@Override
	public void update(int delta) {
		clock.update(delta);
        screens.update(delta);
	}

	@Override
	public void paint(float alpha) {
		clock.paint(alpha);
        screens.paint(clock);
		updateFPS();
	}
}
