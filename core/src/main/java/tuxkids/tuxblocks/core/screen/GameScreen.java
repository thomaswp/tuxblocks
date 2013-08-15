package tuxkids.tuxblocks.core.screen;

import playn.core.util.Clock;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.game.ScreenStack.Predicate;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.utils.PersistUtils;
import tuxkids.tuxblocks.core.widget.HeaderLayer;

public class GameScreen extends BaseScreen {
	
	protected GameState state;
	protected HeaderLayer menu;

	public GameState state() {
		return state;
	}
	
	public GameScreen(ScreenStack screens, GameState state) {
		super(screens, state.background());
		this.state = state;
		menu = createMenu();
		layer.add(menu.layerAddable());
	}
	
	protected HeaderLayer createMenu() {
		return new HeaderLayer(width(), state.themeColor());
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

	public void quit() {
		PersistUtils.persist(state, Constant.KEY_GAME);
		Screen popTo = screens.find(new Predicate() {
			@Override
			public boolean apply(Screen screen) {
				return !(screen instanceof GameScreen);
			}
		});
		screens.popTo(popTo, screens.slide().up());
		Audio.bg().play(Constant.BG_MENU);
	}
	

	public boolean canSave() {
		return state.canSave();
	}
}
