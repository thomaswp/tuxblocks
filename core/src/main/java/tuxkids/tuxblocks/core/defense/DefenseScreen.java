package tuxkids.tuxblocks.core.defense;

import playn.core.GroupLayer;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.MenuLayer;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.defense.Grid.DoubleClickListener;
import tuxkids.tuxblocks.core.defense.round.Level;
import tuxkids.tuxblocks.core.defense.select.SelectScreen;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.Formatter;

public class DefenseScreen extends GameScreen {

	private Grid grid;
	private Inventory inventory; 
	private GroupLayer gridHolder;
	private SelectScreen selectScreen;
	private GroupLayer layer;
	private boolean zoomed;
	private float maxScale;
	
	public DefenseScreen(ScreenStack screens, GameState gameState) {
		super(screens, gameState);
	}
	
	@Override
	public void wasAdded() {
		super.wasAdded();
		
		layer = graphics().createGroupLayer();
		layer.add(menu.layerAddable());
		super.layer.add(layer);
		
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
		
		grid.setDoubleClickListener(new DoubleClickListener() {
			@Override
			public void wasDoubleClicked() {
				zoomed = !zoomed;
				Tutorial.trigger(Trigger.Defense_GridZoom);
			}
		});
		register(grid, Tag.Defense_Grid);
		register(grid.upgradePanel().buttonUpgrade, Tag.Defense_UpgradeTower);
		register(grid.upgradePanel().buttonDelete, Tag.Defense_DeleteTower);
		
		maxScale = height() / grid.height();
		float cornerX = gridHolder.tx() + grid.width();
		float cornerY = gridHolder.ty() + grid.height();
		cornerX += (width() - grid.width() * maxScale) / 2 / (maxScale - 1);
		cornerY -= (height() - cornerY) / (maxScale - 1);
		layer.setOrigin(cornerX, cornerY);
		layer.setTranslation(cornerX, cornerY);
		
		inventory = new Inventory(this, grid, (int)(width() - testGrid.width()), (int)(height() - titleBarHeight));
		inventory.layer().setDepth(1);
		inventory.layer().setTy(titleBarHeight);
		layer.add(inventory.layer());
		
		selectScreen = new SelectScreen(screens, state, grid);
		
		createPlusButton();
		createStartButton();
		
	}
	
	@Override
	protected Trigger wasShownTrigger() {
		return Trigger.Defense_Shown;
	}
	
	@Override
	protected MenuLayer createMenu() {
		return new DefenseMenu(this, width());
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
		register(buttonPlus, Tag.Defense_MoreTowers);
	}
	
	private void createStartButton() {
		Button buttonStart = menu.addRightButton(Constant.BUTTON_OK);
		buttonStart.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					grid.level().startNextRound();
					Tutorial.trigger(Trigger.Defense_StartRound);
				}
			}
		});
		register(buttonStart, Tag.Defense_StartRound);
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
		
		float scale = zoomed ? maxScale : 1;
		layer.setScale(PlayNObject.lerpTime(layer.scaleX(), scale, 0.99f, clock.dt(), 0.001f));
	}
	
	public void pushSelectScreen() {
		pushScreen(selectScreen, screens.slide().right());
	}
	
	@Override
	protected void popThis() {
		popThis(screens.slide().up());
	}
}
