package tuxkids.tuxblocks.core.story;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font.Style;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.TextFormat;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.DefenseScreen;
import tuxkids.tuxblocks.core.lang.Lang;
import tuxkids.tuxblocks.core.lang.Strings_Story;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

public class StoryScreen extends GameScreen implements Strings_Story {

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
		
		int tintPressed = Colors.WHITE, tintUnpressed = Color.rgb(200, 200, 200);
		
		float size = (height() - header.height()) / 2.4f;
		CanvasImage modeImage = CanvasUtils.createRoundRect(size, size, size / 10, Color.argb(0, 255, 255, 255), size / 10, Colors.WHITE);
		
		float buttonTextMaxWidth = size * 0.7f;
		
		Button startButton = new Button(modeImage, false);
		startButton.setPosition(startButton.width() * 0.7f, startButton.height() * 0.7f + header.height());
		startButton.setTint(tintPressed, tintUnpressed);
		registerHighlightable(startButton, Tag.Title_Play);
		layer.add(startButton.layerAddable());
		
		ImageLayer startText = graphics().createImageLayer();
		startText.setImage(CanvasUtils.createText(getString(key_story), optionFormat, Colors.WHITE));
		startText.setTranslation(startButton.x(), startButton.y());
		if (startText.width() > buttonTextMaxWidth) startText.setScale(buttonTextMaxWidth / startText.width());
		PlayNObject.centerImageLayer(startText);
		layer.add(startText);
		
		
		startButton.setOnReleasedListener(new OnReleasedListener() {
			
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					
					startStory();					
					
				}
			}
		});
	}
	
	private void startStory() {
		background.newThemeColor(0.2f + 0.6f * (float) Math.random());
		StoryGameState state = new StoryGameState();
		state.setBackground(background);
		Tutorial.start(state);
		
		DefenseScreen ds = new DefenseScreen(screens, state);
		pushScreen(ds, screens.slide().down());
		// remove this screen from the stack - going back should lead to the TitleScreen
		screens.remove(this);
		Audio.bg().play(Constant.BG_GAME1);
	}
	
	
	@Override
	protected void popThis() {
		Audio.se().play(Constant.SE_BACK);
		popThis(screens.slide().right());
	}
	
	@Override
	protected String getScreenName() {
		return "story";
	}

}
