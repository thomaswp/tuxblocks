package tuxkids.tuxblocks.core.tutorial;

import playn.core.util.Clock;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

public interface TutorialInstance {

	void loadTextFile(String result);
	void paint(Clock clock);
	void update(int delta);
	void trigger(Trigger event);
	void didLeaveScreen();
	void destroy();
	void refreshHighlights();
	void wasRepeated();

}
