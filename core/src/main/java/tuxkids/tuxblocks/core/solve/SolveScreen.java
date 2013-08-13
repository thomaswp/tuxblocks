package tuxkids.tuxblocks.core.solve;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.MenuLayer;
import tuxkids.tuxblocks.core.defense.DefenseMenu;
import tuxkids.tuxblocks.core.screen.BaseScreen;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.SimplifyListener;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.Debug;

public class SolveScreen extends EquationScreen {
	
	private Button buttonBack;
	private Image buttonImageOk, buttonImageBack;
	
	private SimplifyListener solveCallback;
	private boolean solveCorrect;
	private int solveLevel;
	private Stat solveStat;
	
	
	public SolveScreen(final ScreenStack screens, GameState gameState) {
		super(screens, gameState);

		buttonImageBack = PlayN.assets().getImage(Constant.BUTTON_DOWN);
		buttonImageOk = PlayN.assets().getImage(Constant.BUTTON_OK);
		buttonBack = menu.addLeftButton(buttonImageBack);
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) popThis();
			}
		});
		register(buttonBack, Tag.Solve_Ok);
		layer.add(buttonBack.layerAddable());
		
		Button buttonReset = menu.addRightButton(Constant.BUTTON_RESET);
		buttonReset.setPosition(width() - buttonReset.width() * 0.6f, menu.height() / 2);
		register(buttonReset, Tag.Solve_Reset);
		buttonReset.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) reset();
			}
		});
		layer.add(buttonReset.layerAddable());
	}
	
	@Override
	protected Trigger wasShownTrigger() {
		return Trigger.Solve_Shown;
	}
	
	@Override 
	protected MenuLayer createMenu() {
		return new DefenseMenu(this, width(), false); 
	}
	
	@Override
	protected void popThis() {
		popThis(screens.slide().up());
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if (solveCorrect && !entering()) {
			solveCallback.wasSimplified(true);
			state.addExpForLevel(solveStat, solveLevel);
			clearSolve();
		}
		if (buttonBack.image() != buttonImageOk && controller.solved()) {
			Tutorial.trigger(Trigger.Solve_Solved);
		}
		buttonBack.setImage(controller.solved() ? buttonImageOk : buttonImageBack);
	}
	
	private void clearSolve() {
		solveCorrect = false;
		solveCallback = null;
		solveStat = null;
		solveLevel = -1;
	}

	@Override
	public void showNumberSelectScreen(Renderer problem, int answer, int startNumber, 
			Stat stat, int level, SimplifyListener callback) {
		Debug.write("problem level: " + level);
		if (level > state.getStatLevel(stat)) {
			NumberSelectScreen nss = new NumberSelectScreen(screens, state, problem, answer);
			nss.setFocusedNumber(startNumber);
			solveCallback = callback;
			solveCorrect = false;
			solveStat = stat;
			solveLevel = level;
			pushScreen(nss, screens.slide().left());
		} else {
			callback.wasSimplified(true);
		}
	}

	@Override
	protected void onChildScreenFinished(BaseScreen screen) {
		super.onChildScreenFinished(screen);
		if (screen instanceof NumberSelectScreen) {
			if (((NumberSelectScreen) screen).hasCorrectAnswer()) {
				solveCorrect = true;
				Tutorial.trigger(Trigger.Solve_SimplifiedSuccess);
			} else {
				solveCallback.wasSimplified(false);
				clearSolve();
			}
		}
	}
	

}
