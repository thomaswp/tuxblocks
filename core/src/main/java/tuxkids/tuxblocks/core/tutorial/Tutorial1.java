package tuxkids.tuxblocks.core.tutorial;


import static tuxkids.tuxblocks.core.tutorial.Tutorial.Tag.*;
import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.*;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationBlockIndex;
import tuxkids.tuxblocks.core.story.StoryGameState;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.tutorial.gen.Tutorial1_1_Base;
import tuxkids.tuxblocks.core.utils.Debug;


public class Tutorial1 extends FSMTutorial implements Tutorial1_1_Base {

	protected boolean hasCoachedOnExtraDragging;

	public Tutorial1(StoryGameState storyGameState) {
		super(storyGameState);
		
		hasCoachedOnExtraDragging = storyGameState.getBoolean(StoryGameState.HCOED);
	}

	@Override
	public String filename() {
		return filename;
	}

	@Override
	protected void setUpStates() {
		FSMState start = addStartState(id_salvageAndBuild);
		final FSMState _3x4First = makeBasicState(id_equationSolvingScreen);
		final FSMState _6m5First = makeBasicState(id_equationSolvingScreen);	//6m5 means 6 minus 5 (6-5)
		FSMState letsGoSetupRobots = makeBasicState(id_letsGoSetupRobots).addHighlightable(Select_Return);
		
		this.promptSelectionOfFirstEquation(start, _3x4First, _6m5First);
		
		FSMState _3x4SolvedFirst = this.solve3x4asFirstEquation(_3x4First);
		FSMState _6m5SolvedSecond = this.solve6m5asSecondEquation(_3x4SolvedFirst);

		FSMState _6m5SolvedFirst = this.solve6m5asFirstEquation(_6m5First);
		FSMState _3x4SolvedSecond = this.solve3x4asSecondEquation(_6m5SolvedFirst);
		
		_6m5SolvedSecond.addTransition(letsGoSetupRobots, Select_ScreenShown);
		_3x4SolvedSecond.addTransition(letsGoSetupRobots, Select_ScreenShown);
		
		letsGoSetupRobots.addTransition(endState, Defense_ScreenShown);
	}

	private void promptSelectionOfFirstEquation(FSMState one, final FSMState _3x4First, final FSMState _6m5First) {
		FSMState two = makeBasicState(id_goToEquationScreen).addHighlightable(Defense_EquationSelectScreen);
		FSMState three = makeBasicState(id_solveTheClues);
		final FSMState four = makeBasicState(id_selectFirstEquation).addHighlightable(Select_FirstEquation).
		addHighlightable(Select_SecondEquation);
		
		one.addTransition(two, TextBoxHidden);
		two.addTransition(three, Select_ScreenShown);
		three.addTransition(four, TextBoxHidden);
		
		four.addTransition(new StateChooser() {
			@Override
			public FSMState chooseState(Object extraInformation) {
				if (extraInformation instanceof Equation) {
					String eqName = ((Equation) extraInformation).name();
					if ("3x4".equals(eqName)) {
						return _3x4First;
					} else if ("6-5".equals(eqName)) {
						return _6m5First;
					} 
					Debug.write("Unknown equation named "+eqName+" "+extraInformation);
					return endState;
				}
				Debug.write("No extra info for state 4");
				return four;
			}
		}, Solve_ScreenShown);
	}

	private FSMState solve3x4asFirstEquation(final FSMState _3x4One) {
		FSMState _3x4Two = makeBasicState(id_solveForX);
		final FSMState _3x4Three = makeBasicState(id_3x4simplifyMultiplicationPrompt);
		FSMState _3x4Simplify1 = makeBasicState(id_3x4simplifyScreen1);
		FSMState _3x4Simplify2 = makeBasicState(id_simplifyScreen2);
		FSMState _3x4Simplify3 = makeBasicState(id_simplifyScreen3);
		FSMState _3x4Simplify4 = makeBasicState(id_simplifyScreen4);
		FSMState _3x4Simplify5 = makeBasicState(id_simplifyScreen5).addHighlightable(NumberSelect_Ok);
		FSMState _3x4PrematureSimplify = makeBasicState(id_prematureSimplify);	
		FSMState _3x4Solved = makeBasicState(id_3x4solved).addHighlightable(Solve_Ok);

		joinMultiTextStates(_3x4One, _3x4Two, _3x4Three);
		solve3x4AsFirst_handleDrag(_3x4Three);
		
		_3x4Three.addTransition(_3x4Simplify1, NumberSelect_Shown);
		joinMultiTextStates(_3x4Simplify1, _3x4Simplify2, _3x4Simplify3, _3x4Simplify4);
		handlePrematureSimplification(_3x4PrematureSimplify, _3x4Simplify5, _3x4Simplify1, _3x4Simplify2, _3x4Simplify3);
		_3x4Simplify4.addTransition(_3x4Simplify5, NumberSelect_NumberSelected);
		
		_3x4PrematureSimplify.addTransition(_3x4Solved, Solve_Solved);
		_3x4Simplify5.addTransition(_3x4Solved, Solve_Solved);
		return _3x4Solved;
	}

