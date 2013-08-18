package tuxkids.tuxblocks.core.screen;

import playn.core.util.Clock;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.game.ScreenStack.Predicate;
import tripleplay.game.ScreenStack.Transition;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.DefenseScreen;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.PersistUtils;
import tuxkids.tuxblocks.core.widget.HeaderLayer;
import tuxkids.tuxblocks.core.widget.MenuLayer;

public class GameScreen extends BaseScreen {
	
	protected GameState state;
	protected HeaderLayer header;

	public GameState state() {
		return state;
	}
	
	public GameScreen(ScreenStack screens, GameState state) {
		super(screens, state.background());
		this.state = state;
		header = createHeader();
		layer.add(header.layerAddable());
	}
	
	protected HeaderLayer createHeader() {
		return new HeaderLayer(width(), state.themeColor());
	}
	
	protected boolean validDuringRound() {
		return this instanceof DefenseScreen;
	}
	
	protected Transition toDefenseScreenTransition() {
		return screens.slide();
	}
	
	protected int exitTime() {
		return 0;
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if (!exiting()) { //Prevent overlap
			header.update(delta);
			if (!MenuLayer.showing()) {
				state.update(delta);
				if (state.difficulty().roundTime > 0 && !validDuringRound() && !entering()) {
					if (state.level().duringRound() || 
							state.level().timeUntilNextRound() < exitTime()) {
						popThis();
					}
				}
			}
		}
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		if (!exiting()) {
			header.paint(clock);
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
