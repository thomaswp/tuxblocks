package tuxkids.tuxblocks.core.tutorial;

import static tuxkids.tuxblocks.core.story.StoryGameStateKeys.HPBT;
import static tuxkids.tuxblocks.core.story.StoryGameStateKeys.TSRB;
import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.Defense_BadTowerPlacement;
import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.Defense_RoundOver;
import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.Defense_StartRound;
import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.Defense_TowerDropped;
import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.TextBoxHidden;
import pythagoras.i.Point;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.tutorial.gen.Tutorial0_Base;
import tuxkids.tuxblocks.core.utils.Debug;

public class Tutorial0 extends FSMTutorial implements Tutorial0_Base {

	private boolean hasPlacedBlockingTower = false;
	private boolean triedToSkipAhead = false;

	@Override
	public String filename() {
		return filename;
	}

	@Override
	protected void setUpStates() {
		FSMState nextWaveSoon = addStartState(id_nextWaveSoon);
		FSMState shoreUpDefenses = makeBasicState(id_shoreUpDefenses);
		FSMState firstTower = makeBasicState(id_dragFirstTower).addHighlightable(Tag.Defense_PeaShooter);
		final FSMState goodPlacement = makeBasicState(id_goodFirstPlacement);
		final FSMState okayPlacement = makeBasicState(id_okayFirstPlacement);
		final FSMState nowWeWait = makeBasicState(id_secondTowerPlacement);
		final FSMState nowPushButton = makeBasicState(id_nowPushButton).addHighlightable(Tag.Defense_StartRound);

		// non message state that starts the round
		final FSMState autoStartLevel = new FSMState() {
			@Override
			public FSMState notifyMessageShown() {
				gameState.level().startNextRound();
				return super.notifyMessageShown();
			}
		};

		joinMultiTextStates(nextWaveSoon, shoreUpDefenses, firstTower);

		firstTower.addTransition(new StateChooser() {

			@Override
			public FSMState chooseState(Object extraInformation) {
				if (extraInformation instanceof Point) {
					Point point = (Point) extraInformation;
					if (point.x == 5 && point.y == 2) {
						return goodPlacement;
					}
				}
				return okayPlacement;
			}
		}, Defense_TowerDropped);

		// goodPlacement.addTransition(nowWeWait, Defense_TowerDropped);
		// okayPlacement.addTransition(nowWeWait, Defense_TowerDropped);

		StateChooser chooseButtonOrAutostart = new StateChooser() {

			@Override
			public FSMState chooseState(Object extraInformation) {
				if (triedToSkipAhead) {
					return nowPushButton;
				}
				return nowWeWait;
			}
		};

		goodPlacement.addTransition(chooseButtonOrAutostart, Defense_TowerDropped);

		okayPlacement.addTransition(chooseButtonOrAutostart, Defense_TowerDropped);

		nowWeWait.addTransition(autoStartLevel, TextBoxHidden);
		nowPushButton.addTransition(endState, Defense_RoundOver);
		autoStartLevel.addTransition(endState, Defense_RoundOver);
		anyState.addTransition(endState, Defense_RoundOver);

		handleBadTowerTransition(nextWaveSoon);
		handleBadTowerTransition(shoreUpDefenses);
		handleBadTowerTransition(firstTower);
		handleBadTowerTransition(goodPlacement);
		handleBadTowerTransition(okayPlacement);

	}

	private void handleBadTowerTransition(FSMState originalState) {
		final FSMState error = makeBasicState(id_badTowerPlacement);
		originalState.addTransition(new StateChooser() {

			@Override
			public FSMState chooseState(Object extraInformation) {
				hasPlacedBlockingTower = true;
				return error;
			}
		}, Defense_BadTowerPlacement);
		error.registerEpsilonTransition(originalState);
	}

	@Override
	protected boolean handleTriggerPermissions(Trigger event) {
		int towerInventoryCount = TutorialUtils.towerCounts(gameState);
		if (event == Defense_StartRound && towerInventoryCount > 0) {
			if (towerInventoryCount == 2)
				showMessage(getLocalizedText(id_cant_skip_ahead2));
			else
				showMessage(getLocalizedText(id_cant_skip_ahead1));
			triedToSkipAhead = true;
			return false;
		}
		return true;
	}

	@Override
	protected void endOfTutorial() {
		Debug.write("End");
		super.endOfTutorial();
		gameState.setBoolean(HPBT, this.hasPlacedBlockingTower);
		gameState.setBoolean(TSRB, this.triedToSkipAhead);
		gameState.finishedLesson();
	}

}
