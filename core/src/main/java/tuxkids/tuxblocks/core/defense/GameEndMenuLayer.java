package tuxkids.tuxblocks.core.defense;

import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Pointer.Event;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.widget.MenuLayer;

public class GameEndMenuLayer extends MenuLayer {

	private static GameEndMenuLayer instance; 
	
	public static void show(boolean victory, Runnable onDismiss) {
		if (instance == null || instance.destroyed()) {
			instance = new GameEndMenuLayer();
		}
		instance.victory = victory;
		instance.onDismiss = onDismiss;
		show(instance);
	}
	
	protected final Image gameOverImage, victoryImage;
	protected final ImageLayer textImageLayer;
	protected final Button continueButton;
	
	protected boolean victory;
	protected Runnable onDismiss;
	protected boolean played;
	
	public GameEndMenuLayer() {
		super(gWidth() * 0.5f, gHeight() * 0.5f);
		
		TextFormat format = createFormat(height * 0.2f);
		gameOverImage = CanvasUtils.createText("Game Over", format, Colors.BLACK);
		victoryImage = CanvasUtils.createText("Victory!", format, Colors.BLACK);
		
		textImageLayer = graphics().createImageLayer();
		textImageLayer.setTranslation(0, -0.2f * height);
		layer.add(textImageLayer);
		
		continueButton = new Button(null, false);
		continueButton.setPosition(0, height * 0.2f);
		layer.add(continueButton.layerAddable());
		createButton(continueButton, width * 0.6f, "Continue", height * 0.15f, new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					hideInstance();
				}
			}
		});
	}
	
	protected void hideInstance() {
		super.hideInstance();
		if (onDismiss != null) {
			onDismiss.run();
		}
	}
	
	protected void showInstance() {
		super.showInstance();
		textImageLayer.setImage(victory ? victoryImage : gameOverImage);
		centerImageLayer(textImageLayer);
		played = false;
		Audio.bg().stop();
	}
	
	
	protected void updateInstance(int delta) {
		super.updateInstance(delta);
		if (!played && alpha() == 1) {
			played = true;
			if (victory) {
				Audio.se().play(Constant.SE_VICTORY);
			} else {
				Audio.se().play(Constant.SE_GAMEOVER);
			}
		}
	}
}