	private void solve3x4AsFirst_handleDrag(final FSMState startingNode) {
		final FSMState _3x4draggedOne = addLocalizedTextToState(id_didNotClickGreyCircle, new FSMState(){
			@Override
			public FSMState notifyMessageShown() {
				hasCoachedOnExtraDragging  = true;
				return super.notifyMessageShown();
			}
		});
		FSMState _3x4dragged3Two = makeBasicState(id_3x4dragged3);
		final FSMState _3x4FixedBadDrag = makeBasicState(id_3x4fixDrag);
			
		startingNode.addTransition(new StateChooser() {	
			@Override
			public FSMState chooseState(Object extraInformation) {
				if (extraInformation instanceof EquationBlockIndex) {
					if (((EquationBlockIndex) extraInformation).expressionIndex == 0) 
						return _3x4draggedOne;
					return _3x4FixedBadDrag;
				}
				Debug.write("no drag information?");
				return _3x4FixedBadDrag;
			}
		}, Solve_BlockReleasedOnOther);		
		joinMultiTextStates(_3x4draggedOne, _3x4dragged3Two);
		_3x4dragged3Two.registerEpsilonTransition(startingNode);
		_3x4FixedBadDrag.registerEpsilonTransition(startingNode);
	}

	private FSMState solve6m5asSecondEquation(FSMState _3x4SolvedFirst) {
		FSMState _6m5One = makeBasicState(id_unlockOther);
		FSMState _6m5Two = makeBasicState(id_6m5secondSubtract);
		FSMState _6m5Simplify = makeBasicState(id_6m5secondSimplify);
		FSMState _6m5Solved = makeBasicState(id_secondSolved).addHighlightable(Solve_Ok);

		_3x4SolvedFirst.addTransition(_6m5One, Select_ScreenShown);
		_6m5One.addTransition(_6m5Two, Solve_ScreenShown);
		
		solve6m5AsSecond_handleDrag(_6m5Two);
		
		_6m5Two.addTransition(_6m5Simplify, NumberSelect_Shown);
		_6m5Simplify.addTransition(_6m5Solved, Solve_Solved);
		
		return _6m5Solved;
	}

	private void solve6m5AsSecond_handleDrag(FSMState _6m5Two) {
		final FSMState _6m5Dragged = makeBasicState(id_didNotClickGreyCircle);
		final FSMState _6m5DraggedTwo = makeBasicState(id_6m5dragged5);
		final FSMState _6m5DraggedReminder = makeBasicState(id_6m5dragged5reminder);
		final FSMState _6m5FixedBadDrag = makeBasicState(id_6m5fixDrag);
		
		_6m5Two.addTransition(new StateChooser() {
			
			@Override
			public FSMState chooseState(Object extraInformation) {
				if (extraInformation instanceof EquationBlockIndex) {
					if (((EquationBlockIndex) extraInformation).expressionIndex == 0) {
						if (hasCoachedOnExtraDragging) {
							return _6m5DraggedReminder;
						} 
						return _6m5Dragged;
					}
					return _6m5FixedBadDrag;
				}
				Debug.write("no drag information?");
				return _6m5FixedBadDrag;
			}
		}, Solve_BlockReleasedOnOther);
		
		joinMultiTextStates(_6m5Dragged, _6m5DraggedTwo);
		
		_6m5DraggedReminder.registerEpsilonTransition(_6m5Two);
		_6m5DraggedTwo.registerEpsilonTransition(_6m5Two);
		_6m5FixedBadDrag.registerEpsilonTransition(_6m5Two);
	}

	private FSMState solve6m5asFirstEquation(FSMState _6m5One) {
		FSMState _6m5Two = makeBasicState(id_solveForX);
		final FSMState _6m5Three = makeBasicState(id_6m5simplifyMultiplicationPrompt);
		FSMState _6m5Simplify1 = makeBasicState(id_6m5simplifyScreen1);
		FSMState _6m5Simplify2 = makeBasicState(id_simplifyScreen2);
		FSMState _6m5Simplify3 = makeBasicState(id_simplifyScreen3);
		FSMState _6m5Simplify4 = makeBasicState(id_simplifyScreen4);
		FSMState _6m5Simplify5 = makeBasicState(id_simplifyScreen5).addHighlightable(NumberSelect_Ok);
		FSMState _6m5PrematureSimplify = makeBasicState(id_prematureSimplify);	
		FSMState _6m5Solved = makeBasicState(id_6m5solved).addHighlightable(Solve_Ok);

		joinMultiTextStates(_6m5One, _6m5Two, _6m5Three);
		solve6m5AsFirst_handleDrag(_6m5Three);
		
		_6m5Three.addTransition(_6m5Simplify1, NumberSelect_Shown);
		joinMultiTextStates(_6m5Simplify1, _6m5Simplify2, _6m5Simplify3, _6m5Simplify4);
		handlePrematureSimplification(_6m5PrematureSimplify, _6m5Simplify5, _6m5Simplify1, _6m5Simplify2, _6m5Simplify3);
		_6m5Simplify4.addTransition(_6m5Simplify5, NumberSelect_NumberSelected);
		
		_6m5PrematureSimplify.addTransition(_6m5Solved, Solve_Solved);
		_6m5Simplify5.addTransition(_6m5Solved, Solve_Solved);
		return _6m5Solved;
	}

