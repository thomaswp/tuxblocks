package tuxkids.tuxblocks.core.solve;

import java.util.ArrayList;
import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.Keyboard.TypedEvent;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.util.Callback;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.ColorUtils;
import tuxkids.tuxblocks.core.utils.Sobol;

public class NumberSelectScreen extends GameScreen implements Listener {

	public final static String PLACEHOLDER = "%";
	
	private int SPACING = 150;
	private final static int MAX_NUMS = 70;
	private final static int RECT_INTERVAL = 300;
	private final static int MAX_RECTS = 30;
	private final static int MAX_SPRITE_CREATE_PER_FRAME = 10;
	
	private List<Point> numberPoints = new ArrayList<Point>();
	private List<ImageLayer> numberImages = new ArrayList<ImageLayer>();
	private TextFormat textFormat, equationFormat;
	private Point selectedPoint, possibleSelectedPoint;
	private Vector velocity = new Vector();
	private Vector position = new Vector();
	private Vector dragOffset = new Vector();
	private List<Vector> positionTrail = new ArrayList<Vector>();
	private List<Double> timeTrail = new ArrayList<Double>();
	
	private GroupLayer foregroundLayer, backgroundLayer, equationLayer;
	private List<BackgroundSprite> backgroundSprites = 
			new ArrayList<BackgroundSprite>();
	private int createsSpritesThisFrame;
	private int rectTimer;
	private int backgroundPrimaryColor;
	private float backgroundPrimaryHue;
	private float equationHeight, equationBlankX;
	private ImageLayer equationAnswer;
	private Point equationAnswerPoint;
	private String expression;
	private int answer;
	private Button buttonBack, buttonCenter;
	private Image backImageOk, backImageBack, backImageCancel;
	private Point recenterPoint = new Point();
	
	public Integer selectedAnswer() {
		if (selectedPoint == null) return null;
		return getNumber(selectedPoint);
	}
	
	public void setFocusedNumber(int number) {
		recenterPoint = getPoint(number);
	}
	
	public NumberSelectScreen(ScreenStack screens, GameState gameState, String expression, int answer) {
		super(screens, gameState);
		this.expression = expression;
		this.answer = answer;
	}

	@Override
	public void wasAdded() {
		SPACING = (int)(height() / 3.5f);
		position.set(recenterPoint.x * SPACING, recenterPoint.y * SPACING);
		textFormat = new TextFormat().withFont(
				graphics().createFont(Constant.FONT_NAME, Style.PLAIN, SPACING / 3));
		equationFormat = new TextFormat().withFont(
				graphics().createFont(Constant.FONT_NAME, Style.PLAIN, SPACING / 3 * 0.8f)); //32));
		backgroundLayer = graphics().createGroupLayer();
		
		foregroundLayer = graphics().createGroupLayer();
		layer.add(foregroundLayer);
		PlayN.pointer().setListener(this);
		createBackground();
		createEquation(expression);
		foregroundLayer.setOrigin(-width() / 2, -height() / 2 - equationHeight / 2);
		backgroundLayer.setTranslation(0, equationHeight / 2);
		update(0);
	}
	
	private boolean transitionCompleted() {
		return layer.tx() == 0;
	}
	
