package tuxkids.tuxblocks.core.tutorial;

import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

public class Tutorial0 extends FSMTutorial {

	public Tutorial0(int themeColor) {
		super(themeColor);
	}

	@Override
	protected void addStates() {
		State one = addStartState("firstMessage");
		State two = addState("secondMessage");
		State three = addState("thirdMessage");
		
		one.addTransition(Trigger.TextBoxHidden, two);
		two.addTransition(Trigger.Build_Shown, three);
		three.addTransition(Trigger.Title_Build, endState);
		
		anyState.addTransition(Trigger.Title_Play, two);
	}


}
