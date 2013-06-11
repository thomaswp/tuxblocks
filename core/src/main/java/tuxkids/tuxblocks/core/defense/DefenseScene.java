package tuxkids.tuxblocks.core.defense;

import static playn.core.PlayN.graphics;

import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.i.Point;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.Debug;

public class DefenseScene extends GameScreen implements Listener {

	private Grid grid;
	
	public DefenseScene(ScreenStack screens) {
		super(screens);
	}
	
	@Override
	public void wasAdded() {
		grid = new Grid(20, 40, (int)width(), (int)height());
		layer.add(grid.getLayer());
	}

	@Override
	public void update(int delta) {
		grid.update(delta);
	}

	@Override
	public void paint(Clock clock) {
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

}
