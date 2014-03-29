package tuxkids.tuxblocks.core.tutorial;


import static tuxkids.tuxblocks.core.tutorial.Tutorial.Tag.*;
import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.*;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.story.StoryGameState;
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
		//6m5 means 6 minus 5 (6-5)
		final FSMState _6m5One = addState("id_equationSolvingScreen");
		
		FSMState letsGoSetupRobots = addState("id_letsGoSetupRobots").addHighlightable(Select_Return);
		
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
		
		FSMState _3x4SolvedFirst = solve3x4asFirstEquation(_3x4One);
		FSMState _6m5SolvedSecond = solve6m5asSecondEquation(_3x4SolvedFirst);

		FSMState _6m5SolvedFirst = solve6m5asFirstEquation(_6m5One);
		FSMState _3x4SolvedSecond = solve3x4asSecondEquation(_6m5SolvedFirst);
		
		_6m5SolvedSecond.addTransition(letsGoSetupRobots, Select_ScreenShown);
		_3x4SolvedSecond.addTransition(letsGoSetupRobots, Select_ScreenShown);
		letsGoSetupRobots.addTransition(endState, Defense_ScreenShown);
	}

	private FSMState solve3x4asFirstEquation(final FSMState _3x4One) {
		FSMState _3x4Two = addState("id_solveForX");
		FSMState _3x4Three = addState("id_3x4simplifyMultiplicationPrompt");
		FSMState _3x4Simplify1 = addState("id_3x4simplifyScreen1");
		FSMState _3x4Simplify2 = addState("id_simplifyScreen2");
		FSMState _3x4Simplify3 = addState("id_simplifyScreen3");
		FSMState _3x4Simplify4 = addState("id_simplifyScreen4");
		FSMState _3x4Simplify5 = addState("id_simplifyScreen5").addHighlightable(NumberSelect_Ok);
		FSMState _3x4Solved = addState("id_3x4solved").addHighlightable(Solve_Ok);
		
		FSMState _3x4dragged3One = addState("id_didNotClickGreyCircle");
		FSMState _3x4dragged3Two = addState("id_3x4dragged3");
		
		joinMultiTextStates(_3x4One, _3x4Two, _3x4Three);
		_3x4Three.addTransition(_3x4Simplify1, NumberSelect_Shown);
		_3x4Three.addTransition(_3x4dragged3One, Solve_BlockReleasedOnOther);
		
		joinMultiTextStates(_3x4Simplify1, _3x4Simplify2, _3x4Simplify3, _3x4Simplify4);
		_3x4Simplify4.addTransition(_3x4Simplify5, NumberSelect_NumberSelected);
		
		joinMultiTextStates(_3x4dragged3One, _3x4dragged3Two);
		_3x4dragged3Two.registerEpsilonTransition(_3x4Three);
		
		_3x4Simplify5.addTransition(_3x4Solved, Solve_Solved);
		return _3x4Solved;
	}

	private FSMState solve3x4asSecondEquation(FSMState _6m5SolvedFirst) {
		// TODO Auto-generated method stub
		return null;
	}

	private FSMState solve6m5asFirstEquation(FSMState _6m5One) {
		// TODO Auto-generated method stub
		return null;
	}

	private FSMState solve6m5asSecondEquation(FSMState _3x4SolvedFirst) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void endOfTutorial() {
		Debug.write("End");
		super.endOfTutorial();
		gameState.finishedLesson();
	}
	
	

}
