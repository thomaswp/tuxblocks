package tuxkids.tuxblocks.core.solve.build;

import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.solve.SolveScreen;
import tuxkids.tuxblocks.core.widget.HeaderLayer;

/**
 * A modified {@link SolveScreen} used when solving equations
 * created on the {@link BuildScreen}.
 */
public class BuildSolveScreen extends SolveScreen {

	@Override
	protected float equationXPercent() {
		// center the equation exactly because there aren't header
		// items to work around, like in SolveScreen
		return 0.5f;
	}
	
	public BuildSolveScreen(ScreenStack screens, GameState gameState) {
		super(screens, gameState);
	}
	
	@Override
	public HeaderLayer createHeader() {
		return new HeaderLayer(width(), state.themeColor());
	}

}
