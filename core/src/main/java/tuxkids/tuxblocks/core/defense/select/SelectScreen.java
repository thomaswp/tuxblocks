package tuxkids.tuxblocks.core.defense.select;

import java.util.ArrayList;
import java.util.List;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.PlayN;
import playn.core.Surface;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.GameState.ProblemAddedListener;
import tuxkids.tuxblocks.core.MenuSprite;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.Inventory;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.SolveScreen;
import tuxkids.tuxblocks.core.solve.expression.Equation;
import tuxkids.tuxblocks.core.solve.expression.EquationGenerator;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.Debug;

public class SelectScreen extends GameScreen implements ProblemAddedListener {

	private final static int COLS = 2;
	
	private Grid grid;
	private GroupLayer gridHolder;
	private GroupLayer problemLayer;
	private ProblemButton selectedProblem;
	private List<ProblemButton> problemButtons = new ArrayList<ProblemButton>();
	private ProblemButton bottomLeft, bottomRight;
	private SolveScreen solveScreen;
	
	public SelectScreen(final ScreenStack screens, GameState gameState, Grid grid) {
		super(screens, gameState);
		this.grid = grid;
		
		Button button = createMenuButton(Constant.BUTTON_FORWARD);
		button.layerAddable().setDepth(1);
		button.setPosition(width() - button.width() * 0.6f, button.height() * 0.6f);
		button.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					popThis();
				}
			}
		});
		layer.add(button.layerAddable());
		
		MenuSprite menu = new MenuSprite(width(), defaultButtonSize() * 1.2f);
		menu.layer().setDepth(-1);
		layer.add(menu.layer());
		
		
		gridHolder = graphics().createGroupLayer();
		gridHolder.setScale(menu.height() / grid.height());
		gridHolder.setTranslation(0, 0);
		gridHolder.setAlpha(0.75f);
		layer.add(gridHolder);
		
//		ImageLayer bg = graphics().createImageLayer(CanvasUtils.createRect(width(), height(), Colors.WHITE));
//		bg.setDepth(-10);
//		layer.add(bg);
		
		problemLayer = graphics().createGroupLayer();
		problemLayer.setTy(menu.height());
		layer.add(problemLayer);
		for (Problem problem : state.problems()) {
			addProblemButton(problem);
		}
		
		solveScreen = new SolveScreen(screens, gameState);

		ImmediateLayer il = graphics().createImmediateLayer(new Renderer() {
			@Override
			public void render(Surface surface) {
				surface.drawLayer(SelectScreen.this.grid.layer());
			}
		});
		gridHolder.add(il);
		
		state.setProblemAddedListener(this);
	}
	
	@Override
	protected void popThis() {
		popThis(screens.slide().left());
	}
	
	private void addProblemButton(Problem problem) {
		int margin = ProblemButton.MARGIN;
		int width = (int)((width() - margin * (COLS + 1)) / COLS);
		int minHeight = (int)(height() / 6);
		
		int col;
		if (bottomLeft == null) {
			col = 0;
		} else if (bottomRight == null) {
			col = 1;
		} else if (bottomLeft.bottom() <= bottomRight.bottom()) {
			col = 0;
		} else {
			col = 1;
		}
		
		ProblemButton above = col == 0 ? bottomLeft : bottomRight;
		float aboveY = above == null ? 0 : above.bottom();
		
		final ProblemButton pb = new ProblemButton(problem, width, minHeight, grid.towerColor());
		problemLayer.add(pb.layerAddable());
		pb.setPosition((col + 0.5f) * width() / COLS, aboveY + margin + pb.height() / 2);
		pb.setTint(Color.withAlpha(Colors.WHITE, 225), Colors.LIGHT_GRAY);
		pb.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					selectedProblem = pb;
					solveScreen.setEquation(pb.equation());
					pushScreen(solveScreen, screens.slide().down());
				}
			}
		});
		pb.fadeIn(1);
		
		if (above != null) {
			above.setBelow(pb);
			pb.setAbove(above);
		}
		if (col == 0) {
			bottomLeft = pb;
		} else {
			bottomRight = pb;
		}
		problemButtons.add(pb);
	}
	
	public void solveProblem(ProblemButton button) {
		state.solveProblem(button.problem());
		button.destroy();
		if (button.above() != null) {
			button.above().setBelow(button.below());
		}
		if (button.below() != null) {
			button.below().setAbove(button.above());
		}
		if (bottomLeft == button) bottomLeft = bottomLeft.above();
		if (bottomRight == button) bottomRight = bottomRight.above();
	}

	@Override
	public void showTransitionCompleted() {
		super.showTransitionCompleted();
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		grid.update(delta);
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		grid.paint(clock);
		
		if (entering()) return;
		for (ProblemButton problem : problemButtons) {
			problem.paint(clock);
		}
		
		if (del && selectedProblem != null && selectedProblem.layerAddable().alpha() < 0.03f) {
			solveProblem(selectedProblem);
			del = false;
		}
	}
	
	boolean del;
	@Override
	protected void onChildScreenFinished(GameScreen screen) {
		super.onChildScreenFinished(screen);
		if (screen instanceof SolveScreen) {
			selectedProblem.setEquation(((SolveScreen) screen).equation());
			del = ((SolveScreen) screen).solved(); 
			if (del) {
				selectedProblem.setEnabled(false);
				selectedProblem.fadeOut();
			}
		}
	}

	@Override
	public void onProblemAdded(Problem problem) {
		addProblemButton(problem);
	}

}
