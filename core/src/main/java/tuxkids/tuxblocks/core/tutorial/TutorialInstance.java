package tuxkids.tuxblocks.core.tutorial;

import playn.core.util.Clock;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

public interface TutorialInstance {

	/** 
	 * Called before any other method, when this tutorial is loaded by {@link Tutorial} 
	 * and set as the instance 
	 */
	void init(GameState state);
	void loadTextFile(String result);
	void paint(Clock clock);
	void update(int delta);
	void trigger(Trigger event);
	void trigger(Trigger event, Object extraInformation);
	void didLeaveScreen();
	void destroy();
	void refreshHighlights();
	void wasRepeated();
	String filename();
	boolean askPermission(Trigger event);

}
