package tuxkids.tuxblocks.core.title;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Platform.Type;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.TextFormat.Alignment;
import playn.core.util.Callback;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.Lang;
import tuxkids.tuxblocks.core.defense.DefenseScreen;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.screen.BaseScreen;
import tuxkids.tuxblocks.core.solve.build.BuildGameState;
import tuxkids.tuxblocks.core.solve.build.BuildScreen;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.HoverUtils;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.utils.persist.PersistUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.widget.GameBackgroundSprite;
import tuxkids.tuxblocks.core.widget.menu.ContinueMenuLayer;
import tuxkids.tuxblocks.core.widget.menu.ContinueMenuLayer.ResponseListener;

/**
 * Screen shown when the game first starts. Gives options to
 * enter Play or Build modes.
 */
public class TitleScreen extends BaseScreen {

	// Time after the title is first show to snap the
	// TitleLayer into place
	private static final int SNAP_TIME = 500;
	
	private int untilSnap;

	private final TitleLayer titleLayer;
	private final GroupLayer fadeInLayer;
	private final GameBackgroundSprite background;


	private TextFormat authorFormat, superFormat, optionFormat;
	private Button tutorialButton;
	private Button muteButton;
	private ImageLayerTintable startHere;
	private LanguageLayer languageLayer;
	
	public TitleScreen(ScreenStack screens, GameBackgroundSprite background) {
		super(screens, background);
		this.background = background;
		
		titleLayer = new TitleLayer(width());
		titleLayer.setDepth(-1);
		layer.add(titleLayer.layerAddable());
		

		languageLayer = new LanguageLayer(background().ternaryColor(), screens);
		
		// most elements fade in after the snap
		fadeInLayer = graphics().createGroupLayer();
		layer.add(fadeInLayer);
		fadeInLayer.setAlpha(0);
		
		titleLayer.image.addCallback(new Callback<Image>() {
			@Override
			public void onSuccess(Image result) {
				setup();
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
		
	}
	
	@Override
	protected String getScreenName() {
		return "title";
	}
	
	public void reload() {
		fadeInLayer.clear();
		fadeInLayer.setAlpha(0);
		setup();
	}
	
	private void setup() {

		// format for authors text
		authorFormat = new TextFormat().withFont(graphics().createFont(
				Lang.font(), Style.PLAIN, (int)(height() / 25)))
				.withAlignment(Alignment.CENTER);
		// format for text about author text (like "by" and "menotred by")
		superFormat = new TextFormat().withFont(graphics().createFont(
				Lang.font(), Style.PLAIN, (int)(height() / 35)))
				.withAlignment(Alignment.CENTER);
		// format for Play and Build buttons
		optionFormat = new TextFormat().withFont(graphics().createFont(
				Lang.font(), Style.PLAIN, (int)(height() / 10)));
		
		languageLayer.setTranslation(width() / 2, height() * 0.97f);
		fadeInLayer.add(languageLayer.layerAddable());
		
		ImageLayer tuxLayer = createTextLayer(getString("a-tux4kids-game"), width() / 5);
		createSuperTextLayer(getString("by"), width() / 2);
		createTextLayer("Thomas Price", width() / 2);
		createSuperTextLayer(getString("mentored-by"), 4 * width() / 5);
		createTextLayer("Aaditya Maheshwari", 4 * width() / 5);
		
		float midY = (height() + titleLayer.height()) / 2 + authorFormat.font.size();
		int tintPressed = Colors.WHITE, tintUnpressed = Color.rgb(200, 200, 200);
		
		final float buttonSize = height() / 6;
		tutorialButton = new Button(Constant.IMAGE_CONFIRM, buttonSize, buttonSize, true);
		tutorialButton.setPosition(width() / 2, midY);
		tutorialButton.setTint(tintPressed, tintUnpressed);
		fadeInLayer.add(tutorialButton.layerAddable());
		muteButton = new Button(Constant.IMAGE_MUTE, buttonSize, buttonSize, true);
		muteButton.setPosition(width()/20, midY);
		muteButton.setTint(tintPressed, tintUnpressed);
		fadeInLayer.add(muteButton.layerAddable());
		startHere = new ImageLayerTintable();
		Lang.getImage(Constant.IMAGE_START_LOCAL, new Callback<Image>() {
			@Override
			public void onSuccess(Image result) {
				startHere.setImage(result);
				startHere.setScale(0.8f * buttonSize / startHere.height());
				startHere.setOrigin(result.width() * 0.8f, result.height());
				startHere.setTranslation(tutorialButton.x() + tutorialButton.width() * 0.5f, 
						tutorialButton.y() - tutorialButton.height() * 0.5f);
				startHere.setTint(background.primaryColor());
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
			}
		});
		fadeInLayer.add(startHere.layerAddable());
		
		float size = (height() - titleLayer.height()) / 1.8f;
		CanvasImage modeImage = CanvasUtils.createRoundRect(size, size, size / 10, Color.argb(0, 255, 255, 255), size / 10, Colors.WHITE);
		
		float buttonTextMaxWidth = size * 0.7f;
		
		Button playButton = new Button(modeImage, false);
		playButton.setPosition(width() / 5, midY);
		playButton.setTint(tintPressed, tintUnpressed);
		registerHighlightable(playButton, Tag.Title_Play);
		fadeInLayer.add(playButton.layerAddable());
		
		ImageLayer playText = graphics().createImageLayer();
		playText.setImage(CanvasUtils.createText(getString("play"), optionFormat, Colors.WHITE));
		playText.setTranslation(playButton.x(), playButton.y());
		if (playText.width() > buttonTextMaxWidth) playText.setScale(buttonTextMaxWidth / playText.width());
		PlayNObject.centerImageLayer(playText);
		fadeInLayer.add(playText);
		
		Button buildButton = new Button(modeImage, false);
		buildButton.setPosition(4 * width() / 5, midY);
		buildButton.setTint(tintPressed, tintUnpressed);
		registerHighlightable(buildButton, Tag.Title_Build);
		fadeInLayer.add(buildButton.layerAddable());
		
		ImageLayer buildText = graphics().createImageLayer();
		buildText.setImage(CanvasUtils.createText(getString("build"), optionFormat, Colors.WHITE));
		buildText.setTranslation(buildButton.x(), buildButton.y());
		if (buildText.width() > buttonTextMaxWidth) buildText.setScale(buttonTextMaxWidth / buildText.width());
		PlayNObject.centerImageLayer(buildText);
		fadeInLayer.add(buildText);

		tutorialButton.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					Tutorial.start(background.primaryColor(), background.secondaryColor());
					tutorialButton.layerAddable().setVisible(false);
					startHere.setVisible(false);
				}
			}
		});
		
