package tuxkids.tuxblocks.core.tutorial;

import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

class Tutorial0 extends FSMTutorial {

	public Tutorial0(int themeColor) {
		super(themeColor);
	}

	@Override
	protected void addStates() {
		State one = addStartState("firstMessage");
		State two = addState("secondMessage");
		State three = addState("thirdMessage");
		
		one.addTransition(two, Trigger.TextBoxHidden);
		two.addTransition(three, Trigger.Build_Shown);
		three.addTransition(endState, Trigger.Title_Build);
		
		anyState.addTransition(two, Trigger.Title_Play);
	}


}
