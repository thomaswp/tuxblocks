package tuxkids.tuxblocks.core.tutorial;

import static tuxkids.tuxblocks.core.story.StoryGameStateKeys.HESP;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.tutorial.gen.ExplainingStarred_Base;
import tuxkids.tuxblocks.core.utils.Debug;

public class Tutorial2ExplainingStarred extends FSMTutorial implements ExplainingStarred_Base{

	@Override
	public String filename() {
		return filename;
	}

	@Override
	protected void setUpStates() {
		FSMState waitForSelect = addStartState(new FSMState());
	
		FSMState noticeStars = makeBasicState(id_noticeStars).addHighlightable(Tag.StarredEquation);
		
		FSMState theStarsMean = makeBasicState(id_theStarsMean);
		FSMState whenReady = makeBasicState(id_whenReady);
		
		waitForSelect.addTransition(noticeStars, Trigger.Select_ScreenShown);
				
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
