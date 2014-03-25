package tuxkids.tuxblocks.core.tutorial;

import java.util.ArrayList;

import playn.core.Json;
import playn.core.PlayN;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

public abstract class FSMTutorial implements TutorialInstance {

	TutorialLayer layer;
	
	public FSMTutorial(int themeColor) {
		layer = new TutorialLayer(themeColor);
	}
	
	@Override
	public void loadTextFile(String result) {
		Json.Object obj = PlayN.json().parse(result);
		
	}

	@Override
	public void paint(Clock clock) {
		layer.paint(clock);
	}

	@Override
	public void update(int delta) {
		
	}

	@Override
	public void trigger(Trigger event) {
		
	}

	@Override
	public void destroy() {
		layer.destroy();
	}

	@Override
	public void refreshHighlights() {
		Tutorial.refreshHighlights(new ArrayList<Tutorial.Tag>());
	}

	@Override
	public void didLeaveScreen() {
		
	}
	
	@Override
	public void wasRepeated() {
		
	}

}
