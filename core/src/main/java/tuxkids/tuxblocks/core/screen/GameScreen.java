package tuxkids.tuxblocks.core.screen;

import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.MenuLayer;

public class GameScreen extends BaseScreen {
	
	protected GameState state;
	protected MenuLayer menu;

	public GameState state() {
		return state;
	}
	
	public GameScreen(ScreenStack screens, GameState state) {
		super(screens, state.background());
		this.state = state;
		menu = createMenu();
		layer.add(menu.layerAddable());
	}
	
	protected MenuLayer createMenu() {
		return new MenuLayer(width(), state.themeColor());
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		menu.update(delta);
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		if (!exiting()) {
			menu.paint(clock);
		}
	}
}
