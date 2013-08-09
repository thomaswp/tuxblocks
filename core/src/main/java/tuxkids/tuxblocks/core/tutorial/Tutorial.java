package tuxkids.tuxblocks.core.tutorial;

import java.util.ArrayList;

import playn.core.util.Callback;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.PlayNObject;

public class Tutorial extends PlayNObject {
	
	private static Tutorial instance;
	
	private final ArrayList<String> sections = new ArrayList<String>();
	private final TutorialLayer tutorialLayer;
	
	public static void start() {
		assets().getText(Constant.TUTORIAL_PATH, new Callback<String>() {
			@Override
			public void onSuccess(String result) {
				new Tutorial(result);
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
	}
	
	public Tutorial(String path) {
		instance = this;
		for (String line : path.split("\n")) {
			line = line.replace("\n", "").replace("\r", "");
			if (!line.isEmpty()) {
				sections.add(line);
			}
		}
		
		tutorialLayer = new TutorialLayer();
		tutorialLayer.showMessage(sections.get(0));
	}
	
	protected void updateInstance(int delta) {
		
	}
	
	protected void paintInstance(Clock clock) {
		tutorialLayer.paint(clock);
	}
	
	public static void paint(Clock clock) {
		if (instance != null) {
			instance.paintInstance(clock);
		}
	}

	public static void update(int delta) {
		if (instance != null) {
			instance.updateInstance(delta);
		}
	}
}
