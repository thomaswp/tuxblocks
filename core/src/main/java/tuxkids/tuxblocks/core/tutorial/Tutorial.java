package tuxkids.tuxblocks.core.tutorial;

import java.util.ArrayList;
import java.util.List;

import playn.core.util.Callback;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.PlayNObject;

public abstract class Tutorial extends PlayNObject {

	public enum Trigger {
		TextBoxHidden, 
		
		Difficulty_Shown,
		
		Defense_Shown, 
		Defense_TowerDropped, 
		Defense_GridZoom, 
		Defense_TowerSelected, 
		Defense_TowerUpgraded, 
		Defense_StartRound, 
		Defense_RoundOver, 
		
		Select_Shown,
	}
	
	private static Tutorial instance;
	
	private final ArrayList<String> sections = new ArrayList<String>();
	
	protected final List<Action> actions = new ArrayList<Action>();
	protected final TutorialLayer tutorialLayer;
	protected final int themeColor, secondaryColor;
	
	protected int actionIndex = -1;
	protected boolean canReshow;
	
	protected abstract void addActions();
	
	protected Action action() {
		return actions.get(actionIndex);
	}
	
	protected Action nextAction() {
		return actions.get(++actionIndex);
	}
	
	protected Action peek() {
		return actions.get(actionIndex + 1);
	}
	
	protected boolean hasNext() {
		return actionIndex < actions.size() - 1;
	}
	
	public static void start(final int themeColor, final int secondaryColor) {
		if (instance != null) {
			return;
		}
		assets().getText(Constant.TUTORIAL_PATH, new Callback<String>() {
			@Override
			public void onSuccess(String result) {
				new Tutorial1(themeColor, secondaryColor, result);
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
	}
	
	public Tutorial(int themeColor, int secondaryColor, String path) {
		instance = this;
		this.themeColor = themeColor;
		this.secondaryColor = secondaryColor;
		for (String line : path.split("\n")) {
			line = line.replace("\n", "").replace("\r", "");
			if (!line.isEmpty()) {
				line = line.replace("<click>", Constant.click());
				line = line.replace("<mouse>", Constant.mouse());
				sections.add(line);
			}
		}
		
		addActions();
		
		tutorialLayer = new TutorialLayer(themeColor);
		triggerInstance(null);
	}
	
	protected void updateInstance(int delta) {
		
	}
	
	protected void paintInstance(Clock clock) {
		tutorialLayer.paint(clock);
	}

	private void triggerInstance(Trigger event) {
		if (hasNext() && peek().trigger == event) {
			Action action = nextAction();
			tutorialLayer.showAction(action);
			canReshow = false;
		} else if (canReshow && action().trigger == event) {
			tutorialLayer.showAction(action());
		}
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

	public static void trigger(Trigger event) {
		if (instance != null) {
			instance.triggerInstance(event);
		}
	}
	
	public static void cleaIndicators() {
		if (instance != null) {
			instance.tutorialLayer.clearIndicators();
			instance.canReshow = true;
		}
	}
	
	protected Action addAction(Trigger trigger) {
		Action action = new Action();
		action.message = sections.remove(0);
		action.trigger = trigger;
		actions.add(action);
		return action;
	}
	
	protected enum Align {
		Center,
		TopLeft,
		TopRight,
		BottomLeft,
		ButtonRight
	}
	
	protected class Action { 
		public Trigger trigger;
		public String message;
		public List<Indicator> indicators = new ArrayList<Tutorial.Indicator>();
		
		public Action addIndicatorR(String name, int color, float x, float y, float width, float height) {
			return addIndicatorR(name, color, x, y, width, height, Align.Center);
		}
		
		public Action addIndicatorR(String name, int color, float x, float y, float width, float height, Align align) {
			Indicator indicator = new Indicator();
			indicator.name = name;
			indicator.x = x * graphics().width();
			indicator.y = y * graphics().height();
			indicator.width = width == -1 ? -1 : width * graphics().width();
			indicator.height = height == -1 ? -1 : height * graphics().height();
			indicator.align = align;
			indicator.color = color;
			indicators.add(indicator);
			return this;
		}
		
		public Action setAlign(Align align) {
			indicators.get(indicators.size() - 1).align = align;
			return this;
		}
	}
	
	protected class Indicator {
		public String name;
		public float x, y, width, height;
		public Align align = Align.Center;
		public int color;
	}
}
