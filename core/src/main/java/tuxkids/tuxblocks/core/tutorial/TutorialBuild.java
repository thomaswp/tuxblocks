package tuxkids.tuxblocks.core.tutorial;

import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

/** Tutorial for Build mode */
class TutorialBuild extends LinearTutorial {
	
	@Override
	public String filename() {
		return Constant.TUTORIAL_BUILD_PATH;
	}

	@Override
	protected void addActions() {
		addAction(Trigger.Build_Shown);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Build_LeftPanel);
		addAction(Trigger.Solve_BlockReleasedOnBlank);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Build_NumberUp)
		.addHighlight(Tag.Build_NumberDown);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Build_NumberSelect);
		addAction(Trigger.Number_Shown);
		addAction(Trigger.Number_NumberSelected)
		.addHighlight(Tag.Number_Ok);
		addAction(Trigger.Build_Shown)
		.addHighlight(Tag.Build_Ok);
		addAction(Trigger.Solve_Shown)
		.addHighlight(Tag.Solve_Ok)
		.dontRepeat();
		addAction(Trigger.Build_Shown);
	}
}
