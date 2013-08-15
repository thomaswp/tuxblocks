package tuxkids.tuxblocks.core;

import tuxkids.tuxblocks.core.title.Difficulty;

public class BuildGameState extends GameState {

	public BuildGameState() {
		super(new Difficulty());
	}
	
	@Override
	public boolean canSave() {
		return false;
	}

}
