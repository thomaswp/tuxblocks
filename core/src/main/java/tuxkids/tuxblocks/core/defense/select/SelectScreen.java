package tuxkids.tuxblocks.core.defense.select;

import java.util.ArrayList;
import java.util.List;

import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.GameState.ProblemsChangedListener;
import tuxkids.tuxblocks.core.defense.GameHeaderLayer;
import tuxkids.tuxblocks.core.screen.BaseScreen;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.solve.SolveScreen;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.widget.HeaderLayer;

public class SelectScreen extends GameScreen implements ProblemsChangedListener {

	private final static int COLS = 2;
	
	private GroupLayer problemLayer;
	private ProblemButton selectedProblem;
	private List<ProblemButton> problemButtons = new ArrayList<ProblemButton>();
	private ProblemButton bottomLeft, bottomRight;
	private SolveScreen solveScreen;
	
	@Override
	protected int exitTime() {
		return 1000;
	}
	
	public SelectScreen(final ScreenStack screens, GameState gameState) {
		super(screens, gameState);
		
		Button button = header.addRightButton(Constant.BUTTON_FORWARD);
		button.setNoSound();
		register(button, Tag.Select_Return);
		button.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					popThis();
				}
			}
		});		
		
//		ImageLayer bg = graphics().createImageLayer(CanvasUtils.createRect(width(), height(), Colors.WHITE));
//		bg.setDepth(-10);
//		layer.add(bg);
		
		problemLayer = graphics().createGroupLayer();
		problemLayer.setTy(header.height());
		layer.add(problemLayer);
		for (Problem problem : state.problems()) {
			addProblemButton(problem);
		}
		
		solveScreen = new SolveScreen(screens, gameState);
		
		state.setProblemAddedListener(this);
	}
	
	@Override
	public Trigger wasShownTrigger() {
		return Trigger.Select_Shown;
	}
	
	@Override
	public HeaderLayer createHeader() {
		return new GameHeaderLayer(this, width()) {
			@Override
			protected void createWidgets() {
				createAll();
			}
		};
	}
	
	@Override
	protected void popThis() {
		popThis(screens.slide().left());
		Audio.se().play(Constant.SE_BACK);
	}
	
	private void addProblemButton(Problem problem) {
		int margin = ProblemButton.MARGIN;
		int width = (int)((width() - margin * (COLS + 1)) / COLS);
		int minHeight = (int)(height() / 6);
		
		int leftButtons = 0, rightButtons = 0;
		for (ProblemButton button : problemButtons) {
			if (button.enabled()) {
				if (button.x() < width() / 2) {
					leftButtons++;
				} else {
					rightButtons++;
				}
			}
		}
		
		int col = leftButtons <= rightButtons ? 0 : 1;
		ProblemButton above = col == 0 ? bottomLeft : bottomRight;
		float aboveY = above == null ? 0 : above.bottom();
		
		final ProblemButton pb = new ProblemButton(problem, width, minHeight, state.themeColor());
		problemLayer.add(pb.layerAddable());
		pb.setPosition((col + 0.5f) * width() / COLS, aboveY + margin + pb.height() / 2);
		pb.setTint(Colors.WHITE, Colors.LIGHT_GRAY);
		pb.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					selectedProblem = pb;
					solveScreen.setEquation(pb.equation());
					pushScreen(solveScreen, screens.slide().down());
//					removeProblem(pb, true); // for auto-solving
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
		
		if (problemButtons.size() == 0) {
			register(pb, Tag.Select_FirstButton);
		} else if (problemButtons.size() == 1) {
			register(pb, Tag.Select_SecondButton);
		}
		problemButtons.add(pb);
	}
	
	public void removeProblem(ProblemButton button, boolean solve) {
		if (solve) {
			state.solveProblem(button.problem());
			selectedProblem = null;
		}
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
	public void paint(Clock clock) {
		super.paint(clock);
		
		if (entering()) return;
		for (ProblemButton problem : problemButtons) {
			problem.paint(clock);
		}
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		for (int i = 0; i < problemButtons.size(); i++) {
			ProblemButton button = problemButtons.get(i);
			if (button.fadedOut()) {
				removeProblem(button, button == selectedProblem);
				problemButtons.remove(i--);
			}
		}
	}
	
	@Override
	protected void onChildScreenFinished(BaseScreen screen) {
		super.onChildScreenFinished(screen);
		if (screen instanceof SolveScreen) {
			selectedProblem.setEquation(((SolveScreen) screen).equation());
			if (((SolveScreen) screen).solved()) {
				selectedProblem.setEnabled(false);
				selectedProblem.fadeOut();
			}
		}
	}

	@Override
	public void onProblemAdded(Problem problem) {
		addProblemButton(problem);
	}

	@Override
	public void onProblemRemoved(Problem problem) {
		for (ProblemButton button : problemButtons) {
			if (button.problem() == problem) {
				button.setEnabled(false);
				button.fadeOut();
				return;
			}
		}
	}

}
