package tuxkids.tuxblocks.core.title;

import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.GameBackgroundSprite;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.screen.BaseScreen;

public class DifficultyScreen extends BaseScreen {

	public DifficultyScreen(ScreenStack screens, GameBackgroundSprite background) {
		super(screens, background);
		
		SlideLayer mathDifficulty = new SlideLayer(width() * 0.9f, 
				height() / 6, 5, background.primaryColor());
		mathDifficulty.centerLayer();
		mathDifficulty.setTranslation(width() / 2, height() / 4);
		layer.add(mathDifficulty.layerAddable());
	}

}
