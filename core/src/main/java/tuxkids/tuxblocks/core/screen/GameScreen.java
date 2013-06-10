package tuxkids.tuxblocks.core.screen;

import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;

public class GameScreen extends Screen {
	protected ScreenStack screens;
	
	public GameScreen(ScreenStack screens) {
		this.screens = screens;
	}
}
