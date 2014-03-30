package tuxkids.tuxblocks.core.solve;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Keyboard.TypedEvent;
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
import tuxkids.tuxblocks.core.solve.build.BuildScreen;
import tuxkids.tuxblocks.core.solve.markup.ExpressionWriter;
import tuxkids.tuxblocks.core.solve.markup.Renderer;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.HeaderLayer;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.widget.menu.MainMenuLayer;
import tuxkids.tuxblocks.core.widget.menu.MenuLayer;

/**
 * Screen for selecting numbers, usually as the answer to an arithmetic problem.
 * Shown from {@link SolveScreen} and {@link BuildScreen}.
 */
public class NumberSelectScreen extends GameScreen implements Listener {
	
	// spacing between numbers (not actually constant b/c it depends on screen size)
	private int SPACING;
	// max numbers rendered at a given time
	private final static int MAX_NUMS = 70;
	// max number sprites allowed to be created per frame
	private final static int MAX_SPRITE_CREATE_PER_FRAME = 100; // currently no real limit
	// a value for "answer" that indicates any answer is premissible, and there is no "correct" one
	public final static int ANY_ANSWER = Integer.MAX_VALUE;
	
	private TextFormat numberFormat, problemFormat;
	
	// these points correspond to a row and column of numbers and equate with a selected answer
	private Point selectedPoint, possibleSelectedPoint;
	// pan velocity
	private final Vector velocity = new Vector();
	// position and last position of screen's offset
	private final Vector position = new Vector(), lastPosition = new Vector();
	// used to calculate relative movement during a drag
	private final Vector dragOffset = new Vector();
	
	// trail of past positions/times while dragging (used for momentum calculation)
	private final List<Vector> positionTrail = new ArrayList<Vector>();
	private final List<Double> timeTrail = new ArrayList<Double>();
	
	private GroupLayer foregroundLayer, indicatorLayer, headerLayer;
	private int createdSpritesThisFrame;
	private int themeColor;
	private Vector blankCenter;
	// displays the currently selected answer
	private ImageLayer selectedAnswerLayer;
	// the last selected problem answer
	private Point lastSelectedPoint;
	private Renderer problem;
	private int answer;
	private Button buttonBack, buttonCenter, buttonScratch, buttonClear;
	private Image backImageOk, backImageBack, backImageCancel;
	// point to go back to when buttonCenter is pressed
	private Point recenterPoint = new Point();
	// indicates if the player has guessed a wrong answer this showing
	private boolean madeMistake;

	// list of points and corresponding images shown on screen
	private List<Point> numberPoints = new ArrayList<Point>();
	private List<NumberLayer> numberImages = new ArrayList<NumberLayer>();
	// redraws the selected number as colored
	private NumberLayer selectedNumberLayer;
	// BitmapFont used for displaying NumberLayers
	private NumberBitmapFont bitmapFont, bitmapFontColored;
	
	private ScratchLayer scratchLayer;
	private boolean scratchMode;
	
	private boolean primedNegative;
	
	/** Returns the currently selected answer, or null if none is selected */
	public Integer selectedAnswer() {
		if (selectedPoint == null) return null;
		return getNumber(selectedPoint);
	}
	
	/** Returns true if a correct answer is currently selected */
	public boolean hasCorrectAnswer() {
		Integer answer = selectedAnswer();
		return answer != null && (answer == this.answer || this.answer == ANY_ANSWER);
	}
	
	/** Sets the default selected value */
	public void setFocusedNumber(int number) {
		recenterPoint = getPoint(number);
	}
	
	public NumberSelectScreen(ScreenStack screens, GameState gameState, Renderer problem, int answer) {
		super(screens, gameState);
		this.problem = problem;
		this.answer = answer;
	}
	
