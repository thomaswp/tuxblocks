package tuxkids.tuxblocks.core.defense;

import static playn.core.PlayN.graphics;

import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.Transform;
import pythagoras.i.Point;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.MenuSprite;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.defense.select.SelectScreen;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.Debug;

public class DefenseScreen extends GameScreen {

	private Grid grid;
	private Inventory inventory; 
	private GroupLayer gridHolder;
	private SelectScreen selectScreen;
	private Button buttonPlus;
	private Button buttonStart;
	private MenuSprite menuSprite;
	
	public DefenseScreen(ScreenStack screens, GameState gameState) {
		super(screens, gameState);
	}
	
	@Override
	public void wasAdded() {
//		background = graphics().createImageLayer(
//				CanvasUtils.createRect(width(), height(), Colors.LIGHT_GRAY));
//		layer.add(background);

		float titleBarHeight = defaultButtonSize() * 1.2f;
		
		float maxGridWidth = width() * 0.7f; 
		grid = new Grid(19, 28, (int)maxGridWidth, (int)(height() - titleBarHeight));
		grid.setTowerColor(state.themeColor());
		gridHolder = graphics().createGroupLayer();
		gridHolder.setTranslation(width() - grid.width(), (height() + titleBarHeight - grid.height()) / 2);
		gridHolder.setDepth(1);
		layer.add(gridHolder);
		addGrid();
		
		menuSprite = new MenuSprite(width(), titleBarHeight);
		layer.add(menuSprite.layer());
		
		inventory = new Inventory(this, grid, (int)(width() - grid.width()), (int)(height() - titleBarHeight));
		inventory.layer().setDepth(1);
		inventory.layer().setTy(titleBarHeight);
		layer.add(inventory.layer());
		
		selectScreen = new SelectScreen(screens, state, grid);
		
		createPlusButton();
		createStartButton();
	}
	
	private void createPlusButton() {
		float size = GameScreen.defaultButtonSize();
		buttonPlus = createMenuButton(Constant.BUTTON_PLUS);
		buttonPlus.setPosition(size * 0.6f, size * 0.6f);
		buttonPlus.addableLayer().setDepth(1);
		layer.add(buttonPlus.addableLayer());
		
		buttonPlus.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) pushSelectScreen();
			}
		});
	}
	
	private void createStartButton() {
		float size = defaultButtonSize();
		buttonStart = createMenuButton(Constant.BUTTON_OK);
		buttonStart.setPosition(width() - size * 0.6f, size * 0.6f);
		buttonStart.addableLayer().setDepth(1);
		layer.add(buttonStart.addableLayer());
		buttonStart.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					buttonStart.addableLayer().setVisible(false);
					state.newThemeColor();
				}
			}
		});
	}
	
	private void addGrid() {
		if (grid.layer().parent() != gridHolder) {
			gridHolder.add(grid.layer());
			grid.fadeIn(1);
		}
	}
	
	@Override
	public void wasShown() {
		super.wasShown();
		inventory.refreshCountSprites();
	}
	
	@Override
	public void showTransitionCompleted() {
		super.showTransitionCompleted();
		addGrid();
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
	}
	
	public void pushSelectScreen() {
		pushScreen(selectScreen, screens.slide().right());
	}
}