		playButton.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					// Give players the chance to continue from last game if it exists
					if (!Tutorial.running() && PersistUtils.stored(Constant.KEY_GAME)) {
						ContinueMenuLayer.show(new ResponseListener() {
							@Override
							public void responded(boolean cont) {
								if (cont) {
									continueGame();
								} else {
									toDifficultyScreen();
								}
							}
						});
					} else {
						// or if you're in the tutorial, just start a new one
						toDifficultyScreen();
					}
				}
			}
		});
		
		buildButton.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					Tutorial.trigger(Trigger.Title_Build);
					GameState state = new BuildGameState();
					state.setBackground(background);
					BuildScreen bs = new BuildScreen(screens, state);
					pushScreen(bs, screens.slide().down());
				}
			}
		});
		
		tuxLayer.addListener(new Listener() {
			@Override
			public void onPointerStart(Event event) { }
			
			@Override
			public void onPointerEnd(Event event) { 
				// open the Tux4Kids website when the Tux4Kids text is clicked
				PlayN.openURL(Constant.TUX_URL);
			}
			
			@Override
			public void onPointerDrag(Event event) { }
			
			@Override
			public void onPointerCancel(Event event) { }
		});
		
		
	}
	
	private void toDifficultyScreen() {
		// start a new game
		Tutorial.trigger(Trigger.Title_Play);
		DifficultyScreen ds = new DifficultyScreen(screens, background);
		pushScreen(ds, screens.slide().left());
	}
	
	private void continueGame() {
		// load the game
		GameState state = PersistUtils.fetch(GameState.class, Constant.KEY_GAME);
		// clear the save file
		PersistUtils.clear(Constant.KEY_GAME);
		if (state == null) {
			// if the load failed, start a new game
			// (this might happen if the fields of the peristed
			// objects changed since the last save, or if the save
			// is corrupted)
			toDifficultyScreen();
			Debug.write("failed to load game!");
			return;
		} 
		
		// otherwise, start the saved game
		state.setBackground(background);
		DefenseScreen ds = new DefenseScreen(screens, state);
		pushScreen(ds, screens.slide().down());
		Audio.bg().play(Constant.BG_GAME1);
	}
	
	private ImageLayer createSuperTextLayer(String text, float x) {
		ImageLayer layer = graphics().createImageLayer(CanvasUtils.createText(text, superFormat, Colors.WHITE));
		layer.setTranslation(x, titleLayer.height() + superFormat.font.size());
		PlayNObject.centerImageLayer(layer);
		fadeInLayer.add(layer);
		return layer;
	}
	
	private ImageLayer createTextLayer(String text, float x) {
		ImageLayer layer = graphics().createImageLayer(CanvasUtils.createText(text, authorFormat, Colors.WHITE));
		layer.setTranslation(x, titleLayer.height() + superFormat.font.size() + authorFormat.font.size());
		PlayNObject.centerImageLayer(layer);
		fadeInLayer.add(layer);
		return layer;
	}

	@Override
	public void wasAdded() {
		super.wasAdded();
		// when it's first added, start the snap timer
		// for the TitleLayer
		untilSnap = SNAP_TIME;
	}
	
	@Override
	public void wasShown() {
		super.wasShown();
		HoverUtils.clear();
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		
		if (untilSnap > 0) {
			untilSnap -= delta;
			if (untilSnap <= 0) {
				untilSnap = 0;
				titleLayer.snap();
			}
		}
		
		titleLayer.update(delta);

		// show the start tutorial button iff it's not running
		if (!Tutorial.running()) {
			if (tutorialButton != null) {
				tutorialButton.layerAddable().setVisible(true);
			}
			if (startHere != null) {
				startHere.setVisible(true);
			}
		}
	}
	
	public void paint(Clock clock) {
		super.paint(clock);
		titleLayer.paint(clock);
		if (untilSnap == 0) {
			// if we've snapped the TitleLayer, fade in the fadeInLayer
			lerpAlpha(fadeInLayer, 1, 0.998f, clock.dt());
		}
		languageLayer.paint(clock);
	}
	
	@Override
	protected void popThis() {
		// only pop this screen if we're on Android and
		// the player might be killing the game w/ the back button
		if (PlayN.platformType() == Type.ANDROID) {
			super.popThis();
		}
	}
 

}
