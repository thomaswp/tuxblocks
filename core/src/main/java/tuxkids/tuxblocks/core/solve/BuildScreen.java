package tuxkids.tuxblocks.core.solve;

import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.MenuLayer;
import tuxkids.tuxblocks.core.screen.BaseScreen;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.Toolbox.NumberSelectListener;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.Sprite;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.SimplifyListener;
import tuxkids.tuxblocks.core.solve.markup.BlankRenderer;
import tuxkids.tuxblocks.core.solve.markup.Renderer;

public class BuildScreen extends EquationScreen implements NumberSelectListener {

	protected Toolbox toolbox;
	protected SolveScreen solveScreen;

	protected float toolboxWidth() {
		return Sprite.baseSize() * 1.2f;
	}

	protected float controllerWidth() {
		return width() - toolboxWidth();
	}

	public BuildScreen(final ScreenStack screens, GameState state) {
		super(screens, state);

		solveScreen = new SolveScreen(screens, state);

		menu.setTx(toolboxWidth());
		menu.addRightButton(Constant.BUTTON_OK).setOnReleasedListener(
				new OnReleasedListener() {
					@Override
					public void onRelease(Event event, boolean inButton) {
						if (inButton) {
							solveScreen.setEquation(controller.equation());
							pushScreen(solveScreen, screens.slide().down());
						}
					}
				});

		menu.addLeftButton(Constant.BUTTON_DOWN).setOnReleasedListener(
				new OnReleasedListener() {
					@Override
					public void onRelease(Event event, boolean inButton) {
						if (inButton) {
							popThis();
						}
					}
				});

		controller.layer().setTx(toolboxWidth());
		controller.layer().setDepth(1);
		setEquation(Equation.NOOP);

		toolbox = new Toolbox(controller, this, toolboxWidth(), height(), state.themeColor());
		layer.add(toolbox.layerAddable());
	}

	@Override
	public void showNumberSelectScreen(Renderer problem, int answer,
			int startNumber, Stat stat, int level, SimplifyListener callback) {
		callback.wasSimplified(true);
	}

	@Override
	protected MenuLayer createMenu() {
		return new MenuLayer(state, width() - toolboxWidth());
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		toolbox.update(delta);
	}

	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		toolbox.paint(clock);
	}

	@Override
	public void selectNumber(int startNumber) {
		NumberSelectScreen nss = new NumberSelectScreen(screens, state, 
				new BlankRenderer(), NumberSelectScreen.ANY_ANSWER);
		nss.setFocusedNumber(startNumber);
		pushScreen(nss, screens.slide().left());
	}

	@Override
	protected void onChildScreenFinished(BaseScreen screen) {
		super.onChildScreenFinished(screen);
		if (screen instanceof NumberSelectScreen) {
			Integer answer = ((NumberSelectScreen) screen).selectedAnswer();
			if (answer != null) {
				toolbox.setNumber(answer);
			}
		}
	}

	@Override
	protected void popThis() {
		popThis(screens.slide().up());
	}
}
