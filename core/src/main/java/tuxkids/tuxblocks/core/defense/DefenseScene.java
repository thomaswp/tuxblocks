package tuxkids.tuxblocks.core.defense;

import static playn.core.PlayN.graphics;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.screen.GameScreen;

public class DefenseScene extends GameScreen implements Listener {

	public DefenseScene(ScreenStack screens) {
		super(screens);
	}
	
	@Override
	public void wasAdded() {
		CanvasImage background = graphics().createImage(graphics().width(), graphics().height());
		background.canvas().setFillColor(Color.rgb(255, 0, 0));
		background.canvas().fillRect(0, 0, graphics().width() / 2, graphics().height());
		background.canvas().setFillColor(Color.rgb(100, 100, 100));
		background.canvas().fillRect(graphics().width() / 2, 0, graphics().width() / 2, graphics().height());
		ImageLayer l;
		layer.add(l = graphics().createImageLayer(background));
		l.addListener(this);
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paint(Clock clock) {
		
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
