package tuxkids.tuxblocks.core.tutorial;


import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.*;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tuxkids.tuxblocks.core.tutorial.FSMTutorial.State;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.Debug;

class Tutorial0 extends FSMTutorial {

	public Tutorial0(int themeColor) {
		super(themeColor);
	}

	@Override
	protected void addStates() {
		State one = addStartState("id_nextWaveSoon");
		State two = addState("id_shoreUpDefenses");
		State three = addState("id_dragFirstTower");
		final State four = addState("id_goodFirstPlacement");
		final State five = addState("id_okayFirstPlacement");
		State six = addState("id_secondTowerPlacement");
		
		one.addTransition(two, TextBoxHidden);
		two.addTransition(three, TextBoxHidden);
		
		three.addTransition(new StateChooser() {
			
			@Override
			public State chooseState(Object extraInformation) {
				if (extraInformation instanceof Point) {
					
					return four;
				}
				return five;
			}
		}, Defense_TowerDropped);
		
		four.addTransition(six, Defense_TowerDropped);
		five.addTransition(six, Defense_TowerDropped);
		six.addTransition(endState, Defense_RoundOver);
		
		handleBadTowerTransition(one);
		handleBadTowerTransition(two);
		handleBadTowerTransition(three);
		handleBadTowerTransition(four);
		handleBadTowerTransition(five);
	}

	private void handleBadTowerTransition(final State originalState) {
		State error = addState("id_badTowerPlacement");
		originalState.addTransition(error, Defense_BadTowerPlacement);
		error.registerEpsilonTransition(originalState);
		
	}

	
	@Override
	protected void endOfTutorial() {
		Debug.write("End");
		super.endOfTutorial();
	}

}
