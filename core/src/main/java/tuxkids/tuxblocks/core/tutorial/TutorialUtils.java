package tuxkids.tuxblocks.core.tutorial;

import tuxkids.tuxblocks.core.story.StoryGameState;

public class TutorialUtils {

	public static int towerCounts(StoryGameState gameState) {
		int sum = 0;
		int[] counts = gameState.towerCounts();
		for(int i = 0;i<counts.length;i++) {
			sum += counts[i];
		}
		return sum;
	}

}
