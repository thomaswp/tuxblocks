package tuxkids.tuxblocks.core.tutorial;

import java.util.ArrayList;
import java.util.List;

import playn.core.Color;
import playn.core.util.Callback;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.PlayNObject;

public abstract class Tutorial extends PlayNObject {

	public final static int HIGHLIGHT_COLOR_1 = Color.rgb(0xff, 0xaa, 0x44);
	public final static int HIGHLIGHT_COLOR_2 = Color.rgb(0xff, 0x44, 0x44);
	public final static int HIGHLIGHT_CYCLE = 1500;
	
	public enum Trigger {
		Title_Play, 
		Title_Build, 
		
		TextBoxHidden, 
		TextBoxFullyHidden,
		
		Difficulty_Shown,
		
		Defense_Shown, 
		Defense_TowerDropped, 
		Defense_GridZoom, 
		Defense_TowerSelected, 
		Defense_TowerUpgraded, 
		Defense_StartRound, 
		Defense_RoundOver, 
		
		Select_Shown, 
		
		Solve_Shown, 
		Solve_BlockReleased, 
		Solve_BlockReleasedOnOther, 
		Solve_SimplifiedSuccess, 
		Solve_Solved,
		Solve_BlockReleasedOnBlank, 
		Solve_BlockWithModifiersReleasedOnBlank, 
		Solve_BaseBlockReleasedOnOther, 
		Solve_VerticalModifierDoubleClicked, 
		Solve_Simplified, 
		Solve_VariablesStartedCombine, 
		
		Number_Shown, 
		Number_NumberSelected, 
		Number_Scratch, 
		
		Build_Shown, 
	}
	
	public enum Tag {
		Title_Play,
		Title_Build,
		
		Difficulty_Start,
		
		Defense_Towers, 
		Defense_Grid, 
		Defense_UpgradeTower,
		Defense_DeleteTower,
		Defense_StartRound, 
		Defense_MoreTowers,
		Defense_PeaShooter,
		
		Menu_Lives,
		Menu_Countdown,
		Menu_Upgrades, 
		
		Select_Return, 
		Select_FirstButton,
		Select_SecondButton,
		
		Solve_Ok, 
		Solve_Reset,
		
		Number_Ok, 
		Number_Scratch, 
		Number_Clear, 
		
		Build_LeftPanel, 
		Build_NumberSelect, 
		Build_Ok, 
		Build_NumberDown, 
		Build_NumberUp, 
	}
	
	private static Tutorial instance;
	private final static List<Highlightable> highlightables = new ArrayList<Highlightable>();
	
	private final List<String> sections = new ArrayList<String>();
	
	protected final List<Action> actions = new ArrayList<Action>();
	protected final int themeColor, secondaryColor;

	protected TutorialLayer tutorialLayer;
	protected int actionIndex = -1;
	protected boolean canReshow;
	
	protected abstract void addActions();

