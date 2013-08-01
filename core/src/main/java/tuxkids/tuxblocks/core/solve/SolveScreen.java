package tuxkids.tuxblocks.core.solve;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.MenuSprite;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.DefenseMenu;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.BlockController;
import tuxkids.tuxblocks.core.solve.blocks.BlockController.Parent;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.Sprite.SimplifyListener;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.utils.Debug;

public class SolveScreen extends GameScreen implements Parent {
	
	private Equation originalEquation;
	private BlockController controller;
	private Button buttonBack;
	private Image buttonImageOk, buttonImageBack;
	private ImageLayer eqLayer, eqLayerOld;
	private Image lastEqImage;
	
	private SimplifyListener solveCallback;
	private boolean solveCorrect;
	private int solveLevel;
	private Stat solveStat;
	
	public void setEquation(Equation equation) {
		this.originalEquation = equation;
		controller.addEquation(equation.copy());
	}
	
	public boolean solved() {
		return controller.solved();
	}

	public Equation equation() {
		return controller.equation();
	}
	
	public void reset() {
		controller.clear();
		controller.addEquation(originalEquation.copy());
	}
	
	public SolveScreen(final ScreenStack screens, GameState gameState) {
		super(screens, gameState);
		
		menu.layerAddable().setDepth(-1);
		
		controller = new BlockController(this, graphics().width(), height() - menu.height());
		layer.add(controller.layer());
		controller.layer().setTy(menu.height());
		
		eqLayer = graphics().createImageLayer();
		layer.add(eqLayer);
		eqLayer.setImage(controller.equationImage());
		
		eqLayerOld = graphics().createImageLayer();
		layer.add(eqLayerOld);
		eqLayerOld.setImage(controller.equationImage());
		eqLayerOld.setAlpha(0);

		buttonImageBack = PlayN.assets().getImage(Constant.BUTTON_DOWN);
		buttonImageOk = PlayN.assets().getImage(Constant.BUTTON_OK);
		buttonBack = menu.addLeftButton(buttonImageBack);
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) popThis();
			}
		});
		layer.add(buttonBack.layerAddable());
		
		Button buttonReset = menu.addRightButton(Constant.BUTTON_RESET);
		buttonReset.setPosition(width() - buttonReset.width() * 0.6f, menu.height() / 2);
		buttonReset.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) reset();
			}
		});
		layer.add(buttonReset.layerAddable());
	}
	
	@Override 
	protected MenuSprite createMenu() {
		return new DefenseMenu(state, width(), false); 
	}
	
	@Override
	protected void popThis() {
		popThis(screens.slide().up());
	}
	
	@Override
	public void wasAdded() {
		super.wasAdded();	
	}
	
	@Override
	public void wasRemoved() {
		super.wasRemoved();
		controller.clear();
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		controller.update(delta);
		eqLayer.setImage(controller.equationImage());
		if (lastEqImage != controller.equationImage()) {
			eqLayer.setAlpha(0);
			eqLayerOld.setImage(lastEqImage);
			eqLayerOld.setTranslation(eqLayer.tx(), eqLayer.ty());
			eqLayerOld.setAlpha(1);
			lastEqImage = controller.equationImage();
		}
		eqLayer.setTranslation((width() - eqLayer.width()) / 2 , (menu.height() - eqLayer.height()) / 2);
		if (solveCorrect && !entering()) {
			solveCallback.wasSimplified(true);
			state.addExpForLevel(solveStat, solveLevel);
			clearSolve();
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
	public void paint(Clock clock) {
		super.paint(clock);
		controller.paint(clock);
		eqLayer.setAlpha(PlayNObject.lerpTime(eqLayer.alpha(), 1, 0.99f, clock.dt(), 0.01f));
		eqLayerOld.setAlpha(PlayNObject.lerpTime(eqLayerOld.alpha(), 0, 0.99f, clock.dt(), 0.01f));
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
	protected void onChildScreenFinished(GameScreen screen) {
		super.onChildScreenFinished(screen);
		if (screen instanceof NumberSelectScreen) {
			if (((NumberSelectScreen) screen).hasCorrectAnswer()) {
				solveCorrect = true;
			} else {
				solveCallback.wasSimplified(false);
				clearSolve();
			}
		}
	}
	

}