	private void solve6m5AsFirst_handleDrag(FSMState startingNode) {
		final FSMState _6m5draggedOne = addLocalizedTextToState(id_didNotClickGreyCircle, new FSMState(){
			@Override
			public FSMState notifyMessageShown() {
				hasCoachedOnExtraDragging  = true;
				return super.notifyMessageShown();
			}
		});
		FSMState _6m5dragged3Two = makeBasicState(id_6m5dragged5);
		final FSMState _6m5FixedBadDrag = makeBasicState(id_6m5fixDrag);
			
		startingNode.addTransition(new StateChooser() {	
			@Override
			public FSMState chooseState(Object extraInformation) {
				if (extraInformation instanceof EquationBlockIndex) {
					if (((EquationBlockIndex) extraInformation).expressionIndex == 0) 
						return _6m5draggedOne;
					return _6m5FixedBadDrag;
				}
				Debug.write("no drag information?");
				return _6m5FixedBadDrag;
			}
		}, Solve_BlockReleasedOnOther);		
		joinMultiTextStates(_6m5draggedOne, _6m5dragged3Two);
		_6m5dragged3Two.registerEpsilonTransition(startingNode);
		_6m5FixedBadDrag.registerEpsilonTransition(startingNode);
	}

	private FSMState solve3x4asSecondEquation(FSMState _6m5SolvedFirst) {
		FSMState _3x4One = makeBasicState(id_unlockOther);
		FSMState _3x4Two = makeBasicState(id_3x4secondMultiply);
		FSMState _3x4Simplify = makeBasicState(id_3x4secondSimplify);
		FSMState _3x4Solved = makeBasicState(id_secondSolved).addHighlightable(Solve_Ok);

		_6m5SolvedFirst.addTransition(_3x4One, Select_ScreenShown);
		_3x4One.addTransition(_3x4Two, Solve_ScreenShown);
		
		solve3x4AsSecond_handleDrag(_3x4Two);
		
		_3x4Two.addTransition(_3x4Simplify, NumberSelect_Shown);
		_3x4Simplify.addTransition(_3x4Solved, Solve_Solved);
		
		return _3x4Solved;
	}

	private void solve3x4AsSecond_handleDrag(FSMState startingNode) {
		final FSMState _3x4Dragged = makeBasicState(id_didNotClickGreyCircle);
		final FSMState _3x4DraggedTwo = makeBasicState(id_3x4dragged3);
		final FSMState _3x4DraggedReminder = makeBasicState(id_3x4dragged3reminder);
		final FSMState _3x4FixedBadDrag = makeBasicState(id_3x4fixDrag);
		
		startingNode.addTransition(new StateChooser() {
			
			@Override
			public FSMState chooseState(Object extraInformation) {
				if (extraInformation instanceof EquationBlockIndex) {
					if (((EquationBlockIndex) extraInformation).expressionIndex == 0) {
						if (hasCoachedOnExtraDragging) {
							return _3x4DraggedReminder;
						} 
						return _3x4Dragged;
					}
					return _3x4FixedBadDrag;
				}
				Debug.write("no drag information?");
				return _3x4FixedBadDrag;
			}
		}, Solve_BlockReleasedOnOther);
		
		joinMultiTextStates(_3x4Dragged, _3x4DraggedTwo);
		
		_3x4DraggedReminder.registerEpsilonTransition(startingNode);
		_3x4DraggedTwo.registerEpsilonTransition(startingNode);
		_3x4FixedBadDrag.registerEpsilonTransition(startingNode);
	}

	private void handlePrematureSimplification(FSMState simplifyNode, FSMState nodeToSkipTo, FSMState... skippableNodes) {
		for(FSMState node:skippableNodes) {
			node.addTransition(simplifyNode, NumberSelect_NumberSelected);
		}
		joinMultiTextStates(simplifyNode, nodeToSkipTo);	
	}
	
	
	@Override
	protected boolean handleTriggerPermissions(Trigger event) {
		if (event == Solve_GoBack) {
			showMessage(getLocalizedText(id_cant_go_back));
			return false;
		}
		return true;
	}
	
	@Override
	protected void endOfTutorial() {
		Debug.write("End");
		super.endOfTutorial();
		gameState.setBoolean(StoryGameState.HCOED, hasCoachedOnExtraDragging);
		gameState.finishedLesson();
	}

}
