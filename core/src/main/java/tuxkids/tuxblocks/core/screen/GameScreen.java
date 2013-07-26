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
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.PlayNObject;

public class GameScreen extends Screen implements Listener {
	
	protected ScreenStack screens;
	protected GameState state;
	protected GameScreen topActivity;
	private OnScreenFinishedListener onScreenFinishedListener;
	protected int depth;
	private boolean entering, exiting;
	
	public static float defaultButtonSize() {
		return graphics().height() * 0.15f;
	}
	
	public Button createMenuButton(String path) {
		Button button = new Button(path, defaultButtonSize(), defaultButtonSize(), true);
		button.setTint(state.themeColor());
		return button;
	}
	
	public boolean exiting() {
		return exiting;
	}
	
	public boolean entering() {
		return entering;
	}
	
	@Override
	public void wasShown() {
		super.wasShown();
		PlayN.keyboard().setListener(this);
		entering = true;
	}

	float lastTx, lastTy;
	@Override
	public void showTransitionCompleted() {
		super.showTransitionCompleted();
		lastTx = 0;
		lastTy = 0;
		entering = false;
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
	public void update(int delta) {
		if (exiting()) {
			state.background().scroll(layer.tx() - lastTx, layer.ty() - lastTy);
			lastTx = layer.tx();
			lastTy = layer.ty();
		}
	}
	
	@Override
	public void wasRemoved() {
		//layer.destroy();
	}
	
	public int getDepth() {
		return depth;
	}
	
	public GameState state() {
		return state;
	}
	
	public GameScreen(ScreenStack screens, GameState state) {
		this.screens = screens;
		this.state = state;
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
	
	protected void pushScreen(GameScreen screen) {
		pushScreen(screen, screens.slide().left());
	}
	
	protected void pushScreen(final GameScreen screen, Transition transition) {
		topActivity = screen;
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
	
	protected void onChildScreenFinished(GameScreen screen) {
		
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
