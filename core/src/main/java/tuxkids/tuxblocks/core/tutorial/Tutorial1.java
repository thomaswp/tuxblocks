package tuxkids.tuxblocks.core.tutorial;


import static tuxkids.tuxblocks.core.tutorial.Tutorial.Tag.Defense_EquationSelectScreen;
import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.*;
import tuxkids.tuxblocks.core.story.StoryGameState;
import tuxkids.tuxblocks.core.tutorial.gen.Tutorial1_1_Base;
import tuxkids.tuxblocks.core.utils.Debug;


public class Tutorial1 extends FSMTutorial implements Tutorial1_1_Base {

	public Tutorial1(StoryGameState storyGameState) {
		super(storyGameState);
	}

	@Override
	public String filename() {
		return filename;
	}

	@Override
	protected void setUpStates() {
		FSMState one = addStartState(id_salvageAndBuild);
		FSMState two = addState(id_goToEquationScreen).addHighlightable(Defense_EquationSelectScreen);
		FSMState three = addState(id_solveTheClues);
		FSMState four = addState(id_selectFirstEquation);
		
		one.addTransition(two, TextBoxHidden);
		two.addTransition(three, Select_Shown);
		three.addTransition(four, TextBoxHidden);
		
		four.registerElseTransition(endState);
		
	}
	
	@Override
	protected void endOfTutorial() {
		Debug.write("End");
		super.endOfTutorial();
		gameState.finishedLesson();
	}

}
