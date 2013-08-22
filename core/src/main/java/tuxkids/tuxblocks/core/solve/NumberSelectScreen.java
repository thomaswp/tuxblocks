package tuxkids.tuxblocks.core.solve;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Layer.HitTester;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.GameHeaderLayer;
import tuxkids.tuxblocks.core.layers.NumberLayer;
import tuxkids.tuxblocks.core.layers.NumberLayer.NumberBitmapFont;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.markup.ExpressionWriter;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.HeaderLayer;
import tuxkids.tuxblocks.core.widget.MainMenuLayer;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

public class NumberSelectScreen extends GameScreen implements Listener {
	
	private int SPACING = 150;
	private final static int MAX_NUMS = 70;
	private final static int MAX_SPRITE_CREATE_PER_FRAME = 100;
	public final static int ANY_ANSWER = Integer.MAX_VALUE;
	
	private TextFormat numberFormat, problemFormat;
	private Point selectedPoint, possibleSelectedPoint;
	private Vector velocity = new Vector();
	private Vector position = new Vector(), lastPosition = new Vector();
	private Vector dragOffset = new Vector();
	private List<Vector> positionTrail = new ArrayList<Vector>();
	private List<Double> timeTrail = new ArrayList<Double>();
	
	private GroupLayer foregroundLayer, backgroundLayer, equationLayer;
	private int createsSpritesThisFrame;
	private int themeColor;
	private Vector blankCenter;
	private ImageLayer equationAnswer;
	private Point equationAnswerPoint;
	private Renderer problem;
	private int answer;
	private Button buttonBack, buttonCenter, buttonScratch, buttonClear;
	private Image backImageOk, backImageBack, backImageCancel;
	private Point recenterPoint = new Point();
	private boolean madeMistake;

	private List<Point> numberPoints = new ArrayList<Point>();
	private List<NumberLayer> numberImages = new ArrayList<NumberLayer>();
	private NumberLayer selectedNumberLayer;
	private NumberBitmapFont bitmapFont, bitmapFontColored;
	
	private ScratchLayer scratchLayer;
	private boolean scratchMode;
	
	public Integer selectedAnswer() {
		if (selectedPoint == null) return null;
		return getNumber(selectedPoint);
	}
	
	public boolean hasCorrectAnswer() {
		Integer answer = selectedAnswer();
		return answer != null && (answer == this.answer || this.answer == ANY_ANSWER);
	}
	
	public void setFocusedNumber(int number) {
		recenterPoint = getPoint(number);
	}
	
	public NumberSelectScreen(ScreenStack screens, GameState gameState, Renderer problem, int answer) {
		super(screens, gameState);
		this.problem = problem;
		this.answer = answer;
	}
	
	public boolean noMistakes() {
		return !madeMistake;
	}
	
	@Override
	protected int exitTime() {
		return 3000;
	}

	@Override
	public void wasAdded() {
		super.wasAdded();
		
		themeColor = state.themeColor();
		
		SPACING = (int)(height() / 3.5f);
		position.set(recenterPoint.x * SPACING, recenterPoint.y * SPACING);
		lastPosition.set(position);
		numberFormat = new TextFormat().withFont(
				graphics().createFont(Constant.FONT_NAME, Style.PLAIN, SPACING / 3));
		bitmapFont = new NumberBitmapFont(numberFormat, Colors.WHITE);
		bitmapFontColored = new NumberBitmapFont(numberFormat, themeColor);
		
		problemFormat = new TextFormat().withFont(
				graphics().createFont(Constant.FONT_NAME, Style.PLAIN, SPACING / 3 * 0.8f / problem.lines()));
		backgroundLayer = graphics().createGroupLayer();
		
		foregroundLayer = graphics().createGroupLayer();
		layer.add(foregroundLayer);
		PlayN.pointer().setListener(this);
		createBackground();
		createEquation(problem);
		foregroundLayer.setOrigin(-width() / 2, -height() / 2 - header.height() / 2);
		backgroundLayer.setTranslation(0, header.height() / 2);
		
		selectedNumberLayer = new NumberLayer(bitmapFontColored);
		selectedNumberLayer.setDepth(15);
		foregroundLayer.add(selectedNumberLayer.layerAddable());
		
		madeMistake = false;
		
//		touchLayer = graphics().createImageLayer(
//		CanvasUtils.createRect(1, 1, CanvasUtils.TRANSPARENT));
//		touchLayer.addListener(this);
//		touchLayer.setDepth(100);
//		touchLayer.setSize(width(), height());
//		layer.add(touchLayer);
		
		createScratch();
		
		update(0);
	}
	