	/** Returns true if the player has not selected any incorrect answer since this screen was shown */
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
				graphics().createFont(Constant.NUMBER_FONT, Style.PLAIN, SPACING / 3));
		bitmapFont = new NumberBitmapFont(numberFormat, Colors.WHITE);
		bitmapFontColored = new NumberBitmapFont(numberFormat, themeColor);
		
		// adjust problem text size to be smaller if it has more lines
		problemFormat = new TextFormat().withFont(
				graphics().createFont(Constant.NUMBER_FONT, Style.PLAIN, SPACING / 3 * 0.8f / problem.lines()));
		indicatorLayer = graphics().createGroupLayer();
		
		foregroundLayer = graphics().createGroupLayer();
		layer.add(foregroundLayer);
		PlayN.pointer().setListener(this);
		createIndicator();
		createProblem(problem);
		foregroundLayer.setOrigin(-width() / 2, -height() / 2 - header.height() / 2);
		indicatorLayer.setTranslation(0, header.height() / 2);
		
		selectedNumberLayer = new NumberLayer(bitmapFontColored);
		selectedNumberLayer.setDepth(15);
		foregroundLayer.add(selectedNumberLayer.layerAddable());
		
		madeMistake = false;
		
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
	
	// create and position the ScratchLayer and its corresponding Buttons
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
		registerHighlightable(buttonScratch, Tag.Number_Scratch);
		
		buttonClear.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					scratchLayer.clear();
				}
			}
		});
		registerHighlightable(buttonClear, Tag.Number_Clear);
	}
	
	// create the problem to be displayed on the header, and corresponding Buttons
	private void createProblem(Renderer renderer) {
		
		// get the ExpressionWriter to draw the problem
		ExpressionWriter equation = renderer.getExpressionWriter(problemFormat);
		CanvasImage eqImage = graphics().createImage(equation.width(), equation.height());
		ExpressionWriter.Config config = new ExpressionWriter.Config(Colors.BLACK, Colors.BLACK, themeColor);
		equation.drawExpression(eqImage.canvas(), config);
		// find the center of the blank in the expression, where the selected answer will be drawn
		blankCenter = equation.blankCenter();
		
		ImageLayer eqLayer = graphics().createImageLayer(eqImage);
		eqLayer.setTranslation((width() - eqLayer.width()) / 2, (header.height() - eqLayer.height()) / 2); 
		
		backImageOk = PlayN.assets().getImage(Constant.BUTTON_OK);
		backImageBack = PlayN.assets().getImage(Constant.BUTTON_BACK);
		backImageCancel = PlayN.assets().getImage(Constant.BUTTON_CANCEL);
		buttonBack = header.addLeftButton(backImageBack);
		buttonBack.setNoSound();
		registerHighlightable(buttonBack, Tag.Number_Ok);
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					tryAnswer();
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
		
		headerLayer = graphics().createGroupLayer();
		headerLayer.add(header.layerAddable());
		headerLayer.add(eqLayer);
		headerLayer.add(buttonBack.layerAddable());
		headerLayer.add(buttonCenter.layerAddable());
		
		// adjust the blank center to screen coordinates
		blankCenter.x += eqLayer.tx();
		blankCenter.y += eqLayer.ty();
		
		layer.add(headerLayer);
	}
	
	// try to go back, checking the selected answer, if any
	private void tryAnswer() {
		if (selectedPoint != null && !hasCorrectAnswer()) {
			buttonBack.setImage(backImageCancel);
			Audio.se().play(Constant.SE_BACK);
			madeMistake = true;
		} else {
			popThis();
		}
	}
	
	@Override
	protected void popThis() {
		super.popThis();
		// play the correct sound effect
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
		if (selectedPoint == null && selectedAnswerLayer != null) {
			// clear the selected answer in the problem's blank
			headerLayer.remove(selectedAnswerLayer);
			selectedAnswerLayer = null;
			lastSelectedPoint = null;
			buttonBack.setImage(backImageBack);
		} else if (selectedPoint != null && !selectedPoint.equals(lastSelectedPoint)) {
			// if the selected point has changed...
			lastSelectedPoint = new Point(selectedPoint);
			if (selectedAnswerLayer != null) headerLayer.remove(selectedAnswerLayer);

			// redraw the answer in the problem's blank
			String text = "" + getNumber(selectedPoint);
			CanvasImage image = CanvasUtils.createText(text, problemFormat, Colors.BLACK);
			selectedAnswerLayer = graphics().createImageLayer(image);
			selectedAnswerLayer.setOrigin(selectedAnswerLayer.width() / 2, selectedAnswerLayer.height() / 2);
			selectedAnswerLayer.setTranslation(blankCenter.x, blankCenter.y);
			selectedAnswerLayer.setDepth(1);
			headerLayer.add(selectedAnswerLayer);
			buttonBack.setImage(backImageOk);
		}
	}
	
	// the circle, indicating the selected answer
	private void createIndicator() {
		indicatorLayer.setDepth(-100);
		
		CanvasImage circleImage = CanvasUtils.createCircle(SPACING / 2, 
				Color.argb(0, 0, 0, 0), 20, Colors.WHITE);
		ImageLayer circle = graphics().createImageLayer(circleImage);
		circle.setOrigin(circleImage.width() / 2, circleImage.height() / 2);
		circle.setTranslation(width() / 2, height() / 2);
		circle.setAlpha(0.7f);
		circle.setDepth(0);
		
		indicatorLayer.add(circle);
		layer.add(indicatorLayer);
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);

		updateEquationAnswer();
		
		// get the visible numbers
		int left = (int)((position.x - width() / 2) / SPACING - 0.5);
		int right = (int)((position.x + width() / 2) / SPACING + 0.5);
		int top = (int)((position.y - height() / 2) / SPACING - 0.5);
		int bot = (int)((position.y + height() / 2) / SPACING + 0.5);
		
		createdSpritesThisFrame = 0;
		Point p = new Point();
		for (int i = left; i <= right; i++) {
			for (int j = top; j <= bot; j++) {
				// create the sprites (if they don't already exist)
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
		
		selectedNumberLayer.setVisible(false);
		for (int i = 0; i < numberImages.size(); i++) {
			NumberLayer layer = numberImages.get(i);
			Point p = numberPoints.get(i);
			float dx = position.x - p.x * SPACING;
			float dy = position.y - p.y * SPACING;
			float distance = FloatMath.sqrt(dx * dx + dy * dy);
			if (p.equals(selectedPoint)) {
				// hide the selected point and replace it with a colored version
				selectedNumberLayer.setNumber(layer.number());
				selectedNumberLayer.setTranslation(layer.tx(), layer.ty());
				selectedNumberLayer.setOrigin(layer.width() / 2, layer.height() / 2);
				selectedNumberLayer.setVisible(true);
				layer.setVisible(false);
			} else {
				layer.setVisible(true);
			}
			// set alpha based on distance from the center of the screen
			float alpha = 1 - Math.min(distance / SPACING / 3.5f, 1);
			layer.setAlpha(alpha);
		}
		
		if (selectedPoint == null) {
			// friction
			position.x += velocity.x * clock.dt();
			position.y += velocity.y * clock.dt();
			velocity.x *= Math.pow(0.995, clock.dt());
			velocity.y *= Math.pow(0.995, clock.dt());
		} else {
			// snap to selected point
			PlayNObject.lerpTime(position, selectedPoint.x * SPACING, selectedPoint.y * SPACING, 
					0.99f, clock.dt());
		}
		foregroundLayer.setTranslation(-position.x, -position.y);
		
		// show or hide the scratch layer
		float targetScratchAlpha = scratchMode ? 1 : 0;
		scratchLayer.setAlpha(PlayNObject.lerpTime(scratchLayer.alpha(), targetScratchAlpha, 0.99f, clock.dt(), 0.01f));
		scratchLayer.setVisible(scratchLayer.alpha() > 0);
		buttonClear.layerAddable().setVisible(scratchMode);
	}
	
	private NumberLayer createNumberSprite(Point p) {
		int index = numberPoints.indexOf(p);
		if (index >= 0) {
			// if the image is in our list, bring it to the front
			NumberLayer layer = numberImages.remove(index);
			numberImages.add(layer);
			Point point = numberPoints.remove(index);
			numberPoints.add(point);
			return layer;
		}

		if (createdSpritesThisFrame == MAX_SPRITE_CREATE_PER_FRAME) {
			return null;
		}
		createdSpritesThisFrame++;

		p = p.clone();

		final NumberLayer layer = new NumberLayer(bitmapFont);
		layer.setNumber(getNumber(p));
		layer.setOrigin(layer.width() / 2, layer.height() / 2);
		layer.setTranslation(p.x * SPACING, p.y * SPACING);
		layer.addListener(new NumberListener(p));
		layer.setAlpha(0);
		// give it a wider-than-normal hit-tester, so they're easier to click on
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
			// remove the last used number sprites
			NumberLayer rem = numberImages.remove(0);
			numberPoints.remove(0);
			rem.destroy();
		}
		
		return layer;
	}
	
	// the number corresponding to a given row-column in the grid
	private int getNumber(Point p) {
		return p.x - p.y * 10;
	}
	
	// the row-column point associated with a given number
	// (there are more than one correct mappings - this chooses one)
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
		
		// ignore if we're pressing a button or showing the scratch layer
		if (buttonScratch.hit(event.x(), event.y()) || scratchMode) {
			return;
		}
		
		dragging = true;
		primedNegative = false;
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
			// momentum
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

	// allow the player to use keyboard input
	@Override
	public void onKeyTyped(TypedEvent event) {
		super.onKeyTyped(event);
		if (MenuLayer.showing()) return;
		
		Integer s = selectedAnswer();
		int selected = s == null ? 0 : s;
		
		char c = event.typedChar();
		if (c >= '0' && c <= '9') {
			int digit = Integer.parseInt("" + c);
			if (selected < 0 || (selected == 0 && primedNegative)) digit *= -1;
			selected = selected * 10 + digit;
			if (Math.abs(selected) < 1500) {
				selectedPoint = getPoint(selected);
				Tutorial.trigger(Trigger.Number_NumberSelected);
			}
		} else if (c == '-') {
			if (selected != 0) {
				selectedPoint = getPoint(-selected);
			} else {
				primedNegative = true;
			}
			Tutorial.trigger(Trigger.Number_NumberSelected);
		}
	}
	
	@Override
	public void onKeyDown(playn.core.Keyboard.Event event) {
		super.onKeyDown(event);
		if (MenuLayer.showing()) return;
		
		if (event.key() == Key.BACKSPACE || event.key() == Key.DELETE) {
			Integer s = selectedAnswer();
			if (s == null) return;
			int selected = s;
			if (Math.abs(selected) < 10) {
				selectedPoint = null;
			} else {
				selectedPoint = getPoint(selected / 10);
			}
		} else if (event.key() == Key.ENTER) {
			tryAnswer();
		}
	}
	
	// max length of the trail of positions/times
	private final static int MAX_TRAIL = 10;
	
	@Override
	public void onPointerDrag(Event event) {
		if (MainMenuLayer.showing()) dragging = false;
		if (!dragging) return;
		// add to the trail for momenum calculations later
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
	public void onPointerCancel(Event event) { }
	
	// when numbers are clicked
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
			// don't select the point immediately...
			// note it for if this isn't a fast pan
			possibleSelectedPoint = point;
		}

		@Override
		public void onPointerEnd(Event event) { 
			// if panning didn't cancel the selection
			if (point.equals(possibleSelectedPoint)) {
				// select the point
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
