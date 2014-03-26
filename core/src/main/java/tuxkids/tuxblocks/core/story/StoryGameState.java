package tuxkids.tuxblocks.core.story;

import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.title.Difficulty;

public class StoryGameState extends GameState {

	public StoryGameState() {
		super(new Difficulty(0, 1, Difficulty.ROUND_TIME_INFINITE));
		
	}
	

}
