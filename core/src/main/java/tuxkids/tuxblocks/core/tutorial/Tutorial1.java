package tuxkids.tuxblocks.core.tutorial;


import static tuxkids.tuxblocks.core.tutorial.Tutorial.Tag.*;
import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.*;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.story.StoryGameState;
import tuxkids.tuxblocks.core.tutorial.FSMTutorial.FSMState;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.utils.Debug;


public class Tutorial1 extends FSMTutorial {

	public Tutorial1(StoryGameState storyGameState) {
		super(storyGameState);
	}

	@Override
	protected void setUpStates() {
		FSMState one = addStartState("id_salvageAndBuild");
		FSMState two = addState("id_goToEquationScreen").addHighlightable(Defense_EquationSelectScreen);
		FSMState three = addState("id_solveTheClues");
		final FSMState four = addState("id_selectFirstEquation").addHighlightable(Select_FirstEquation).
				addHighlightable(Select_SecondEquation);
		
		final FSMState _3x4One = addState("id_equationSolvingScreen");
		FSMState _3x4Two = addState("id_solveForX");
		FSMState _3x4Three = addState("id_3x4simplifyMultiplicationPrompt");
		FSMState _3x4Simplify1 = addState("id_3x4simplifyScreen1");
		FSMState _3x4Simplify2 = addState("id_simplifyScreen2");
		FSMState _3x4Simplify3 = addState("id_simplifyScreen3");
		FSMState _3x4Simplify4 = addState("id_simplifyScreen4");
		FSMState _3x4Simplify5 = addState("id_simplifyScreen5").addHighlightable(Tag.Number_Ok);;
		
		//6m5 means 6 minus 5 (6-5)
		final FSMState _6m5One = addState("id_equationSolvingScreen");
		
		one.addTransition(two, TextBoxHidden);
		two.addTransition(three, Select_ScreenShown);
		three.addTransition(four, TextBoxHidden);
		
		four.addTransition(new StateChooser() {
			@Override
			public FSMState chooseState(Object extraInformation) {
				if (extraInformation instanceof Equation) {
					String eqName = ((Equation) extraInformation).name();
					if ("3x4".equals(eqName)) {
						return _3x4One;
					} else if ("6-5".equals(eqName)) {
						return _6m5One;
					} 
					Debug.write("Unknown equation named "+eqName+" "+extraInformation);
					return endState;
				}
				Debug.write("No extra info for state 4");
				return four;
			}
		}, Solve_ScreenShown);
		
		joinMultiTextStates(_3x4One, _3x4Two, _3x4Three);
		_3x4Three.addTransition(_3x4Simplify1, NumberSelect_Shown);
		
		joinMultiTextStates(_3x4Simplify1, _3x4Simplify2, _3x4Simplify3, _3x4Simplify4);
		_3x4Simplify4.addTransition(_3x4Simplify5, NumberSelect_NumberSelected);
		
		
	}
	
	@Override
	protected void endOfTutorial() {
		Debug.write("End");
		super.endOfTutorial();
		gameState.finishedLesson();
	}
	
	

}
