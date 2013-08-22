package tuxkids.tuxblocks.core.solve;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.BlockController;
import tuxkids.tuxblocks.core.solve.blocks.BlockController.Parent;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.HeaderLayer;

public abstract class EquationScreen extends GameScreen implements Parent {
	
	protected Equation originalEquation;
	protected BlockController controller;
	protected ImageLayer eqLayer, eqLayerOld;
	protected Image lastEqImage;
	
	protected float equationXPercent() {
		return 0.5f;
	}
	
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
		
		header.layerAddable().setDepth(-1);
		
		controller = new BlockController(this, controllerWidth(), height() - header.height());
		controller.setEquationImageHeight(header.height());
		controller.layer().setTy(header.height());
		layer.add(controller.layer());
		
		eqLayer = graphics().createImageLayer();
		eqLayer.setImage(controller.equationImage());
		layer.add(eqLayer);
		
		eqLayerOld = graphics().createImageLayer();
		eqLayerOld.setImage(controller.equationImage());
		eqLayerOld.setAlpha(0);
		layer.add(eqLayerOld);
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
			eqLayer.setTranslation(
					header.width() * equationXPercent() - eqLayer.width() / 2 + header.tx(), 
					(header.height() - eqLayer.height()) / 2);
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