	@Override
	public HeaderLayer createHeader() {
		return new GameHeaderLayer(this, width()) {
			@Override
			protected void createWidgets() {
				createTimer();
			}
		};
	}
	
	@Override
	public Trigger wasShownTrigger() {
		return Trigger.Number_Shown;
	}
	
	private void createScratch() {
		buttonScratch = header.createButton(Constant.BUTTON_SCRATCH);
		buttonScratch.setPosition(buttonScratch.width() * 0.6f, height() - buttonScratch.height() * 0.6f);
		buttonScratch.layerAddable().setDepth(21);
		layer.add(buttonScratch.layerAddable());
		
		buttonClear = header.createButton(Constant.BUTTON_RESET);
		buttonClear.setPosition(width() - buttonScratch.width() * 0.6f, height() - buttonScratch.height() * 0.6f);
		buttonClear.layerAddable().setDepth(21);
		buttonClear.layerAddable().setVisible(false);
		layer.add(buttonClear.layerAddable());
		
		scratchLayer = new ScratchLayer(width(), height() - header.height());
		scratchLayer.setTy(header.height());
		scratchLayer.setAlpha(0);
		scratchLayer.setDepth(20);
		layer.add(scratchLayer.layerAddable());
		
		buttonScratch.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					scratchMode = !scratchMode;
					Tutorial.trigger(Trigger.Number_Scratch);
				}
			}
		});
		register(buttonScratch, Tag.Number_Scratch);
		
		buttonClear.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					scratchLayer.clear();
				}
			}
		});
		register(buttonClear, Tag.Number_Clear);
	}
	
	private void createEquation(Renderer renderer) {
		
		ExpressionWriter equation = renderer.getExpressionWriter(problemFormat);
		CanvasImage eqImage = graphics().createImage(equation.width(), equation.height());
		ExpressionWriter.Config config = new ExpressionWriter.Config(Colors.BLACK, Colors.BLACK, themeColor);
		equation.drawExpression(eqImage.canvas(), config);
		blankCenter = equation.blankCenter();
		
		ImageLayer eqLayer = graphics().createImageLayer(eqImage);
		eqLayer.setTranslation((width() - eqLayer.width()) / 2, (header.height() - eqLayer.height()) / 2); 
		
		backImageOk = PlayN.assets().getImage(Constant.BUTTON_OK);
		backImageBack = PlayN.assets().getImage(Constant.BUTTON_BACK);
		backImageCancel = PlayN.assets().getImage(Constant.BUTTON_CANCEL);
		buttonBack = header.addLeftButton(backImageBack);
		buttonBack.setNoSound();
		register(buttonBack, Tag.Number_Ok);
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					if (selectedPoint != null && !hasCorrectAnswer()) {
						buttonBack.setImage(backImageCancel);
						Audio.se().play(Constant.SE_BACK);
						madeMistake = true;
					} else {
						popThis();
					}
				}
			}
		});
		 
		buttonCenter = header.addRightButton(Constant.BUTTON_CENTER);
		buttonCenter.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) selectedPoint = new Point(recenterPoint);
			}
		});
		
		equationLayer = graphics().createGroupLayer();
		equationLayer.add(header.layerAddable());
		equationLayer.add(eqLayer);
		equationLayer.add(buttonBack.layerAddable());
		equationLayer.add(buttonCenter.layerAddable());
		
		blankCenter.x += eqLayer.tx();
		blankCenter.y += eqLayer.ty();
		
		layer.add(equationLayer);
	}
	
	@Override
	protected void popThis() {
		super.popThis();
		if (hasCorrectAnswer()) {
			if (answer != ANY_ANSWER) {
				Audio.se().play(Constant.SE_SUCCESS);
			} else {
				Audio.se().play(Constant.SE_OK);
			}
		} else {
			Audio.se().play(Constant.SE_BACK);
		}
	}
	
	private void updateEquationAnswer() {
		if (selectedPoint == null && equationAnswer != null) {
			equationLayer.remove(equationAnswer);
			equationAnswer = null;
			equationAnswerPoint = null;
			buttonBack.setImage(backImageBack);
		} else if (selectedPoint != null && !selectedPoint.equals(equationAnswerPoint)) {
			equationAnswerPoint = new Point(selectedPoint);
			if (equationAnswer != null) equationLayer.remove(equationAnswer);
			
			String text = "" + getNumber(selectedPoint);
			CanvasImage image = CanvasUtils.createText(text, problemFormat, Colors.BLACK);
			equationAnswer = graphics().createImageLayer(image);
			equationAnswer.setOrigin(equationAnswer.width() / 2, equationAnswer.height() / 2);
			equationAnswer.setTranslation(blankCenter.x, blankCenter.y);
			equationAnswer.setDepth(1);
			equationLayer.add(equationAnswer);
			buttonBack.setImage(backImageOk);
		}
	}
	
	private void createBackground() {
		backgroundLayer.setDepth(-100);
		
		CanvasImage circleImage = CanvasUtils.createCircle(SPACING / 2, 
				Color.argb(0, 0, 0, 0), 20, Colors.WHITE);
		ImageLayer circle = graphics().createImageLayer(circleImage);
		circle.setOrigin(circleImage.width() / 2, circleImage.height() / 2);
		circle.setTranslation(width() / 2, height() / 2);
		circle.setAlpha(0.7f);
		circle.setDepth(0);
		
		backgroundLayer.add(circle);
		layer.add(backgroundLayer);
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		
		int left = (int)((position.x - width() / 2) / SPACING - 0.5);
		int right = (int)((position.x + width() / 2) / SPACING + 0.5);
		int top = (int)((position.y - height() / 2) / SPACING - 0.5);
		int bot = (int)((position.y + height() / 2) / SPACING + 0.5);
		
		updateEquationAnswer();
		
		createsSpritesThisFrame = 0;
		Point p = new Point();
		for (int i = left; i <= right; i++) {
			for (int j = top; j <= bot; j++) {
				p.setLocation(i, j);
				createNumberSprite(p);
			}
		}
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);

		state.background().scroll(lastPosition.x - position.x, lastPosition.y - position.y);
		lastPosition.set(position);
		
		updateEquationAnswer();
		
		selectedNumberLayer.setVisible(false);
		for (int i = 0; i < numberImages.size(); i++) {
			NumberLayer layer = numberImages.get(i);
			Point p = numberPoints.get(i);
			float dx = position.x - p.x * SPACING;
			float dy = position.y - p.y * SPACING;
			float distance = FloatMath.sqrt(dx * dx + dy * dy);
			float alpha = 1 - Math.min(distance / SPACING / 3.5f, 1);
			if (p.equals(selectedPoint)) {
				selectedNumberLayer.setNumber(layer.number());
				selectedNumberLayer.setTranslation(layer.tx(), layer.ty());
				selectedNumberLayer.setOrigin(layer.width() / 2, layer.height() / 2);
				selectedNumberLayer.setVisible(true);
				layer.setVisible(false);
			} else {
				layer.setVisible(true);
			}
//			layer.setAlpha(PlayNObject.lerpTime(preAlpha, alpha, 0.995f, clock.dt()));
			layer.setAlpha(alpha);
		}
		
		if (selectedPoint == null) {
			position.x += velocity.x * clock.dt();
			position.y += velocity.y * clock.dt();
			velocity.x *= Math.pow(0.995, clock.dt());
			velocity.y *= Math.pow(0.995, clock.dt());
		} else {
			PlayNObject.lerpTime(position, selectedPoint.x * SPACING, selectedPoint.y * SPACING, 
					0.99f, clock.dt());
		}
		foregroundLayer.setTranslation(-position.x, -position.y);
		
		float targetScratchAlpha = scratchMode ? 1 : 0;
		scratchLayer.setAlpha(PlayNObject.lerpTime(scratchLayer.alpha(), targetScratchAlpha, 0.99f, clock.dt(), 0.01f));
		scratchLayer.setVisible(scratchLayer.alpha() > 0);
