package tuxkids.tuxblocks.core.screen;

import playn.core.Graphics;
import playn.core.Key;
import playn.core.Keyboard.Event;
import playn.core.Keyboard.Listener;
import playn.core.Keyboard.TypedEvent;
import playn.core.PlayN;
import pythagoras.f.Vector;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.game.ScreenStack.Transition;
import tuxkids.tuxblocks.core.GameBackgroundSprite;
import tuxkids.tuxblocks.core.PlayNObject;

public class BaseScreen extends Screen implements Listener {

	protected ScreenStack screens;
	protected final GameBackgroundSprite background;
	
	private OnScreenFinishedListener onScreenFinishedListener;
	private boolean entering, exiting;
	private int depth;
	private float lastTx, lastTy;
	
	public boolean exiting() {
		return exiting;
	}
	
	public boolean entering() {
		return entering;
	}
	
	public int screenDepth() {
		return depth;
	}
	
	@Override
	public void wasShown() {
		super.wasShown();
		PlayN.keyboard().setListener(this);
		entering = true;
	}

	@Override
	public void showTransitionCompleted() {
		super.showTransitionCompleted();
		entering = false;
		lastTx = 0;
		lastTy = 0;
	}
	
	@Override
	public void hideTransitionStarted() {
		exiting = true;
	}
	
	@Override
	public void wasHidden() {
		exiting = false;
	}

	
	@Override
	public void wasRemoved() {
		//layer.destroy();
	}
	
	public BaseScreen(ScreenStack screens, GameBackgroundSprite background) {
		this.screens = screens;
		this.background = background;
	}
	
	protected static Graphics graphics() {
		return PlayN.graphics();
	}
	
	protected static float lerp(float x0, float x1, float perc) {
		return PlayNObject.lerp(x0, x1, perc);
	}
	
	protected static void lerp(Vector v0, float x1, float y1, float perc) {
		PlayNObject.lerp(v0, x1, y1, perc);
	}
	
	protected final void pushScreen(GameScreen screen) {
		pushScreen(screen, screens.slide().left());
	}
	
	protected void pushScreen(final BaseScreen screen, Transition transition) {
		screen.onScreenFinishedListener = new OnScreenFinishedListener() {
			@Override
			public void onScreenFinished() {
				onChildScreenFinished(screen);
			}
		};
		screen.depth = depth + 1;
		screens.push(screen, transition);
	}
	
	protected void popThis(Transition transition) {
		PlayN.keyboard().setListener(null);
		screens.remove(this, transition);
		layer.setDepth(-1);
		if (onScreenFinishedListener != null) onScreenFinishedListener.onScreenFinished();
	}
	
	protected void popThis() {
		popThis(screens.slide().right());
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if (exiting()) {
			background.scroll(layer.tx() - lastTx, layer.ty() - lastTy);
			lastTx = layer.tx();
			lastTy = layer.ty();
		}
	}
	
	protected void onChildScreenFinished(BaseScreen screen) {
		
	}
	
	protected interface OnScreenFinishedListener {
		void onScreenFinished();
	}

	@Override
	public void onKeyDown(Event event) {
		if (event.key() == Key.BACK || event.key() == Key.ESCAPE) {
			popThis();
		}
	}

	@Override
	public void onKeyTyped(TypedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyUp(Event event) {
		// TODO Auto-generated method stub
		
	}
}
