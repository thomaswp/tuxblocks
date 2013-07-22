package tuxkids.tuxblocks.core.solve.blocks.n;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.Path;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite.BlockListener;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController.Side;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.MinusBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.ModifierBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.NumberBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.OverBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.PlusBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.TimesBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.VariableBlockSprite;
import tuxkids.tuxblocks.core.utils.Debug;

public class SolveScene extends GameScreen {

	private BlockController controller;
	private ImageLayer eqLayer;
	
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
		
		
		BaseBlockSprite sprite1 = sprite;
		controller.addExpression(Side.Left, sprite1);
		
		BaseBlockSprite sprite2 = new NumberBlockSprite(5);
		controller.addExpression(Side.Right, sprite2);
		
		controller.addExpression(Side.Left, new BlockHolder());
		
		eqLayer = graphics().createImageLayer();
		layer.add(eqLayer);
		eqLayer.setImage(controller.getEquationImage());
		eqLayer.setTranslation(20, 20);

	}
	
	int u = 0;
	@Override
	public void update(int delta) {
		controller.update(delta);
		u += delta;
		if (u > 500) {
			u = 0;
			eqLayer.setImage(controller.getEquationImage());
		}
	}
	
	@Override
	public void paint(Clock clock) {
		controller.paint(clock);
	}
}
