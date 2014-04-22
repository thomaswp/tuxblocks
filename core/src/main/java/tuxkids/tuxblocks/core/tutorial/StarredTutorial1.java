package tuxkids.tuxblocks.core.tutorial;

import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.story.StoryGameState;
import tuxkids.tuxblocks.core.tutorial.FSMTutorial.FSMState;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.tutorial.gen.StarredTutorial1_Base;

public class StarredTutorial1 extends AbstractStarredTutorial implements StarredTutorial1_Base {

	private boolean wasTrainedEarlierOnDrag = false;
	
	@Override
	public void init(GameState gameState) {
		super.init(gameState);
		
		wasTrainedEarlierOnDrag = this.gameState.getBoolean(StoryGameState.HCOED);
	}
	
	@Override
	public String filename() {
		return filename;
	}

	@Override
	protected void setUpStates() {
		FSMState introState = addStartState(id_intro);
		final FSMState letsDrag = makeBasicState(id_letsDrag);
		final FSMState letsDragTrained = makeBasicState(id_letsDrag_alreadyTrained);
		FSMState dragged8 = makeBasicState(id_dragged8);
		FSMState takeItFromHere = makeBasicState(id_takeItFromHere);
		FSMState dragged5NotTrained = makeBasicState(id_dragged5_notTrainedEarlier);
		FSMState dragged5Trained = makeBasicState(id_dragged5_trainedEarlier);
		FSMState undoDrag = makeBasicState(id_undoDrag);
		FSMState goodCorrectionDrag = makeBasicState(id_goodCorrectionDrag);
		FSMState badCorrectionDrag = makeBasicState(id_badCorrectionDrag);
		
		introState.addTransition(new StateChooser() {
			
			@Override
			public FSMState chooseState(Object extraInformation) {
				if (wasTrainedEarlierOnDrag) {
					return letsDragTrained;
				}
				return letsDrag;
			}
		}, Trigger.TextBoxHidden);
		
		letsDrag.a
	}

	@Override
	public Equation createEquation() {
		//x+8 = 5
		return new Equation.Builder().addLeft(new VariableBlock("x").plus(8))
				.addRight(5).createEquation();
	}

}
