package tuxkids.tuxblocks.core.solve.blocks.n;

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

public class SolveScene extends GameScreen {

	public SolveScene(ScreenStack screens, GameState state) {
		super(screens, state);
	}
	
	BaseBlockSprite sprite;
	
	@Override
	public void wasAdded() {
		BaseBlock block = new VariableBlock("x");
		block.addModifier(new PlusBlock(5));
		block.addModifier(new MinusBlock(2));
		block.addModifier(new TimesBlock(3));
		block.addModifier(new OverBlock(4));
		block.addModifier(new TimesBlock(3));
		block.addModifier(new OverBlock(4));
		block.addModifier(new PlusBlock(5));
		block.addModifier(new MinusBlock(2));
		
		sprite = new BaseBlockSprite(block);
		layer.addAt(sprite.layerAddable(), 200, 200);
		
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
		
		PlayN.pointer().setListener(new Listener() {
			
			@Override
			public void onPointerStart(Event event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPointerEnd(Event event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPointerDrag(Event event) {
//				float width = Math.max(event.x() - np.layerAddable().tx(), 0);
//				float height = Math.max(event.y() - np.layerAddable().ty(), 0);
//				np.setSize(width, height);
			}
			
			@Override
			public void onPointerCancel(Event event) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public void update(int delta) {
		sprite.update(delta);
	}
	
	@Override
	public void paint(Clock clock) {
		sprite.paint(clock);
	}

}
