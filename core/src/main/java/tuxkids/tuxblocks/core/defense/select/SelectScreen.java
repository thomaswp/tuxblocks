package tuxkids.tuxblocks.core.defense.select;

import playn.core.GroupLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.Debug;

public class SelectScreen extends GameScreen {

	private Grid grid;
	private GroupLayer gridHolder;
	
	public SelectScreen(ScreenStack screens, Grid grid) {
		super(screens);
		this.grid = grid;
		
		Button button = new Button(PlayN.assets().getImage("images/forward.png"), 100, 100, true);
		button.setTint(Colors.RED);
		button.layer().setDepth(1);
		layer.add(button.layer());
		
		gridHolder = graphics().createGroupLayer();
		gridHolder.setOrigin(grid.width(), grid.height());
		gridHolder.setScale(0.25f);
		gridHolder.setTranslation(width(), height());
		layer.add(gridHolder);
		
		button.setPosition(width() - button.width() / 2, button.height() / 2);
		button.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					popThis(SelectScreen.this.screens.slide().left());
				}
			}
		});
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
	}
	

}
