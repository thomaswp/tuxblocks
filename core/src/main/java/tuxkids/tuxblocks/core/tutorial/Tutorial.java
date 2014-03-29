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
import tuxkids.tuxblocks.core.story.StoryGameState;
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
		
		Build_Shown, Title_Story, Defense_BadTowerPlacement, 
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
		Defense_EquationSelectScreen,
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
	
	private static TutorialInstance instance;
	private static float startTimeOfHighlightCycle;
	private static boolean isFirstHighlightCycle;
	private final static List<Highlightable> highlightables = new ArrayList<Highlightable>();

	public static boolean running() {
		return instance != null;
	}
	
	/** Starts the main tutorial. */
	@Deprecated
	public static void start(final int themeColor, final int secondaryColor) {
		if (instance != null) {
			return;
		}
		FSMTutorial.setPrimaryColor(themeColor);
//		start(new TutorialStart(themeColor, secondaryColor), Constant.TUTORIAL_START_PATH);
//		start(new Tutorial0(), "TutorialStory0.json");
	}
	
	public static void start(int primaryColor, StoryGameState state) {
		FSMTutorial.setPrimaryColor(primaryColor);
		loadTutorial(state.getCurrentTutorialInstance(), state.getCurrentTutorialScript());
	}

	public static void loadTutorial(final TutorialInstance tutorial, final String path) {
		instance = tutorial;
		Lang.getText(path, new Callback<String>() {
			@Override
			public void onSuccess(String result) {
				tutorial.loadTextFile(result);
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
	}

	/** Called from {@link TuxBlocksGame#paint(float)} */
	public static void paint(Clock clock) {
		if (instance != null) {
			instance.paint(clock);
		}	
		paintHighlightables(clock);
	}

	private static void paintHighlightables(Clock clock) {
		Float perc = null;
		for (Highlightable highlightable : highlightables) {
			if (highlightable.highlighter().highlighted()) {
				if (perc == null)
					perc = calculatePercentHighlighted(clock);
				highlightable.highlighter().setTint(HIGHLIGHT_COLOR_1, HIGHLIGHT_COLOR_2, perc);
			}
		}
	}

	private static float calculatePercentHighlighted(Clock clock) {
		if (startTimeOfHighlightCycle < 0) {
			startTimeOfHighlightCycle = clock.time();
			return 0;
		}
		float perc = (clock.time()-startTimeOfHighlightCycle) / HIGHLIGHT_CYCLE;
		if (perc > 1.0f && isFirstHighlightCycle) {
			isFirstHighlightCycle = false;
		}
		if (isFirstHighlightCycle) 
			perc *= 2;		//double time for first 2 cycles
			
		perc = perc % 1;		//get fractional part
		perc = Math.abs(perc - 0.5f)+.2f;		//I like the highlight a bit brighter	
		return perc;
	}

	/** Called from {@link TuxBlocksGame#update(int)} */
	public static void update(int delta) {
		if (instance != null) {
			instance.update(delta);
		}
	}

	/** Indicate that a {@link Trigger} has occurred in the game. */
	public static void trigger(Trigger event) {
		if (instance != null) {
			instance.trigger(event);
		}
	}
	
	public static void trigger(Trigger event, Object extraInfo) {
		if (instance != null) {
			instance.trigger(event, extraInfo);
		}
	}
	
	/** Called when the player switches {@link Screen}s */
	public static void clearIndicators() {
		for (Highlightable highlightable : highlightables) {
			highlightable.highlighter().setHighlighted(false);
		}
		highlightables.clear();
		if (instance != null) {
			instance.didLeaveScreen();
		}
	}
	
	/** Register a {@link Highlightable} object on-screen */
	public static void addHighlightable(Highlightable highlightable) {
		highlightables.add(highlightable);
		//Debug.write(highlightable);
		if (instance != null) instance.refreshHighlights();
	}

	/** Called at the beginning of the game to clear static fields */
	public static void clear() {
		if (instance != null) {
			instance.destroy();
			instance = null;
		}
	}
	
	/** Called from {@link TutorialLayer} when the player reshows a message */
	public static void messageRepeated() {
		if (instance != null) {
			instance.wasRepeated();
		}
	}
	
	protected static void refreshHighlights(List<Tag> highlights) {
		startTimeOfHighlightCycle = -1.0f;
		isFirstHighlightCycle = true;
		for (Highlightable highlightable : highlightables) {
			highlightable.highlighter().setHighlighted(
					highlightable.highlighter().hasTags(highlights));
		}
	}
	
	protected static String prepareMessage(String line) {
		if (line == null) return null;

		line = line.replace("\n", "").replace("\r", "");
		
		String domain = "tutorial";
		// replace platform-specific text
		line = line.replace("<click>", Lang.getDeviceString(domain, Constant.TUTORIAL_TEXT_CLICK));
		line = line.replace("<clicking>", Lang.getDeviceString(domain, Constant.TUTORIAL_TEXT_CLICKING));
		line = line.replace("<mouse>", Lang.getDeviceString(domain, Constant.TUTORIAL_TEXT_MOUSE));
		line = line.replace("<esc>", Lang.getDeviceString(domain, Constant.TUTORIAL_TEXT_MENU));
		
		return line;
	}
	
}
