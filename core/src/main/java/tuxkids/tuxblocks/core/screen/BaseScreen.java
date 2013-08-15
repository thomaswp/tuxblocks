package tuxkids.tuxblocks.core.screen;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import playn.core.Graphics;
import playn.core.Key;
import playn.core.Layer;
import playn.core.Keyboard.Event;
import playn.core.Keyboard.Listener;
import playn.core.Keyboard.TypedEvent;
import playn.core.PlayN;
import pythagoras.f.Vector;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.game.ScreenStack.Transition;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.layers.LayerLike;
import tuxkids.tuxblocks.core.tutorial.Highlightable;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.widget.GameBackgroundSprite;
import tuxkids.tuxblocks.core.widget.MainMenuLayer;

public class BaseScreen extends Screen implements Listener {

	protected ScreenStack screens;
	protected final GameBackgroundSprite background;
	
	private OnScreenFinishedListener onScreenFinishedListener;
	private boolean entering, exiting;
	private int depth;
	private float lastTx, lastTy;
	private List<Highlightable> highlightables = new ArrayList<Highlightable>();
	private boolean showing;
	
	public boolean exiting() {
		return exiting;
	}
	
	public boolean entering() {
		return entering;
	}
	
	public int screenDepth() {
		return depth;
	}
	
	protected Trigger wasShownTrigger() {
		return null;
	}
	
	@Override
	public void wasShown() {
		super.wasShown();
		PlayN.keyboard().setListener(this);
		entering = true;
		addHighlightables();
		showing = true;
	}
	
	public void register(Highlightable highlightable, Tag tag) {
		highlightable.highlighter().addTag(tag);
		if (!highlightables.contains(highlightable)) {
			highlightables.add(highlightable);
		}
		if (showing) {
			Tutorial.addHighlightable(highlightable);
		}
	}
	
	private void addHighlightables() {
		for (Highlightable h : highlightables) {
			Tutorial.addHighlightable(h);
		}
	}

	@Override
	public void showTransitionCompleted() {
		super.showTransitionCompleted();
		entering = false;
		lastTx = 0;
		lastTy = 0;
		Tutorial.trigger(wasShownTrigger());
	}
	
	@Override
	public void hideTransitionStarted() {
		exiting = true;
	}
	
	@Override
	public void wasHidden() {
		showing = false;
		exiting = false;
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
	
	protected static void lerpAlpha(LayerLike layer, float target, float base, float dt) {
		PlayNObject.lerpAlpha(layer, target, base, dt);
	}
	
	protected static void lerpAlpha(Layer layer, float target, float base, float dt) {
		PlayNObject.lerpAlpha(layer, target, base, dt);
	}
	
	protected final void pushScreen(GameScreen screen) {
		pushScreen(screen, screens.slide().left());
	}
	
	protected void pushScreen(final BaseScreen screen, Transition transition) {
		Tutorial.clearIndicators();
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
		Tutorial.clearIndicators();
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
		if (event.key() == Key.BACK || event.key() == Key.DOWN) {
			if (MainMenuLayer.showing()) {
				MainMenuLayer.toggle(this);
			} else {
				popThis();
			}
		}
		if (event.key() == Key.MENU || event.key() == Key.ESCAPE) {
			MainMenuLayer.toggle(this);
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