	public static boolean running() {
		return instance != null;
	}
	
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
		start(new TutorialStart(themeColor, secondaryColor), Constant.TUTORIAL_START_PATH);
	}
	
	private static void start(final Tutorial tutorial, final String path) {
		instance = tutorial;
		assets().getText(path, new Callback<String>() {
			@Override
			public void onSuccess(String result) {
				tutorial.setText(result);
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
	}
	
	public Tutorial(int themeColor, int secondaryColor) {
		this.themeColor = themeColor;
		this.secondaryColor = secondaryColor;
	}
	
	private void setText(String text) {
		for (String line : text.split("\n")) {
			line = line.replace("\n", "").replace("\r", "");
			if (!line.isEmpty()) {
				line = line.replace("<click>", Constant.click());
				line = line.replace("<clicking>", Constant.clicking());
				line = line.replace("<mouse>", Constant.mouse());
				line = line.replace("<esc>", Constant.menu());
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
		if (tutorialLayer != null) {
			tutorialLayer.paint(clock);
		}
	}

	private void triggerInstance(Trigger event) {
		if (hasNext() && peek().trigger == event) {
			Action action = nextAction();
			if (action instanceof Segue) {
				String path = ((Segue) action).path;
				Tutorial tutorial = ((Segue) action).tutorial;
				destroy();
				start(tutorial, path);
				return;
			} else {
				tutorialLayer.showAction(action);
			}
			canReshow = false;
		} else if (canReshow && actionIndex >= 0 && action().trigger == event) {
			tutorialLayer.showAction(action());
			canReshow = false;
		} else if (event != null && hasNext() && peek().skipTrigger == event) {
			actions.remove(peek());
			triggerInstance(event);
			return;
		} else if (event == Trigger.TextBoxFullyHidden && !hasNext()) {
			destroy();
			return;
		}
		if (actionIndex >= 0) {
			for (Highlightable highlightable : highlightables) {
				highlightable.highlighter().setHighlighted(
						highlightable.highlighter().hasTags(action().highlights));
			}
		}
	}
	
	private void destroy() {
		instance = null;
		tutorialLayer.destroy();
		clearIndicators();
	}

	protected Segue addSegue(Tutorial tutorial, String path, Trigger trigger) {
		Segue segue = new Segue(tutorial, path);
		segue.trigger = trigger;
		actions.add(segue);
		return segue;
	}

	protected void addStart() {
		actions.clear();
	}
	
	private void clearIndicatorsInstance() {
		if (tutorialLayer != null) {
			tutorialLayer.clearIndicators();
		}
		if (actionIndex >= 0 && action().canRepeat) {
			canReshow = true;
		}
	}
	
	public static void paint(Clock clock) {
		if (instance != null) {
			instance.paintInstance(clock);
		}
		float perc = (float)(System.currentTimeMillis() % HIGHLIGHT_CYCLE) / 
				HIGHLIGHT_CYCLE;
		perc = Math.abs(perc - 0.5f);
		for (Highlightable highlightable : highlightables) {
			if (highlightable.highlighter().highlighted()) {
				highlightable.highlighter().setTint(HIGHLIGHT_COLOR_1, HIGHLIGHT_COLOR_2, perc);
			}
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
	
	public static void clearIndicators() {
		for (Highlightable highlightable : highlightables) {
			highlightable.highlighter().setHighlighted(false);
		}
		highlightables.clear();
		if (instance != null) {
			instance.clearIndicatorsInstance();
		}
	}
	
	public static void addHighlightable(Highlightable highlightable) {
		highlightables.add(highlightable);
		if (instance != null && instance.actionIndex >= 0 && 
				highlightable.highlighter().hasTags(instance.action().highlights)) {
			highlightable.highlighter().setHighlighted(true);
		}
	}

	public static void clear() {
		if (instance != null) {
			instance.destroy();
			instance = null;
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
	
	protected class Segue extends Action {
		public final Tutorial tutorial;
		public final String path;
		
		public Segue(Tutorial tutorial, String path) {
			this.path = path;
			this.tutorial = tutorial;
		}
	}
	
	protected class Action { 
		public Trigger trigger, skipTrigger;
		public String message;
		public List<Indicator> indicators = new ArrayList<Tutorial.Indicator>();
		public List<Tag> highlights = new ArrayList<Tutorial.Tag>();
		public boolean canRepeat = true;
		
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
		
		public Action addHighlight(Tag highlight) {
			highlights.add(highlight);
			return this;
		}
		
		public Action setAlign(Align align) {
			indicators.get(indicators.size() - 1).align = align;
			return this;
		}

		public Action dontRepeat() {
			canRepeat = false;
			return this;
		}

		public Action setSkip(Trigger skipTrigger) {
			this.skipTrigger = skipTrigger;
			return this;
		}
	}
	
	protected class Indicator {
		public String name;
		public float x, y, width, height;
		public Align align = Align.Center;
		public int color;
	}

	public static void messageRepeated() {
		if (instance != null) {
			instance.canReshow = false;
		}
	}
}
