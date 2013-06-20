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

public class SelectScreen extends GameScreen {

	private Grid grid;
	private GroupLayer gridHolder;
	private GroupLayer problemLayer;
	private ProblemButton selectedProblem;
	private List<ProblemButton> problemButtons = new ArrayList<ProblemButton>();
	private SolveScreen solveScreen;
	
	public SelectScreen(final ScreenStack screens, GameState gameState, Grid grid) {
		super(screens, gameState);
		this.grid = grid;
		
		Button button = createMenuButton(Constant.BUTTON_FORWARD);
		button.layer().setDepth(1);
		button.setPosition(width() - button.width() * 0.6f, button.height() * 0.6f);
		button.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					popThis(SelectScreen.this.screens.slide().left());
				}
			}
		});
		layer.add(button.layer());
		
		MenuSprite menu = new MenuSprite(width(), defaultButtonSize() * 1.2f);
		menu.layer().setDepth(-1);
		layer.add(menu.layer());
		
		
		gridHolder = graphics().createGroupLayer();
		gridHolder.setOrigin(grid.width(), grid.height());
		gridHolder.setScale(0.25f);
		gridHolder.setTranslation(width(), height());
		gridHolder.setAlpha(0.75f);
		layer.add(gridHolder);
		
//		ImageLayer bg = graphics().createImageLayer(CanvasUtils.createRect(width(), height(), Colors.WHITE));
//		bg.setDepth(-10);
//		layer.add(bg);
		
		problemLayer = graphics().createGroupLayer();
		problemLayer.setTy(menu.height());
		layer.add(problemLayer);
		createProblems();
		
		solveScreen = new SolveScreen(screens, gameState);

		ImmediateLayer il = graphics().createImmediateLayer(new Renderer() {
			@Override
			public void render(Surface surface) {
				surface.drawLayer(SelectScreen.this.grid.getLayer());
			}
		});
		gridHolder.add(il);
	}
	
	private void createProblems() {
		List<Problem> problems = state.problems();
		int cols = 2;
		int rows = (problems.size() + 1) / cols;
		int margin = ProblemButton.MARGIN;
		int width = (int)((width() - margin * (cols + 1)) / cols);
		int minHeight = (int)(height() / 6);
		
		for (int col = 0; col < cols; col++) {
			float y = margin;
			ProblemButton lastButton = null;
			for (int row = 0; row < rows; row++) {
				int index = col * rows + row;
				if (index >= problems.size()) return;
				
				final ProblemButton pb = new ProblemButton(state.problems().get(index), width, minHeight);
				problemLayer.add(pb.layer());
				pb.setPosition((col + 0.5f) * width() / cols, y + pb.height() / 2);
				y += pb.height() + margin;
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
				if (lastButton != null) {
					lastButton.setBelow(pb);
					pb.setAbove(lastButton);
				}
				lastButton = pb;
				problemButtons.add(pb);
			}
		}
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
		for (ProblemButton problem : problemButtons) {
			problem.paint(clock);
		}
		
		if (!entering() && del && selectedProblem != null) {
			selectedProblem.layer().setAlpha(PlayNObject.lerpTime(
					selectedProblem.layer().alpha(), 0, 0.995f, clock.dt()));
			if (selectedProblem.layer().alpha() < 0.03f) {
				solveProblem(selectedProblem);
				del = false;
			}
		}
	}
	
	boolean del;
	@Override
	protected void onChildScreenFinished(GameScreen screen) {
		super.onChildScreenFinished(screen);
		if (screen instanceof SolveScreen) {
			selectedProblem.setEquation(((SolveScreen) screen).equation());
			del = ((SolveScreen) screen).solved(); 
			if (del) selectedProblem.setEnabled(false);
		}
	}

}
