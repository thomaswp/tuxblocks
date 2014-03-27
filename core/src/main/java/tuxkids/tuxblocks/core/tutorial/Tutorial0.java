package tuxkids.tuxblocks.core.tutorial;


import static tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger.*;

class Tutorial0 extends FSMTutorial {

	public Tutorial0(int themeColor) {
		super(themeColor);
	}

	@Override
	protected void addStates() {
		State one = addStartState("id_nextWaveSoon");
		State two = addState("id_shoreUpDefenses");
		State three = addState("id_dragFirstTower");
		State four = addState("id_goodFirstPlacement");
		State five = addState("id_okayFirstPlacement");
		State six = addState("id_secondTowerPlacement");
		
		one.addTransition(two, TextBoxHidden);
		two.addTransition(three, TextBoxHidden);
		
		three.addTransition(five, Defense_TowerDropped);
		five.addTransition(six, Defense_TowerDropped);
		
		//anyState.addTransition(two, Title_Play);
	}


}
