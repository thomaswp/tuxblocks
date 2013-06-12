package tuxkids.tuxblocks.core.screen;

import playn.core.Graphics;
import playn.core.PlayN;
import pythagoras.f.Vector;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.PlayNObject;

public class GameScreen extends Screen {
	protected ScreenStack screens;
	
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
}
