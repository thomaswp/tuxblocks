package tuxkids.tuxblocks.core.solve;

import static playn.core.PlayN.graphics;

import java.util.ArrayList;
import java.util.List;

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
	private List<BaseBlock> baseBlocks = new ArrayList<BaseBlock>(),
			leftBaseBlocks = new ArrayList<BaseBlock>(), rightBaseBlocks = new ArrayList<BaseBlock>();
	private BaseBlock draggingFrom, highlight;
	private boolean flipModifierPreview;
	private ModifierBlock dragging;
	private Point dragOffset = new Point();
	private EquationSprite equationSprite;
	private BaseBlock simplyfyResult;
	private Button buttonBack;
	private Image buttonImageOk, buttonImageBack;
	private MenuSprite menu;
	private float equalsX;
	
	public void setEquation(Equation equation) {
		this.startEquation = equation;
	}
	
	public boolean solved() {
		if (dragging != null) return false;
		for (BaseBlock baseBlock : baseBlocks) if (baseBlock.hasModifier()) return false;
		return true;
	}

	public Equation equation() {
		return new Equation(leftBaseBlocks.get(0).topLevelExpression(), rightBaseBlocks.get(0).topLevelExpression(), 
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
		
		menu = new MenuSprite(width(), defaultButtonSize() * 1.2f);
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

		BaseBlock leftHandSide = Block.createBlock(startEquation.leftHandSide());
		BaseBlock  rightHandSide = Block.createBlock(startEquation.rightHandSide());
		
		float blockHeight = Math.max(leftHandSide.groupHeight(), rightHandSide.groupHeight());
		
		leftHandSide.layer().setTy((graphics().height() + blockHeight + menu.height()) / 2);
		leftHandSide.layer().setTx(graphics().width() / 4 - leftHandSide.groupWidth() / 2);
		layer.add(leftHandSide.layer());
		if (leftHandSide.hasModifier())	leftHandSide.lastModifier().layer().addListener(this);
		leftHandSide.setSimplifyListener(this);
		
		rightHandSide.layer().setTy((graphics().height() + blockHeight + menu.height()) / 2);
		rightHandSide.layer().setTx(3 * graphics().width() / 4 - rightHandSide.groupWidth() / 2);
		layer.add(rightHandSide.layer());
		if (rightHandSide.hasModifier()) rightHandSide.lastModifier().layer().addListener(this);
		rightHandSide.setSimplifyListener(this);
		
		BaseBlock number = BaseBlock.createBlock(new Number(3));
		number.layer().setTy((graphics().height() + blockHeight + menu.height()) / 2);
		number.layer().setTx(graphics().width() / 2 - number.groupWidth() / 2);
		layer.add(number.layer());
		if (number.hasModifier()) number.lastModifier().layer().addListener(this);
		number.setSimplifyListener(this);
		
		baseBlocks.add(leftHandSide);
		leftBaseBlocks.add(leftHandSide);
		baseBlocks.add(number);
		leftBaseBlocks.add(number);
		baseBlocks.add(rightHandSide);
		rightBaseBlocks.add(rightHandSide);
		
		equalsX = width() * 2 / 3;
		
		equationSprite = new EquationSprite(leftHandSide, rightHandSide);
		refreshEquationSprite();
	}
	
	@Override
	public void wasRemoved() {
		super.wasRemoved();
		equationSprite.layer().destroy();
		for (BaseBlock baseBlock : baseBlocks) baseBlock.layer().destroy();
	}

	private void refreshEquationSprite() {
		equationSprite.refresh(dragging, highlight, flipModifierPreview);
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
			for (BaseBlock baseBlock : baseBlocks) {
				if (baseBlock.hasModifier() &&
						baseBlock.lastModifier().layer() == event.hit()) {
					draggingFrom = baseBlock;
					break;
				}
			}
			if (draggingFrom != null) {
				dragging = draggingFrom.pop();
				if (draggingFrom.hasModifier()) {
					draggingFrom.lastModifier().layer().addListener(this);
				}
				dragOffset.set(
						draggingFrom.layer().tx() + dragging.layer().tx() - event.x(), 
						draggingFrom.layer().ty() + dragging.layer().ty() - event.y());
				dragging.layer().setTranslation(event.x() + dragOffset.x, event.y() + dragOffset.y);
				layer.add(dragging.layer());
				dragging.layer().setDepth(2);
				refreshEquationSprite();
			}
		}
	}

	@Override
	public void onPointerEnd(Event event) {
		if (dragging != null) {
			for (BaseBlock baseBlock : baseBlocks) {
				baseBlock.stopShowingPreview();
			}

			layer.remove(dragging.layer());
			
			BaseBlock dragStop = highlight;
			if (flipModifierPreview) {
				dragStop.addModifier(dragging.getModifier());
			} else {
				dragStop.addModifier(dragging.getOriginalModifier());
			}
			dragStop.lastModifier().layer().addListener(this);
			dragging = null;
			refreshEquationSprite();
		}
		dragging = null;
	}

	@Override
	public void onPointerDrag(Event event) {
		if (dragging != null) {
			dragging.layer().setTranslation(
					event.x() + dragOffset.x,
					event.y() + dragOffset.y);

			boolean invert = (draggingFrom.layer().tx() < equalsX) != (event.x() < equalsX);
			dragging.setInverted(invert);
			refreshEquationSprite();
			
			float blockCX = dragging.layer().tx() + dragging.width() / 2;
			float blockCY = dragging.layer().ty() + dragging.height() / 2;
			
			highlight = draggingFrom;
			flipModifierPreview = false;
			for (BaseBlock baseBlock : baseBlocks) {
				baseBlock.updateShowPreview(blockCX, blockCY, 
						dragging.getModifier());
				if (baseBlock != draggingFrom && baseBlock.isShowingPreview()) {
					highlight = baseBlock;
					flipModifierPreview = true;
				}
			}
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
