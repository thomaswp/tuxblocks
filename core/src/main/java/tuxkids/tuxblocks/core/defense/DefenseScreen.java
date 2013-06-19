package tuxkids.tuxblocks.core.defense;

import static playn.core.PlayN.graphics;

import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.f.Transform;
import pythagoras.i.Point;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.select.SelectScreen;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.Debug;

public class DefenseScreen extends GameScreen implements Listener {

	private Grid grid;
	private Inventory inventory; 
	private ImageLayer background;
	private GroupLayer gridHolder;
	
	public DefenseScreen(ScreenStack screens, GameState gameState) {
		super(screens, gameState);
	}
	
	@Override
	public void wasAdded() {
		background = graphics().createImageLayer(
				CanvasUtils.createRect(width(), height(), Colors.LIGHT_GRAY));
		layer.add(background);

		float maxGridWidth = width() * 0.7f; 
		grid = new Grid(19, 23, (int)maxGridWidth, (int)height());
		gridHolder = graphics().createGroupLayer();
		gridHolder.setTranslation(width() - grid.width(), (height() - grid.height()) / 2);
		gridHolder.setDepth(1);
		layer.add(gridHolder);
		addGrid();
		
		inventory = new Inventory(this, grid, (int)(width() - grid.width()), (int)(height()));
		inventory.layer().setDepth(1);
		layer.add(inventory.layer());
	}
	
	private void addGrid() {
		if (grid.getLayer().parent() != gridHolder) {
			gridHolder.add(grid.getLayer());
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

	@Override
	public void onPointerStart(Event event) {
		screens.remove(this);
	}

	@Override
	public void onPointerEnd(Event event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPointerDrag(Event event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPointerCancel(Event event) {
		// TODO Auto-generated method stub
		
	}
	
	public void pushSelectScreen() {
		pushScreen(new SelectScreen(screens, state, grid), screens.slide().right());
	}

}
