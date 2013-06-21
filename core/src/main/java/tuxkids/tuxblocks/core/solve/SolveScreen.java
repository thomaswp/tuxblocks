package tuxkids.tuxblocks.core.solve;

import static playn.core.PlayN.graphics;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.Point;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.MenuSprite;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock.OnSimplifyListener;
import tuxkids.tuxblocks.core.solve.blocks.Block;
import tuxkids.tuxblocks.core.solve.blocks.ModifierBlock;
import tuxkids.tuxblocks.core.solve.expression.Equation;
import tuxkids.tuxblocks.core.solve.expression.EquationGenerator;
import tuxkids.tuxblocks.core.solve.expression.Expression;
import tuxkids.tuxblocks.core.solve.expression.Number;
import tuxkids.tuxblocks.core.solve.expression.Variable;
import tuxkids.tuxblocks.core.utils.Debug;

public class SolveScreen extends GameScreen implements Listener, OnSimplifyListener {
	
	private Equation startEquation;
	private BaseBlock leftHandSide, rightHandSide;
	private BaseBlock draggingFrom, draggingTo;
	private ModifierBlock dragging;
	private Point dragOffset = new Point();
	private EquationSprite equationSprite;
	private BaseBlock simplyfyResult;
	private Button buttonBack;
	private Image buttonImageOk, buttonImageBack;
	
	public void setEquation(Equation equation) {
		this.startEquation = equation;
	}
	
	public boolean solved() {
		return dragging == null && !leftHandSide.hasModifier() && !rightHandSide.hasModifier();
	}

	public Equation equation() {
		return new Equation(leftHandSide.getTopLevelExpression(), rightHandSide.getTopLevelExpression(), 
				startEquation.answer(), startEquation.difficulty());
	}
	
