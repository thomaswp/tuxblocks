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
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.MinusBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.ModifierBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.NumberBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.OverBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.PlusBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.TimesBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.VariableBlockSprite;
import tuxkids.tuxblocks.core.utils.Debug;

public class SolveScene extends GameScreen {

	public SolveScene(ScreenStack screens, GameState state) {
		super(screens, state);
	}
	
	BaseBlockSprite draggingFrom;
	ModifierBlockSprite dragging;
	List<BaseBlockSprite> baseBlocks = new ArrayList<BaseBlockSprite>();
	
	@Override
	public void wasAdded() {
		BaseBlockSprite sprite = new VariableBlockSprite("x");
		sprite.addModifier(new PlusBlockSprite(3));
		sprite.addModifier(new MinusBlockSprite(4));
		sprite.addModifier(new TimesBlockSprite(5));
		sprite.addModifier(new OverBlockSprite(6));
		sprite.addModifier(new PlusBlockSprite(7));
		sprite.addModifier(new MinusBlockSprite(8));
		sprite.addModifier(new TimesBlockSprite(1));
		sprite.addModifier(new OverBlockSprite(2));
		
		
		BaseBlockSprite sprite1 = sprite;
		layer.addAt(sprite1.layerAddable(), 100, 200);
		sprite1.addBlockListener(new BListener());
		
		BaseBlockSprite sprite2 = new NumberBlockSprite(5);
		layer.addAt(sprite2.layerAddable(), 600, 200);
		sprite2.addBlockListener(new BListener());
		
		baseBlocks.add(sprite1);
		baseBlocks.add(sprite2);
		
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
				Debug.write(baseBlocks.get(0).hierarchy());
				Debug.write("--------------");
				Debug.write(baseBlocks.get(1).hierarchy());
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
		for (BaseBlockSprite sprite : baseBlocks) sprite.update(delta);
		if (dragging != null) dragging.update(delta);
	}
	
	@Override
	public void paint(Clock clock) {
		for (BaseBlockSprite sprite : baseBlocks) sprite.paint(clock);
		if (dragging != null) dragging.paint(clock);
	}

	
	private class BListener implements BlockListener {
		
		@Override
		public void wasGrabbed(ModifierBlockSprite sprite, float gx, float gy) {
			for (BaseBlockSprite base : baseBlocks) {
				if (base.contains(gx, gy)) {
					draggingFrom = base;
				}
			}
			layer.add(sprite.layer());
			sprite.layer().setDepth(1);
			dragging = sprite;
		}

		@Override
		public void wasMoved(ModifierBlockSprite sprite, float gx, float gy) {
			for (BaseBlockSprite base : baseBlocks) {
				base.setPreview(base.contains(gx, gy));
			}
		}

		@Override
		public boolean wasReleased(ModifierBlockSprite sprite, float gx,
				float gy) {
			dragging = null;
			BaseBlockSprite r = null;
			for (BaseBlockSprite base : baseBlocks) {
				base.clearPreview();
				if (base.contains(gx, gy)) r = base;
			}
			if (r != null) {
				r.addModifier(sprite, false);
				return true;
			} else {
				draggingFrom.addModifier(sprite, false);
				return false;
			}
		}
	}
}
