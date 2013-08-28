package tuxkids.tuxblocks.core.solve.build;

import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.title.Difficulty;

/**
 * A special {@link GameState} to use when in Build-mode.
 * The architecture of the game is such that a GameState is
 * still necessary, but since we're not actually playing, we
 * just override and function that would do something to do
 * nothing.
 */
public class BuildGameState extends GameState {

	public BuildGameState() {
		super(new Difficulty());
	}
	
	@Override
	public boolean canSave() {
		return false;
	}
	
	@Override
	public void addPoints(int points) {
		// NOOP
	}
	
	@Override
	public void addExp(Stat stat, int exp) {
		// NOOP
	}
	
	@Override
	public void update(int delta) {
		// NOOP
	}

}
