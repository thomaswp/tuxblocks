package tuxkids.tuxblocks.core.story;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.Font.Style;
import playn.core.Pointer.Event;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.Lang;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

public class StoryScreen extends GameScreen {

	public StoryScreen(ScreenStack screens, GameState state) {
		super(screens, state);

		
		Button buttonBack = header.addLeftButton(Constant.BUTTON_BACK);
		buttonBack.setNoSound();
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					popThis();
				}
			}
		});
		
		TextFormat optionFormat = new TextFormat().withFont(graphics().createFont(Lang.font(), Style.PLAIN, (int)(height() / 10)));
		
		float midY = (height()) / 2;
		int tintPressed = Colors.WHITE, tintUnpressed = Color.rgb(200, 200, 200);
		
		float size = (height() - header.height()) / 1.8f;
		CanvasImage modeImage = CanvasUtils.createRoundRect(size, size, size / 10, Color.argb(0, 255, 255, 255), size / 10, Colors.WHITE);
		
		float buttonTextMaxWidth = size * 0.7f;
		
		Button startButton = new Button(modeImage, false);
		startButton.setPosition(width() / 2, midY);
		startButton.setTint(tintPressed, tintUnpressed);
		registerHighlightable(startButton, Tag.Title_Play);
		layer.add(startButton.layerAddable());
		
		ImageLayer startText = graphics().createImageLayer();
		startText.setImage(CanvasUtils.createText(getString("story"), optionFormat, Colors.WHITE));
		startText.setTranslation(startButton.x(), startButton.y());
		if (startText.width() > buttonTextMaxWidth) startText.setScale(buttonTextMaxWidth / startText.width());
		PlayNObject.centerImageLayer(startText);
		layer.add(startText);
		
		
		startButton.setOnReleasedListener(new OnReleasedListener() {
			
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					Tutorial.start(background.primaryColor(), background.secondaryColor());
					
					
					
				}
			}
		});
	}
	
	@Override
	protected void popThis() {
		Audio.se().play(Constant.SE_BACK);
		popThis(screens.slide().right());
	}

}
