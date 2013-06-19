package tuxkids.tuxblocks.core.defense.select;

import java.util.ArrayList;
import java.util.List;

import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.Inventory;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.SolveScene;
import tuxkids.tuxblocks.core.solve.expression.Equation;
import tuxkids.tuxblocks.core.solve.expression.EquationGenerator;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class SelectScreen extends GameScreen {

	private Grid grid;
	private GroupLayer gridHolder;
	private GroupLayer problemLayer;
	private ProblemButton selectedProblem;
	private List<ProblemButton> problemButtons = new ArrayList<ProblemButton>();
	
	public SelectScreen(final ScreenStack screens, GameState gameState, Grid grid) {
		super(screens, gameState);
		this.grid = grid;
		
		Button button = new Button(PlayN.assets().getImage(Constant.BUTTON_FORWARD), 
				defaultButtonSize(), defaultButtonSize(), true);
		button.setTint(Colors.BLACK);
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
		
		gridHolder = graphics().createGroupLayer();
		gridHolder.setOrigin(grid.width(), grid.height());
		gridHolder.setScale(0.25f);
		gridHolder.setTranslation(width(), height());
		layer.add(gridHolder);
		
		ImageLayer bg = graphics().createImageLayer(CanvasUtils.createRect(width(), height(), Colors.WHITE));
		bg.setDepth(-10);
		layer.add(bg);
		
		problemLayer = graphics().createGroupLayer();
		layer.add(problemLayer);
		createProblems();
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
				pb.setTint(Colors.WHITE, Colors.LIGHT_GRAY);
				pb.setOnReleasedListener(new OnReleasedListener() {
					@Override
					public void onRelease(Event event, boolean inButton) {
						if (inButton) {
							selectedProblem = pb;
							pushScreen(new SolveScene(screens, state, pb.equation()), screens.slide().down());
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
		gridHolder.add(grid.getLayer());
		grid.fadeIn(0.8f);
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
	}
	
	@Override
	protected void onChildScreenFinished(GameScreen screen) {
		super.onChildScreenFinished(screen);
		if (screen instanceof SolveScene) {
			if (((SolveScene) screen).solved()) {
				solveProblem(selectedProblem);
			} else {
				selectedProblem.setEquation(((SolveScene) screen).equation());
			}
		}
	}

}
