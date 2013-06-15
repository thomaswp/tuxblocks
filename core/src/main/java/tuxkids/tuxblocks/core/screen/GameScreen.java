package tuxkids.tuxblocks.core.screen;

import playn.core.Graphics;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.Keyboard.Event;
import playn.core.Keyboard.Listener;
import playn.core.Keyboard.TypedEvent;
import playn.core.PlayN;
import pythagoras.f.Vector;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.PlayNObject;

public class GameScreen extends Screen implements Listener {
	protected ScreenStack screens;
	protected GameScreen topActivity;
	private OnScreenFinishedListener onScreenFinishedListener;
	protected int depth;
	
	@Override
	public void wasShown() {
		super.wasShown();
		PlayN.keyboard().setListener(this);
	}
	
	@Override
	public void wasRemoved() {
		layer.destroy();
	}
	
	public int getDepth() {
		return depth;
	}
	
	public GameScreen(ScreenStack screens) {
		this.screens = screens;
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
	
	protected void pushScreen(final GameScreen screen) {
		topActivity = screen;
		screen.onScreenFinishedListener = new OnScreenFinishedListener() {
			@Override
			public void onScreenFinished() {
				onChildScreenFinished(screen);
			}
		};
		screen.depth = depth + 1;
		screens.push(screen);
	}
	
	protected void popThis() {
		PlayN.keyboard().setListener(null);
		screens.remove(this);
		layer.setDepth(-1);
		if (onScreenFinishedListener != null) onScreenFinishedListener.onScreenFinished();
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
