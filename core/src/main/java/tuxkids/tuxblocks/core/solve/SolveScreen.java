package tuxkids.tuxblocks.core.solve;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.defense.GameHeaderLayer;
import tuxkids.tuxblocks.core.screen.BaseScreen;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.SimplifyListener;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.HeaderLayer;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

public class SolveScreen extends EquationScreen {
	
	private Button buttonBack;
	private Image buttonImageOk, buttonImageBack;
	
	private SimplifyListener solveCallback;
	private boolean solveCorrect;
	private int solveLevel;
	private Stat solveStat;
	
	@Override
	protected float equationXPercent() {
		return 0.6f;
	}
	
	@Override
	protected int exitTime() {
		return 2000;
	}
	
	public SolveScreen(final ScreenStack screens, GameState gameState) {
		super(screens, gameState);

		buttonImageBack = PlayN.assets().getImage(Constant.BUTTON_DOWN);
		buttonImageOk = PlayN.assets().getImage(Constant.BUTTON_OK);
		buttonBack = header.addLeftButton(buttonImageBack);
		buttonBack.setNoSound();
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) popThis();
			}
		});
		register(buttonBack, Tag.Solve_Ok);
		layer.add(buttonBack.layerAddable());
		
		Button buttonReset = header.addRightButton(Constant.BUTTON_RESET);
		buttonReset.setPosition(width() - buttonReset.width() * 0.6f, header.height() / 2);
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
	protected HeaderLayer createHeader() {
		return new GameHeaderLayer(this, width()) {
			@Override
			protected void createWidgets() {
				createBars();
				createTimer();
			}
		}; 
	}
	
	@Override
	protected void popThis() {
		popThis(screens.slide().up());
		if (controller.solved()) {
			Audio.se().play(Constant.SE_SUCCESS);
		} else {
			Audio.se().play(Constant.SE_BACK);
		}
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
		if (controller.solved()) {
			buttonBack.setImage(buttonImageOk);
		} else {
			buttonBack.setImage(buttonImageBack);
		}
		
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
