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
	
	public SolveScene(ScreenStack screens, GameState state) {
		super(screens, state);
	}
	
	@Override
	public void wasAdded() {
		controller = new BlockController();
		layer.add(controller.layer());
		
		BaseBlockSprite sprite = new VariableBlockSprite("x");
		sprite.addModifier(new PlusBlockSprite(3));
		sprite.addModifier(new MinusBlockSprite(4));
		sprite.addModifier(new TimesBlockSprite(5));
		sprite.addModifier(new OverBlockSprite(6));
		sprite.addModifier(new PlusBlockSprite(7));
		sprite.addModifier(new MinusBlockSprite(8));
		sprite.addModifier(new TimesBlockSprite(5));
		sprite.addModifier(new OverBlockSprite(6));
		
		
		BaseBlockSprite sprite1 = sprite;
		controller.addExpression(sprite1, 100, 200);
		
		BaseBlockSprite sprite2 = new NumberBlockSprite(5);
		controller.addExpression(sprite2, 600, 200);
		
		controller.addExpression(new BlockHolder(), 450, 200);
		
//		int sideWidth = 6; int baseHeight = 50;
//		int middle = 20;
//		CanvasImage image = graphics().createImage(sideWidth * 2 + middle, baseHeight + middle);
//		Path p = image.canvas().createPath();
//		p.moveTo(0, 0);
//		float width = image.width() - 1;
//		float height = image.height() - 1;
//		p.lineTo(width, 0);
//		p.lineTo(width, height);
//		p.lineTo(width - sideWidth, height);
//		p.lineTo(width - sideWidth, height - middle);
//		p.lineTo(sideWidth, height - middle);
//		p.lineTo(sideWidth, height);
//		p.lineTo(0, height);
//		p.close();
//		image.canvas().setFillColor(Colors.RED);
//		image.canvas().fillPath(p);
//		image.canvas().setStrokeColor(Colors.BLACK);
//		image.canvas().strokePath(p);
//		
//		int[] widthDims = new int[] { sideWidth + 2, middle - 5, sideWidth + 3};
//		int[] heightDims = new int[] { baseHeight + 2, middle - 5, 3 };

		
//		final NinepatchLayer np = new NinepatchLayer(PlayN.assets().getImage("test.9.png"));
//		layer.addAt(np.layerAddable(), 50, 50);

	}
	
	@Override
	public void update(int delta) {
		controller.update(delta);
	}
	
	@Override
	public void paint(Clock clock) {
		controller.paint(clock);
	}
}
