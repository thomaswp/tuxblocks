package tuxkids.tuxblocks.core;

import static playn.core.PlayN.currentTime;
import static playn.core.PlayN.graphics;
import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Game;
import playn.core.ImageLayer;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.defense.DefenseScreen;
import tuxkids.tuxblocks.core.solve.BuildScreen;
import tuxkids.tuxblocks.core.solve.blocks.EquationGenerator;

public class TuxBlocksGame extends Game.Default {

	public static final int UPDATE_RATE = 1000 / 30;
	private final static int MAX_DELTA = (int)(UPDATE_RATE * 1.5f);
	
	protected final SolidClock clock = new SolidClock(UPDATE_RATE);
	//protected final Clock.Source clock = new Clock.Source(UPDATE_RATE);

    protected final ScreenStack screens = new ScreenStack() {
//        @Override protected void handleError (RuntimeException error) {
//            log().warn("Screen failure", error);
//        }
        @Override public Transition defaultPushTransition () {
//        	return fade;
            return slide();
        }
        @Override public Transition defaultPopTransition () {
            //return new FadeTransition(screens); 
            return slide().right();
        }
    };
    
	private static TuxBlocksGame instance;
	
	private GameBackgroundSprite background;
	
	public static int screenDepth() {
		return instance.screens.size();
	}
	
	public TuxBlocksGame() {
		super(UPDATE_RATE);
		instance = this;
	}

	@Override
	public void init() {
		
		GameState state = new GameState();
		background = state.background();
		background.layer().setDepth(-10);
		
		
		graphics().rootLayer().add(background.layer());
//		screens.push(new DefenseScreen(screens, state));
		screens.push(new BuildScreen(screens, state));
		
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
			fpsLayer.setDepth(Float.MAX_VALUE);
			graphics().rootLayer().add(fpsLayer);
			frames = 0;
		}
	}

	@Override
	public void update(int delta) {
		delta = Math.min(delta, MAX_DELTA);
		clock.update(delta);
		background.update(delta);
        screens.update(delta);
	}

	@Override
	public void paint(float alpha) {
		clock.paint(alpha);
		background.paint(clock);
        screens.paint(clock);
		updateFPS();
	}
}
