package tuxkids.tuxblocks.core.solve.build;

import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.title.Difficulty;

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
