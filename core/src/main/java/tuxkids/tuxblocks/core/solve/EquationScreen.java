package tuxkids.tuxblocks.core.solve;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.MenuSprite;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.BlockController;
import tuxkids.tuxblocks.core.solve.blocks.BlockController.Parent;
import tuxkids.tuxblocks.core.solve.blocks.Equation;

public abstract class EquationScreen extends GameScreen implements Parent {
	
	protected Equation originalEquation;
	protected BlockController controller;
	protected ImageLayer eqLayer, eqLayerOld;
	protected Image lastEqImage;
	
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
	
	protected float controllerWidth() {
		return width();
	}
	
	public EquationScreen(ScreenStack screens, GameState state) {
		super(screens, state);
		
		menu.layerAddable().setDepth(-1);
		
		controller = new BlockController(this, controllerWidth(), height() - menu.height());
		controller.setEquationImageHeight(menu.height());
		controller.layer().setTy(menu.height());
		layer.add(controller.layer());
		
		eqLayer = graphics().createImageLayer();
		layer.add(eqLayer);
		eqLayer.setImage(controller.equationImage());
		
		eqLayerOld = graphics().createImageLayer();
		layer.add(eqLayerOld);
		eqLayerOld.setImage(controller.equationImage());
		eqLayerOld.setAlpha(0);
	}
	
	@Override
	public void wasRemoved() {
		super.wasRemoved();
		controller.clear();
	}
	
	public void reset() {
		controller.clear();
		controller.addEquation(originalEquation.copy());
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
		if (eqLayer.image() != null) {
			eqLayer.setTranslation((menu.width() - eqLayer.width()) / 2 + menu.tx(), (menu.height() - eqLayer.height()) / 2);
		}
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		controller.paint(clock);
		eqLayer.setAlpha(PlayNObject.lerpTime(eqLayer.alpha(), 1, 0.99f, clock.dt(), 0.01f));
		eqLayerOld.setAlpha(PlayNObject.lerpTime(eqLayerOld.alpha(), 0, 0.99f, clock.dt(), 0.01f));
	}
}
