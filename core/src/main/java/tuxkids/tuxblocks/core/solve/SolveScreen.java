package tuxkids.tuxblocks.core.solve;

import java.util.ArrayList;
import java.util.List;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.Point;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.MenuSprite;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.n.NumberSelectScreen;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Equation;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController.Parent;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Sprite.SimplifyListener;

public class SolveScreen extends GameScreen implements Parent {
	
	private BlockController controller;
	private Button buttonBack;
	private Image buttonImageOk, buttonImageBack;
	private MenuSprite menu;
	private ImageLayer eqLayer, eqLayerOld;
	private Image lastEqImage;
	private SimplifyListener solveCallback;
	private boolean solveCorrect;
	
	public void setEquation(Equation equation) {
		controller.addEquation(equation);
	}
	
	public boolean solved() {
		return controller.solved();
	}

	public Equation equation() {
		return controller.equation();
	}
	
	public SolveScreen(final ScreenStack screens, GameState gameState) {
		super(screens, gameState);
		
		menu = new MenuSprite(width(), defaultButtonSize() * 1.2f);
		menu.layer().setDepth(-1);
		layer.add(menu.layer());
		
		controller = new BlockController(this, graphics().width(), height() - menu.height());
		layer.add(controller.layer());
		controller.layer().setTy(menu.height());
		
		eqLayer = graphics().createImageLayer();
		layer.add(eqLayer);
		eqLayer.setImage(controller.equationImage());
		eqLayer.setTranslation(20, 20);
		
		eqLayerOld = graphics().createImageLayer();
		layer.add(eqLayerOld);
		eqLayerOld.setImage(controller.equationImage());
		eqLayerOld.setTranslation(20, 20);
		eqLayerOld.setAlpha(0);

		buttonImageBack = PlayN.assets().getImage(Constant.BUTTON_DOWN);
		buttonImageOk = PlayN.assets().getImage(Constant.BUTTON_OK);
		buttonBack = createMenuButton(Constant.BUTTON_DOWN);
		buttonBack.setPosition(buttonBack.width() * 0.6f, buttonBack.height() * 0.6f);
		buttonBack.layerAddable().setDepth(10);
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) popThis();
			}
		});
		layer.add(buttonBack.layerAddable());
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
			eqLayerOld.setAlpha(1);
			lastEqImage = controller.equationImage();
		}
		if (solveCorrect && !entering()) {
			solveCallback.wasSimplified(true);
			solveCorrect = false;
			solveCallback = null;
		}
		buttonBack.setImage(controller.solved() ? buttonImageOk : buttonImageBack);
	}
	
	@Override
	public void paint(Clock clock) {
		controller.paint(clock);
		eqLayer.setAlpha(PlayNObject.lerpTime(eqLayer.alpha(), 1, 0.99f, clock.dt(), 0.01f));
		eqLayerOld.setAlpha(PlayNObject.lerpTime(eqLayerOld.alpha(), 0, 0.99f, clock.dt(), 0.01f));
	}

	@Override
	public void showNumberSelectScreen(Renderer problem, int answer, int startNumber,
			SimplifyListener callback) {
		NumberSelectScreen nss = new NumberSelectScreen(screens, state, problem, answer);
		nss.setFocusedNumber(startNumber);
		solveCallback = callback;
		solveCorrect = false;
		pushScreen(nss, screens.slide().left());
	}

	@Override
	protected void onChildScreenFinished(GameScreen screen) {
		super.onChildScreenFinished(screen);
		if (screen instanceof NumberSelectScreen) {
			if (((NumberSelectScreen) screen).hasCorrectAnswer()) {
				solveCorrect = true;
			} else {
				solveCallback.wasSimplified(false);
			}
		}
	}
	

}