	public SolveScreen(final ScreenStack screens, GameState gameState) {
		super(screens, gameState);
		
		CanvasImage background = graphics().createImage(graphics().width(), graphics().height());
		background.canvas().setFillColor(Color.rgb(255, 255, 255));
		background.canvas().fillRect(0, 0, graphics().width() / 2, graphics().height());
		background.canvas().setFillColor(Color.rgb(100, 100, 100));
		background.canvas().fillRect(graphics().width() / 2, 0, graphics().width() / 2, graphics().height());
		ImageLayer bg = graphics().createImageLayer(background);
		bg.setDepth(-10);
//		layer.add(bg);
		
		MenuSprite menu = new MenuSprite(width(), defaultButtonSize() * 1.2f);
		menu.layer().setDepth(-1);
		layer.add(menu.layer());

		buttonImageBack = PlayN.assets().getImage(Constant.BUTTON_DOWN);
		buttonImageOk = PlayN.assets().getImage(Constant.BUTTON_OK);
		buttonBack = createMenuButton(Constant.BUTTON_DOWN);
		buttonBack.setPosition(buttonBack.width() * 0.6f, buttonBack.height() * 0.6f);
		buttonBack.addableLayer().setDepth(10);
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) popThis(screens.slide().up());
			}
		});
		layer.add(buttonBack.addableLayer());
	}
	
	@Override
	public void wasAdded() {
		super.wasAdded();

		leftHandSide = Block.createBlock(startEquation.leftHandSide());
		leftHandSide.layer().setTy(graphics().height());
		leftHandSide.layer().setTx(graphics().width() / 4 - leftHandSide.getGroupWidth() / 2);
		layer.add(leftHandSide.layer());
		if (leftHandSide.hasModifier())	leftHandSide.getLastModifier().getSprite().addListener(this);
		leftHandSide.setSimplifyListener(this);
		
		rightHandSide = Block.createBlock(startEquation.rightHandSide());
		rightHandSide.layer().setTy(graphics().height());
		rightHandSide.layer().setTx(3 * graphics().width() / 4 - leftHandSide.getGroupWidth() / 2);
		layer.add(rightHandSide.layer());
		if (rightHandSide.hasModifier()) rightHandSide.getLastModifier().getSprite().addListener(this);
		rightHandSide.setSimplifyListener(this);
		
		equationSprite = new EquationSprite(leftHandSide, rightHandSide);
		refreshEquationSprite();
	}
	
	@Override
	public void wasRemoved() {
		super.wasRemoved();
		equationSprite.layer().destroy();
		leftHandSide.layer().destroy();
		rightHandSide.layer().destroy();
	}

	private void refreshEquationSprite() {
		equationSprite.refresh(dragging, draggingTo, draggingFrom);
		ImageLayer layer = equationSprite.layer();
		this.layer.add(layer);
		layer.setTy(10);
		layer.setTx(graphics().width() / 2);
		buttonBack.setImage(solved() ? buttonImageOk : buttonImageBack);
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
	}

	@Override
	public void paint(Clock clock) {
		super.paint(clock);
	}
	
	@Override
	public void onPointerStart(Event event) {
		if (dragging != null) return;
		dragging = null;
		draggingFrom = null;
		if (event.hit() != null) {
			if (leftHandSide.hasModifier() &&
					leftHandSide.getLastModifier().getSprite() == event.hit()) {
				draggingFrom = leftHandSide;
				draggingTo = rightHandSide;						
			} else if (rightHandSide.hasModifier() &&
					rightHandSide.getLastModifier().getSprite() == event.hit()) {
				draggingFrom = rightHandSide;
				draggingTo = leftHandSide;
			}
			if (draggingFrom != null) {
				dragging = draggingFrom.pop();
				if (draggingFrom.hasModifier()) {
					draggingFrom.getLastModifier().getSprite().addListener(this);
				}
				dragOffset.set(
						draggingFrom.layer().tx() + dragging.getSprite().tx() - event.x(), 
						draggingFrom.layer().ty() + dragging.getSprite().ty() - event.y());
				dragging.getSprite().setTranslation(event.x() + dragOffset.x, event.y() + dragOffset.y);
				layer.add(dragging.getSprite());
				refreshEquationSprite();
			}
		}
	}

	@Override
	public void onPointerEnd(Event event) {
		if (dragging != null) {
			boolean dragTo = draggingTo.isShowingPreview();
			leftHandSide.stopShowingPreview();
			rightHandSide.stopShowingPreview();

			layer.remove(dragging.getSprite());
			
			BaseBlock dragStop;
			if (dragTo) {
				dragStop = draggingTo;
				dragStop.addModifier(dragging.getModifier());
			} else {
				dragStop = draggingFrom;
				dragStop.addModifier(dragging.getOriginalModifier());
			}
			dragStop.getLastModifier().getSprite().addListener(this);
			dragging = null;
			refreshEquationSprite();
		}
		dragging = null;
	}

	@Override
	public void onPointerDrag(Event event) {
		if (dragging != null) {
			dragging.getSprite().setTranslation(
					event.x() + dragOffset.x,
					event.y() + dragOffset.y);
			float distanceX = Math.abs(dragging.getSprite().tx() + dragging.getSprite().width() / 2 - (draggingFrom.layer().tx() + draggingFrom.getGroupWidth() / 2));
			if (!dragging.isInverted() && distanceX > graphics().width() / 4 + 5) {
				dragging.invert();
			} else if (dragging.isInverted() && distanceX < graphics().width() / 4 - 5) {
				dragging.invert();
			}
			refreshEquationSprite();
			
			float blockCX = dragging.getSprite().tx() + dragging.width() / 2;
			float blockCY = dragging.getSprite().ty() + dragging.height() / 2;
			leftHandSide.updateShowPreview(blockCX, blockCY, 
					dragging.getModifier());
			rightHandSide.updateShowPreview(blockCX, blockCY, 
					dragging.getModifier());
		}
	}

	@Override
	public void onPointerCancel(Event event) {

	}

	
	@Override
	public void onSimplify(BaseBlock baseBlock, String expression, int answer, int start) {
		simplyfyResult = baseBlock;
		NumberSelectScreen nss = new NumberSelectScreen(screens, state, expression, answer);
		nss.setFocusedNumber(start);
		pushScreen(nss);
	}
	
	@Override
	protected void onChildScreenFinished(GameScreen screen) {
		super.onChildScreenFinished(screen);
		if (screen instanceof NumberSelectScreen) {
			Integer answer = ((NumberSelectScreen) screen).selectedAnswer();
			if (answer != null) {
				simplyfyResult.simplfy(answer);
				refreshEquationSprite();
			}
		}
	}

}
