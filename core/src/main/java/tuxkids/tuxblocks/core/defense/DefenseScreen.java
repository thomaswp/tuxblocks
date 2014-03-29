package tuxkids.tuxblocks.core.defense;

import playn.core.GroupLayer;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.Grid.DoubleClickListener;
import tuxkids.tuxblocks.core.defense.select.SelectScreen;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.widget.HeaderLayer;
import tuxkids.tuxblocks.core.widget.menu.GameEndMenuLayer;
import tuxkids.tuxblocks.core.widget.menu.MainMenuLayer;

/**
 * Screen where players can view the {@link Grid}, add {@link Tower}s,
 * upgrade Towers and watch Rounds unfold.
 */
public class DefenseScreen extends GameScreen {

	private Grid grid;
	private Inventory inventory; // towers available for placement
	private GroupLayer gridHolder; // layer containing the Grid
	private SelectScreen selectScreen;
	// we add to this layer so we can zoom in/out without messing up the
	// internals of the GameScreen
	private GroupLayer layer;
	private boolean zoomed; // zoomed in on the grid
	private float maxScale; // max scale for zooming in
	
	public DefenseScreen(ScreenStack screens, GameState gameState) {
		super(screens, gameState);
	}
	
	@Override
	public void wasAdded() {
		super.wasAdded();
		
		layer = graphics().createGroupLayer();
		layer.add(header.layerAddable());
		super.layer.add(layer);
		
		float titleBarHeight = header.height();
		
		// because each cell of the Grid needs to be a discrete number of pixels,
		// we can only specify the max width and height, and it will have the largest
		// size it can within those parameters, but not necessarily and exact fix for either
		float maxGridWidth = width() * 0.7f; 
		grid = new Grid(state, 15, 21, (int)maxGridWidth, (int)(height() - titleBarHeight));
		gridHolder = graphics().createGroupLayer();
		gridHolder.add(grid.layer());
		gridHolder.setTranslation(width() - grid.width(), (height() + titleBarHeight - grid.height()) / 2);
		gridHolder.setDepth(1);
		layer.add(gridHolder);
		
		state.registerGrid(grid);
		
		// zoom in on double-click
		grid.setDoubleClickListener(new DoubleClickListener() {
			@Override
			public void wasDoubleClicked() {
				zoomed = !zoomed;
				Tutorial.trigger(Trigger.Defense_GridZoom);
			}
		});
		registerHighlightable(grid, Tag.Defense_Grid);
		registerHighlightable(grid.upgradePanel().buttonUpgrade, Tag.Defense_UpgradeTower);
		registerHighlightable(grid.upgradePanel().buttonDelete, Tag.Defense_DeleteTower);
		
		// it's pretty much guaranteed that the height will be bounded before the width
		maxScale = height() / grid.height();
		// set the origin of our layer such that zooming in will center the Grid 
		float cornerX = gridHolder.tx() + grid.width();
		float cornerY = gridHolder.ty() + grid.height();
		cornerX += (width() - grid.width() * maxScale) / 2 / (maxScale - 1);
		cornerY -= (height() - cornerY) / (maxScale - 1);
		layer.setOrigin(cornerX, cornerY);
		layer.setTranslation(cornerX, cornerY);
		
		// add the Inventory
		inventory = new Inventory(this, grid, (int)(width() - grid.width()), (int)(height() - titleBarHeight));
		inventory.layer().setDepth(1);
		inventory.layer().setTy(titleBarHeight);
		layer.add(inventory.layer());
		
		selectScreen = new SelectScreen(screens, state);
		
		createPlusButton();
		createStartButton();
		
	}
	
	@Override
	protected Trigger wasShownTrigger() {
		return Trigger.Defense_ScreenShown;
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
	
	// button for going to the SelectScreen
	private void createPlusButton() {
		Button buttonPlus = header.addLeftButton(Constant.BUTTON_PLUS);
		buttonPlus.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					pushSelectScreen();
				}
			}
		});
		registerHighlightable(buttonPlus, Tag.Defense_EquationSelectScreen);
	}
	
	// button for starting the round early
	private void createStartButton() {
		Button buttonStart = header.addRightButton(Constant.BUTTON_OK);
		buttonStart.setSuccessSound();
		buttonStart.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					state.level().startNextRound();
					Tutorial.trigger(Trigger.Defense_StartRound);
				}
			}
		});
		registerHighlightable(buttonStart, Tag.Defense_StartRound);
	}
	
	@Override
	public void wasShown() {
		super.wasShown();
		inventory.refreshCountSprites();
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		
		grid.update(delta); // grid only updates when this screen is showing
		
		// set button visibility
		boolean duringRound = state.level().duringRound();
		// can't start a Round early if we're in the middle of one already
		header.rightButton().layerAddable().setVisible(!duringRound);
		// nor can we go to another screen 
		header.leftButton().layerAddable().setVisible(
				!duringRound && state.problems().size() > 0);
		
		// win or lose
		if (!exiting() && state.lives() <= 0) {
			GameEndMenuLayer.show(false, new Runnable() {
				@Override
				public void run() {
					popThis(screens.slide().up());
					Audio.bg().play(Constant.BG_MENU);
				}
			});
		} else if (!exiting() && state.level().victory()) {
			GameEndMenuLayer.show(true, new Runnable() {
				@Override
				public void run() {
					popThis(screens.slide().up());
					Audio.bg().play(Constant.BG_MENU);
				}
			});
		}
	}

	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		grid.paint(clock);
		
		// update zoom in/out
		float scale = zoomed ? maxScale : 1;
		layer.setScale(PlayNObject.lerpTime(layer.scaleX(), scale, 0.99f, clock.dt(), 0.001f));
	}
	
	public void pushSelectScreen() {
		pushScreen(selectScreen, screens.slide().right());
	}
	
	@Override
	protected void popThis() {
		MainMenuLayer.show(this);
	}
}
