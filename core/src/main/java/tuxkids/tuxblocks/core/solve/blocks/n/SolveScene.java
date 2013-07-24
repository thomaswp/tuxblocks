package tuxkids.tuxblocks.core.solve.blocks.n;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController.Side;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.MinusBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.NumberBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.OverBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.PlusBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.TimesBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.VariableBlockSprite;
import tuxkids.tuxblocks.core.utils.Debug;

public class SolveScene extends GameScreen {

	private BlockController controller;
	private ImageLayer eqLayer, eqLayerOld;
	private Image lastEqImage;
	
	public SolveScene(ScreenStack screens, GameState state) {
		super(screens, state);
	}
	
	@Override
	public void wasAdded() {
		controller = new BlockController();
		layer.add(controller.layer());
		
		BaseBlockSprite sprite = new VariableBlockSprite("x");
		sprite.addModifier(new PlusBlockSprite(3));
		sprite.addModifier(new MinusBlockSprite(3));
		sprite.addModifier(new TimesBlockSprite(5));
		sprite.addModifier(new OverBlockSprite(6));
		sprite.addModifier(new PlusBlockSprite(7));
		sprite.addModifier(new MinusBlockSprite(8));
		sprite.addModifier(new TimesBlockSprite(5));
		sprite.addModifier(new OverBlockSprite(6));
		
		Debug.write(sprite.hierarchy());
		Debug.write(((BaseBlockSprite) sprite.copy()).hierarchy());
		
		
		BaseBlockSprite sprite1 = sprite;
		controller.addExpression(Side.Left, sprite1);
		
		BaseBlockSprite sprite2 = new NumberBlockSprite(5);
		controller.addExpression(Side.Right, sprite2);
		
		controller.addExpression(Side.Left, new BlockHolder());
		
		eqLayer = graphics().createImageLayer();
		layer.add(eqLayer);
		eqLayer.setImage(controller.equationImage());
		eqLayer.setTranslation(20, 20);
		
		eqLayerOld = graphics().createImageLayer();
		layer.add(eqLayerOld);
		eqLayerOld.setImage(controller.equationImage());
		eqLayerOld.setTranslation(20, 20);
		eqLayerOld.setAlpha(0);

	}
	
	@Override
	public void update(int delta) {
		controller.update(delta);
		eqLayer.setImage(controller.equationImage());
		if (lastEqImage != controller.equationImage()) {
			eqLayer.setAlpha(0);
			eqLayerOld.setImage(lastEqImage);
			eqLayerOld.setAlpha(1);
			lastEqImage = controller.equationImage();
		}
	}
	
	@Override
	public void paint(Clock clock) {
		controller.paint(clock);
		eqLayer.setAlpha(PlayNObject.lerpTime(eqLayer.alpha(), 1, 0.99f, clock.dt(), 0.01f));
		eqLayerOld.setAlpha(PlayNObject.lerpTime(eqLayerOld.alpha(), 0, 0.99f, clock.dt(), 0.01f));
	}
}