//		buttonClear.layerAddable().setAlpha(scratchLayer.alpha() * Button.UNPRESSED_ALPHA);
		buttonClear.layerAddable().setVisible(scratchMode);
	}
	
	private NumberLayer createNumberSprite(Point p) {
		int index = numberPoints.indexOf(p);
		if (index >= 0) {
			NumberLayer layer = numberImages.remove(index);
			numberImages.add(layer);
			Point point = numberPoints.remove(index);
			numberPoints.add(point);
			return layer;
		}

		if (createsSpritesThisFrame == MAX_SPRITE_CREATE_PER_FRAME) {
			return null;
		}
		createsSpritesThisFrame++;

		p = p.clone();

		final NumberLayer layer = new NumberLayer(bitmapFont);
		layer.setNumber(getNumber(p));
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
		layer.setTranslation(p.x * SPACING, p.y * SPACING);
		layer.addListener(new NumberListener(p));
		layer.setAlpha(0);
		layer.setHitTester(new HitTester() {
			@Override
			public Layer hitTest(Layer l, pythagoras.f.Point p) {
				if (p.distance(layer.width() / 2, layer.height() / 2) < SPACING / 2.5f) return l;
				return null;
			}
		});
		foregroundLayer.add(layer.layerAddable());
		numberImages.add(layer);
		numberPoints.add(p);
		
		if (numberImages.size() > MAX_NUMS) {
			NumberLayer rem = numberImages.remove(0);
			numberPoints.remove(0);
			rem.destroy();
		}
		
		return layer;
	}
	
	private int getNumber(Point p) {
		return p.x - p.y * 10;
	}
	
	private Point getPoint(int number) {
		if (number >= 0) {
			return new Point(number % 10, -number / 10);
		} else {
			return new Point(number % 10, -number / 10);
		}
	}

	private boolean dragging;
	@Override
	public void onPointerStart(Event event) {
		if (MainMenuLayer.showing()) return;
		
		if (buttonScratch.hit(event.x(), event.y()) || scratchMode) {
			return;
		}
		
		dragging = true;
		dragOffset.set(position.x + event.x(), position.y + event.y());
		positionTrail.clear();
		timeTrail.clear();
		velocity.set(0, 0);
	}

	@Override
	public void onPointerEnd(Event event) {
		if (MainMenuLayer.showing()) return;
		if (!dragging) return;
		dragging = false;
		if (positionTrail.size() > 1) {
			Vector last = positionTrail.get(0);
			double lastTime = timeTrail.get(0);
			
			float dx = (position.x - last.x) / (float)(event.time() - lastTime);
			float dy = (position.y - last.y) / (float)(event.time() - lastTime);
			velocity.set(dx, dy);
			if (velocity.length() > 0.5f) {
				possibleSelectedPoint = null;
				selectedPoint = null;
			}
		}
	}

	private final static int MAX_TRAIL = 10;
	
	@Override
	public void onPointerDrag(Event event) {
		if (MainMenuLayer.showing()) dragging = false;
		if (!dragging) return;
		position.set(-event.x() + dragOffset.x, -event.y() + dragOffset.y);
		positionTrail.add(position.clone());
		timeTrail.add(event.time());
		if (positionTrail.size() > MAX_TRAIL) {
			positionTrail.remove(0);
			timeTrail.remove(0);
			selectedPoint = null;
		}
	}

	@Override
	public void onPointerCancel(Event event) {
		
	}
	
	private class NumberListener implements Listener {

		private Point point;
		
		public NumberListener(Point point) {
			this.point = point;
		}
		
		@Override
		public void onPointerStart(Event event) {
			event.flags().setPropagationStopped(false);
			if ((event.y() < header.height() && 
					Math.abs(event.x() / width() - 0.5f) > 0.25f)) {
				return;
			}
			possibleSelectedPoint = point;
		}

		@Override
		public void onPointerEnd(Event event) { 
			if (point.equals(possibleSelectedPoint)) {
				selectedPoint = point;
				Audio.se().play(Constant.SE_TICK);
				Tutorial.trigger(Trigger.Number_NumberSelected);
			}
		}

		@Override
		public void onPointerDrag(Event event) { }

		@Override
		public void onPointerCancel(Event event) { }
	}
}
