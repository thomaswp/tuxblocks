package tuxkids.tuxblocks.core.tutorial;

import static tuxkids.tuxblocks.core.story.StoryGameStateKeys.HESP;
import tuxkids.tuxblocks.core.story.StoryGameState;
import tuxkids.tuxblocks.core.tutorial.gen.ExplainingStarred_Base;
import tuxkids.tuxblocks.core.utils.Debug;

public class Tutorial2ExplainingStarred extends FSMTutorial implements ExplainingStarred_Base{

	public Tutorial2ExplainingStarred(StoryGameState storyGameState) {
		super(storyGameState);

	}

	@Override
	public String filename() {
		return filename;
	}

	@Override
	protected void setUpStates() {
		FSMState noticeStars = addStartState(id_noticeStars);
		FSMState theStarsMean = makeBasicState(id_theStarsMean);
		FSMState whenReady = makeBasicState(id_whenReady);
				
		joinMultiTextStates(noticeStars, theStarsMean, whenReady);
	}
	
	@Override
	protected void endOfTutorial() {
		Debug.write("End");
		super.endOfTutorial();
		gameState.setBoolean(HESP, true);
		gameState.finishedLesson();
	}

}
