package tuxkids.tuxblocks.core.tutorial;

import java.util.ArrayList;
import java.util.List;

import playn.core.Color;
import playn.core.util.Callback;
import playn.core.util.Clock;
import tripleplay.game.Screen;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.Lang;
import tuxkids.tuxblocks.core.TuxBlocksGame;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.Button;

/**
 * A class for managing Tutorials. Also the base class for the
 * individual tutorials.
 */
public abstract class Tutorial extends PlayNObject {

	public final static int HIGHLIGHT_COLOR_1 = Color.rgb(0xff, 0xaa, 0x44);
	public final static int HIGHLIGHT_COLOR_2 = Color.rgb(0xff, 0x44, 0x44);
	public final static int HIGHLIGHT_CYCLE = 1500;
	
	/**
	 * Events which can happen in-game. When they do happen, the
	 * corresponding Trigger will be passed to {@link Tutorial#trigger(Trigger)}.
	 */
	public enum Trigger {
		TextBoxHidden, 
		TextBoxFullyHidden,
		
		Title_Play, 
		Title_Build, 
		
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
	
	/**
	 * A tag for a specific {@link Button} or other 
	 * {@link Highlightable} object for identification.
	 */
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
	
	/** Starts the main tutorial. */
	public static void start(final int themeColor, final int secondaryColor) {
		if (instance != null) {
			return;
		}
		start(new TutorialStart(themeColor, secondaryColor), Constant.TUTORIAL_START_PATH);
	}
	
	private static void start(final Tutorial tutorial, final String path) {
		instance = tutorial;
		Lang.getText(path, new Callback<String>() {
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
		// load the text into lines
		for (String line : text.split("\n")) {
			// normalize line-ends
			line = line.replace("\n", "").replace("\r", "");
			if (!line.replace(" ", "").isEmpty()) {
				String domain = "tutorial";
				// replace platform-specific text
				line = line.replace("<click>", Lang.getDeviceString(domain, Constant.TUTORIAL_TEXT_CLICK));
				line = line.replace("<clicking>", Lang.getDeviceString(domain, Constant.TUTORIAL_TEXT_CLICKING));
				line = line.replace("<mouse>", Lang.getDeviceString(domain, Constant.TUTORIAL_TEXT_MOUSE));
				line = line.replace("<esc>", Lang.getDeviceString(domain, Constant.TUTORIAL_TEXT_MENU));
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

	// called when something happens that might
	// advance the tutorial
	private void triggerInstance(Trigger event) {
		if (hasNext() && peek().trigger == event) {
			// if the tirgger causes the next Action...
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
			// or reshow this action's message if the player has left the screen
			// it was originally shown on and come back
			tutorialLayer.showAction(action());
			canReshow = false;
		} else if (event != null && hasNext() && peek().skipTrigger == event) {
			// sometimes we want to skip an Action if the next Action is ready
			// (when players do things out of order) so we skip an Action
			actions.remove(peek());
			triggerInstance(event);
			return;
		} else if (event == Trigger.TextBoxFullyHidden && !hasNext()) {
			// if this is the last Action and the textbox is hidden, end the tutorial
			destroy();
			return;
		}
		if (actionIndex >= 0) {
			// refresh highlights
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

	// for segueing into a new tutorial 
	protected Segue addSegue(Tutorial tutorial, String path, Trigger trigger) {
		Segue segue = new Segue(tutorial, path);
		segue.trigger = trigger;
		actions.add(segue);
		return segue;
	}

	// for skipping part of a tutorial for testing
	// i.e. don't use this in production
	@Deprecated
	protected void addStart() {
		actions.clear();
	}
	
	private void clearIndicatorsInstance() {
		if (tutorialLayer != null) {
			tutorialLayer.clearIndicators();
		}
		if (actionIndex >= 0 && action().canRepeat) {
			// alow the Action's message to be reshown, if applicable
			canReshow = true;
		}
	}
	
	/** Called from {@link TuxBlocksGame#paint(float)} */
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

	/** Called from {@link TuxBlocksGame#update(int)} */
	public static void update(int delta) {
		if (instance != null) {
			instance.updateInstance(delta);
		}
	}

	/** Indicate that a {@link Trigger} has occured in the game. */
	public static void trigger(Trigger event) {
		if (instance != null) {
			instance.triggerInstance(event);
		}
	}
	
	/** Called when the player switches {@link Screen}s */
	public static void clearIndicators() {
		for (Highlightable highlightable : highlightables) {
			highlightable.highlighter().setHighlighted(false);
		}
		highlightables.clear();
		if (instance != null) {
			instance.clearIndicatorsInstance();
		}
	}
	
	/** Register a {@link Highlightable} object on-screen */
	public static void addHighlightable(Highlightable highlightable) {
		highlightables.add(highlightable);
		if (instance != null && instance.actionIndex >= 0 && 
				highlightable.highlighter().hasTags(instance.action().highlights)) {
			highlightable.highlighter().setHighlighted(true);
		}
	}

	/** Called at the beginning of the game to clear static fields */
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
	
	/** For segueing between tutorials */
	protected class Segue extends Action {
		public final Tutorial tutorial;
		public final String path;
		
		public Segue(Tutorial tutorial, String path) {
			this.path = path;
			this.tutorial = tutorial;
		}
	}
	
	/** Represents one segment of the tutorial, it's trigger and what it highlights. */
	protected class Action { 
		public Trigger trigger, skipTrigger;
		public String message;
		public List<Indicator> indicators = new ArrayList<Tutorial.Indicator>();
		public List<Tag> highlights = new ArrayList<Tutorial.Tag>();
		public boolean canRepeat = true;
		
		@Deprecated
		public Action addIndicatorR(String name, int color, float x, float y, float width, float height) {
			return addIndicatorR(name, color, x, y, width, height, Align.Center);
		}
		
		@Deprecated
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
	
	/** Currently not supported */
	protected class Indicator {
		public String name;
		public float x, y, width, height;
		public Align align = Align.Center;
		public int color;
	}

	/** Called from {@link TutorialLayer} when the player reshows a message */
	public static void messageRepeated() {
		if (instance != null) {
			instance.canReshow = false;
		}
	}
}
