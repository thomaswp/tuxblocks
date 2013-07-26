package tuxkids.tuxblocks.core.solve.blocks.n;

import playn.core.Color;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Mouse.Listener;
import playn.core.PlayN;
import playn.core.Mouse.ButtonEvent;
import playn.core.Mouse.MotionEvent;
import playn.core.Mouse.WheelEvent;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.n.markup.Renderer;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController.Parent;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController.Side;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.MinusBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.NumberBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.OverBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.PlusBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Sprite.SimplifyListener;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.TimesBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.VariableBlockSprite;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.Debug;

public class SolveScene extends GameScreen implements Parent {

	private BlockController controller;
	private ImageLayer eqLayer, eqLayerOld;
	private Image lastEqImage;
	private SimplifyListener solveCallback;
	private boolean solveCorrect;
	
	public SolveScene(ScreenStack screens, GameState state) {
		super(screens, state);
	}
	
	@Override
	public void wasAdded() {
		controller = new BlockController(this, graphics().width(), graphics().height());
		layer.add(controller.layer());
		
		BaseBlockSprite sprite1 = new VariableBlockSprite("x")
		.addModifier(new TimesBlockSprite(3));
		controller.addExpression(Side.Left, sprite1);
		
		BaseBlockSprite sprite2 = new VariableBlockSprite("x")
		.addModifier(new MinusBlockSprite(7))
		.addModifier(new OverBlockSprite(4));
		controller.addExpression(Side.Left, sprite2);
		
		controller.addExpression(Side.Right, new NumberBlockSprite(8));
		
		eqLayer = graphics().createImageLayer();
		layer.add(eqLayer);
		eqLayer.setImage(controller.equationImage());
		eqLayer.setTranslation(20, 20);
		
		eqLayerOld = graphics().createImageLayer();
		layer.add(eqLayerOld);
		eqLayerOld.setImage(controller.equationImage());
		eqLayerOld.setTranslation(20, 20);
		eqLayerOld.setAlpha(0);

		
//		float w = graphics().width() / 360f, x = 0;
//		for (int i = 0; i < 360; i++) {
//			ImageLayer l = graphics().createImageLayer();
//			l.setImage(CanvasUtils.createRect(w + 1, 100, getColor(i)));
//			l.setDepth(100);
//			l.setTx(x);
//			x += w;
//			layer.add(l);
//		}
//		PlayN.mouse().setListener(new Listener() {
//			
//			@Override
//			public void onMouseWheelScroll(WheelEvent event) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onMouseUp(ButtonEvent event) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onMouseMove(MotionEvent event) {
//				int d = (int)(event.x() / graphics().width() * 360);
//				int c = getColor(d);
//				int r = Color.red(c), g = Color.green(c), b = Color.blue(c);
//				Debug.write("%d: [%d, %d, %d]", d, r, g, b);
//			}
//			
//			@Override
//			public void onMouseDown(ButtonEvent event) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
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
	
	protected int getColor(int degree) {
		degree = degree % 360;
		if (degree <= 120) {
			degree /= 2;
		} else if (degree <= 180) {
			degree -= 60;
		} else if (degree < 240) {
			degree = (degree - 180) * 2 + 120;
		}
		int color = CanvasUtils.hsvToRgb((degree%360) / 360f, 0.9f, 0.9f);
		return color;
	}
}