	private void createEquation(String equation) {
		int index = equation.indexOf(PLACEHOLDER);
		String before = equation.substring(0, index);
		String after = equation.substring(index + 1);

		TextLayout sampleNumber = graphics().layoutText("-999", equationFormat);

		int boxSpace = 10;
		float boxWidth = sampleNumber.width() + boxSpace;
		float boxHeight = (sampleNumber.height() + boxSpace);
		boxHeight = (boxHeight + boxWidth) / 2;
		float width = 0, height = 0;
		TextLayout beforeLayout = null, afterLayout = null;
		
		if (!before.isEmpty()) {
			beforeLayout = graphics().layoutText(before, equationFormat);
			width += beforeLayout.width() + boxSpace;
			height = Math.max(beforeLayout.height(), height);
		}
		if (!after.isEmpty()) {
			afterLayout = graphics().layoutText(after, equationFormat);
			width += afterLayout.width() + boxSpace;
			height = Math.max(afterLayout.height(), height);
		}
		width += boxWidth + boxSpace * 2;
		height = Math.max(boxHeight, height);
		
		CanvasImage eqImage = graphics().createImage(width, height);
		Canvas canvas = eqImage.canvas();
		float x = 0;

		canvas.setFillColor(Colors.BLACK);
		if (beforeLayout != null) {
			canvas.fillText(beforeLayout, x, (height - beforeLayout.height()) / 2);
			x += beforeLayout.width() + boxSpace;
		}
		
		canvas.setFillColor(Color.withAlpha(backgroundPrimaryColor, 100));
		canvas.setStrokeColor(backgroundPrimaryColor);
		canvas.fillRect(x, (height - boxHeight) / 2, boxWidth, boxHeight);
		canvas.strokeRect(x, (height - boxHeight) / 2, 
				boxWidth - 0.5f, boxHeight - 0.5f);
		equationBlankX = x + boxWidth / 2;
		x += boxWidth + boxSpace;

		canvas.setFillColor(Colors.BLACK);
		if (afterLayout != null) {
			canvas.fillText(afterLayout, x, (height - afterLayout.height()) / 2);
		}
		
		ImageLayer eqLayer = graphics().createImageLayer(eqImage);
		
		CanvasImage screenImage = CanvasUtils.createRect(width(), eqLayer.height() * 1.2f,
				Colors.LIGHT_GRAY, 1, Colors.DARK_GRAY);
		ImageLayer screenLayer = graphics().createImageLayer(screenImage);
		eqLayer.setTranslation((screenLayer.width() - eqLayer.width()) / 2,
				(screenLayer.height() - eqLayer.height()) / 2);
		screenLayer.setAlpha(0.75f);
		
		backImageOk = PlayN.assets().getImage("images/ok.png");
		backImageBack = PlayN.assets().getImage("images/back.png");
		backImageCancel = PlayN.assets().getImage("images/cancel.png");
		float bgHeight = screenImage.height();
		float buttonHeight =  bgHeight * 0.8f;
		buttonBack = new Button(backImageBack, buttonHeight, buttonHeight, true);
		buttonBack.setPosition(buttonBack.width() / 2 + 10, bgHeight / 2);
		buttonBack.setTint(backgroundPrimaryColor, Button.UNPRESSED_ALPHA);
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					if (selectedPoint != null && getNumber(selectedPoint) != answer) {
						buttonBack.setImage(backImageCancel);
					} else {
						popThis();
					}
				}
			}
		});
		 
		buttonCenter = new Button("images/center.png", buttonHeight, buttonHeight, true);
		buttonCenter.setPosition(width() - buttonCenter.width() / 2 - 10, bgHeight / 2);
		buttonCenter.setTint(backgroundPrimaryColor, Button.UNPRESSED_ALPHA);
		buttonCenter.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				selectedPoint = new Point(recenterPoint);
			}
		});
		
		equationLayer = graphics().createGroupLayer();
		equationLayer.add(screenLayer);
		equationLayer.add(eqLayer);
		equationLayer.add(buttonBack.layer());
		equationLayer.add(buttonCenter.layer());
		
		equationHeight = screenLayer.height();
		equationBlankX += eqLayer.tx(); 
		
		layer.add(equationLayer);
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
			CanvasImage image = CanvasUtils.createText(text, equationFormat, Colors.BLACK);
			equationAnswer = graphics().createImageLayer(image);
			equationAnswer.setOrigin(equationAnswer.width() / 2, equationAnswer.height() / 2);
			equationAnswer.setTranslation(equationBlankX, equationHeight / 2);
			equationLayer.add(equationAnswer);
			buttonBack.setImage(backImageOk);
		}
	}

	private void createBackgroundSprite() {
		BackgroundSprite sprite = new BackgroundSprite(backgroundPrimaryHue, 
				(float)backgroundSprites.size() / 20); 
		backgroundSprites.add(sprite);
		backgroundLayer.add(sprite.layer);
	}
	
	private void createBackground() {
		backgroundPrimaryHue = (float)Math.random();
		backgroundPrimaryColor = ColorUtils.hsvToRgb(backgroundPrimaryHue, 1, 1);
		for (int i = 0; i < 0; i++) {
			createBackgroundSprite();
		}
		backgroundLayer.setDepth(-100);
		
//		CanvasImage bgImage = CanvasUtils.createRect(width(), height(), Colors.BLACK);
//		ImageLayer bg = graphics().createImageLayer(bgImage);
//		bg.setDepth(-101);
		
		CanvasImage circleImage = CanvasUtils.createCircle(SPACING / 2, 
				Color.argb(0, 0, 0, 0), 20, Colors.WHITE);
		ImageLayer circle = graphics().createImageLayer(circleImage);
		circle.setOrigin(circleImage.width() / 2, circleImage.height() / 2);
		circle.setTranslation(width() / 2, height() / 2);
		circle.setAlpha(0.7f);
		circle.setDepth(0);
		
		backgroundLayer.add(circle);
//		backgroundLayer.add(bg);
		layer.add(backgroundLayer);
	}
	
	@Override
	public void update(int delta) {
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
				ImageLayer layer = getNumberSprite(p);
				if (layer == null) continue;
				float dx = position.x - p.x * SPACING;
				float dy = position.y - p.y * SPACING;
				float distance = (float)Math.sqrt(dx * dx + dy * dy);
				float alpha = 1 - Math.min(distance / SPACING / 5, 1);
				float preAlpha = layer.alpha();
				if (p.equals(selectedPoint)) {
					layer.setTint(backgroundPrimaryColor);
				} else {
					layer.setTint(Colors.WHITE);
				}
				layer.setAlpha(lerp(preAlpha, alpha, 1 - (float)Math.pow(0.995, delta)));
			}
		}
		
		if (transitionCompleted() && backgroundSprites.size() < MAX_RECTS) {
			rectTimer += delta;
			if (rectTimer >= RECT_INTERVAL) {
				rectTimer -= RECT_INTERVAL;
				createBackgroundSprite();
			}
		}
		for (BackgroundSprite bg : backgroundSprites) {
			bg.update(delta);
		}
	}
	
	@Override
	public void paint(Clock clock) {
		if (selectedPoint == null) {
			position.x += velocity.x * clock.dt();
			position.y += velocity.y * clock.dt();
			velocity.x *= Math.pow(0.995, clock.dt());
			velocity.y *= Math.pow(0.995, clock.dt());
		} else {
			lerp(position, selectedPoint.x * SPACING, selectedPoint.y * SPACING, 
					1 - (float)Math.pow(0.99, clock.dt()));
		}
		foregroundLayer.setTranslation(-position.x, -position.y);
	}
	
	private ImageLayer getNumberSprite(Point p) {
		int index = numberPoints.indexOf(p);
		if (index >= 0) {
			ImageLayer layer = numberImages.remove(index);
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
		
		int border = 0;
		TextLayout layout = PlayN.graphics().layoutText("" + getNumber(p), textFormat);
		CanvasImage image = graphics().createImage(layout.width() + border * 2, 
				layout.height() + border * 2);
		image.canvas().setFillColor(Colors.WHITE);
		image.canvas().fillText(layout, border, border);
		ImageLayer layer = graphics().createImageLayer(image);
		layer.setOrigin(image.width() / 2, image.height() / 2);
		layer.setTranslation(p.x * SPACING, p.y * SPACING);
		layer.addListener(new NumberListener(p));
		layer.setAlpha(0);
		foregroundLayer.add(layer);
		numberImages.add(layer);
		numberPoints.add(p);
		
		if (numberImages.size() > MAX_NUMS) {
			ImageLayer rem = numberImages.remove(0);
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
		if (buttonBack.hit(event.x(), event.y()) ||
				buttonCenter.hit(event.x(), event.y())) {
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
			possibleSelectedPoint = point;
		}

		@Override
		public void onPointerEnd(Event event) { 
			if (point.equals(possibleSelectedPoint)) {
				selectedPoint = point;
			}
		}

		@Override
		public void onPointerDrag(Event event) { }

		@Override
		public void onPointerCancel(Event event) { }
	}
	
	private Sobol sobolD4 = new Sobol(4);
	private class BackgroundSprite {
		private ImageLayer layer;
		private float depth;
		private Vector originalPos;
		private float maxAlpha;
		
		public BackgroundSprite(float hue, float r) {
			double[] point = sobolD4.nextPoint();
			int size = (int)(point[0] * 250 + 50);
			float h = hue;
			if (r < 1f / 3) {
				h = h + 0.7f;
			} else if (r < 2f / 3) {
				h = h + 0.3f;
			}

			float s = (float)Math.random() * 0.5f + 0.5f;
			float v = (float)Math.random() * 0.3f + 0.4f;
			CanvasImage image = CanvasUtils.createRect(size, size, 
					ColorUtils.hsvToRgb(h, s, v), 1, Colors.DARK_GRAY);
			layer = graphics().createImageLayer(image);
			maxAlpha = (float)Math.random() * 0.4f + 0.4f;
			layer.setAlpha(0);
			
			layer.setOrigin(image.width() / 2, image.height() / 2);
			originalPos = new Vector(width() * ((float)point[1] * 1.6f - 0.3f), 
					((float)point[2] * 1.6f - 0.3f) * height());
			layer.setTranslation(originalPos.x, originalPos.y);
			
			depth = (float)point[3] * 15 + 5;
			layer.setDepth(-depth);
		}
		
		public void update(int delta) {
			layer.setTranslation(originalPos.x + foregroundLayer.tx() / depth, 
					originalPos.y + foregroundLayer.ty() / depth);
			layer.setAlpha(lerp(layer.alpha(), maxAlpha, 1 - (float)Math.pow(0.999, delta)));
		}
	}
}
