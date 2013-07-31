package tuxkids.tuxblocks.core.defense;

import playn.core.GroupLayer;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.MenuSprite;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.defense.round.Level;
import tuxkids.tuxblocks.core.defense.select.SelectScreen;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.Formatter;

public class DefenseScreen extends GameScreen {

	private Grid grid;
	private Inventory inventory; 
	private GroupLayer gridHolder;
	private SelectScreen selectScreen;
	
	public DefenseScreen(ScreenStack screens, GameState gameState) {
		super(screens, gameState);
	}
	
	@Override
	public void wasAdded() {

		float titleBarHeight = menu.height();
		
		float maxGridWidth = width() * 0.7f; 
		Grid testGrid = new Grid(state, 15, 21, (int)maxGridWidth, (int)(height() - titleBarHeight));

		grid = testGrid;//new Grid(state, 15, 21, (int)(testGrid.width() * 1.5f), (int)(testGrid.height() * 1.5f));
		grid.setTowerColor(state.themeColor());
		gridHolder = graphics().createGroupLayer();
		gridHolder.add(grid.layer());
		gridHolder.setTranslation(width() - testGrid.width(), (height() + titleBarHeight - testGrid.height()) / 2);
//		gridHolder.setScale((float)testGrid.width() / grid.width());
		gridHolder.setDepth(1);
		layer.add(gridHolder);
		
		inventory = new Inventory(this, grid, (int)(width() - testGrid.width()), (int)(height() - titleBarHeight));
		inventory.layer().setDepth(1);
		inventory.layer().setTy(titleBarHeight);
		layer.add(inventory.layer());
		
		selectScreen = new SelectScreen(screens, state, grid);
		
		createPlusButton();
		createStartButton();
		
	}
	
	@Override
	protected MenuSprite createMenu() {
		return new DefenseMenu(state, width());
	}
	
	private void createPlusButton() {
		Button buttonPlus = menu.addLeftButton(Constant.BUTTON_PLUS);
		buttonPlus.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					pushSelectScreen();
				}
			}
		});
	}
	
	private void createStartButton() {
		Button buttonStart = menu.addRightButton(Constant.BUTTON_OK);
		buttonStart.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					grid.level().startNextRound();
				}
			}
		});
	}
	
	@Override
	public void wasShown() {
		super.wasShown();
		inventory.refreshCountSprites();
	}
	
	@Override
	public void showTransitionCompleted() {
		super.showTransitionCompleted();
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		grid.update(delta);
		Level level = grid.level();
		menu.rightButton().layerAddable().setVisible(false);
		if (level.finished()) {
//			menuSprite.setText("Level Complete!");
		} if (level.duringRound()) {
//			menuSprite.setText("Round " + level.round());
		} else {
//			int nextRoundIn = grid.level().timeUntilNextRound() / 1000 + 1;
//			menuSprite.setText(Formatter.format("Next round in %d...", nextRoundIn));
			menu.rightButton().layerAddable().setVisible(true);
		}
	}

	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		grid.paint(clock);
	}
	
	public void pushSelectScreen() {
		pushScreen(selectScreen, screens.slide().right());
	}
}
