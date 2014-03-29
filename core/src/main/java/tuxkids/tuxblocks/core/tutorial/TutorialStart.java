package tuxkids.tuxblocks.core.tutorial;

import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

/** Start of both tutorials */
public class TutorialStart extends LinearTutorial {

	@Override
	public String filename() {
		return Constant.TUTORIAL_START_PATH;
	}

	@Override
	protected void addActions() {
		addAction(null);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Title_Play)
		.addHighlight(Tag.Title_Build);
		addSegue(new TutorialPlay(), Constant.TUTORIAL_PLAY_PATH, Trigger.Title_Play)
		.setSkip(Trigger.Title_Build);
		addSegue(new TutorialBuild(), Constant.TUTORIAL_BUILD_PATH, Trigger.Title_Build);
	}

}
