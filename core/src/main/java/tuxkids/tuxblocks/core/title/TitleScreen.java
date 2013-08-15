package tuxkids.tuxblocks.core.title;

import playn.core.Color;
import playn.core.Font.Style;
import playn.core.Assets;
import playn.core.CanvasImage;
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
import tripleplay.game.ScreenStack.Transition;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.DefenseScreen;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.screen.BaseScreen;
import tuxkids.tuxblocks.core.solve.BuildScreen;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.GameBackgroundSprite;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

public class TitleScreen extends BaseScreen{

	private static final int SNAP_TIME = 500;
	
	private int untilSnap;

	private final TitleLayer titleLayer;
	private final GroupLayer fadeInLayer;
	private final TextFormat authorFormat, superFormat, optionFormat;
	private final GameBackgroundSprite background;
	
	private Button tutorialButton;
	private ImageLayerTintable startHere;
	
	public TitleScreen(ScreenStack screens, GameBackgroundSprite background) {
		super(screens, background);
		this.background = background;
		
		titleLayer = new TitleLayer(width());
		titleLayer.setDepth(-1);
		layer.add(titleLayer.layerAddable());
		
		authorFormat = new TextFormat().withFont(graphics().createFont(
				Constant.FONT_NAME, Style.PLAIN, (int)(height() / 25)))
				.withAlignment(Alignment.CENTER);
		superFormat = new TextFormat().withFont(graphics().createFont(
				Constant.FONT_NAME, Style.PLAIN, (int)(height() / 35)))
				.withAlignment(Alignment.CENTER);
		optionFormat = new TextFormat().withFont(graphics().createFont(
				Constant.FONT_NAME, Style.PLAIN, (int)(height() / 10)));
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
	protected void pushScreen(final BaseScreen screen, Transition transition) {
		PlayN.pointer().setListener(null);
		super.pushScreen(screen, transition);
	}
	
	private void setup() {
		ImageLayer tuxLayer = createTextLayer("a Tux4Kids game", width() / 5);
		createSuperTextLayer("by", width() / 2);
		createTextLayer("Thomas Price", width() / 2);
		createSuperTextLayer("mentored by", 4 * width() / 5);
		createTextLayer("Aaditya Maheshwari", 4 * width() / 5);
		
		float midY = (height() + titleLayer.height()) / 2 + authorFormat.font.size();
		int tintPressed = Colors.WHITE, tintUnpressed = Color.rgb(200, 200, 200);
		
		final float buttonSize = height() / 6;
		tutorialButton = new Button(Constant.IMAGE_CONFIRM, buttonSize, buttonSize, true);
		tutorialButton.setPosition(width() / 2, midY);
		tutorialButton.setTint(tintPressed, tintUnpressed);
		fadeInLayer.add(tutorialButton.layerAddable());
		
		startHere = new ImageLayerTintable();
		startHere.setImage(PlayN.assets().getImage(Constant.IMAGE_START));
		startHere.image().addCallback(new Callback<Image>() {
			@Override
			public void onSuccess(Image result) {
				startHere.setScale(1f * buttonSize / startHere.width());
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
		
		Button playButton = new Button(modeImage, false);
		playButton.setPosition(width() / 5, midY);
		playButton.setTint(tintPressed, tintUnpressed);
		register(playButton, Tag.Title_Play);
		fadeInLayer.add(playButton.layerAddable());
		
		ImageLayer playText = graphics().createImageLayer();
		playText.setImage(CanvasUtils.createText("Play", optionFormat, Colors.WHITE));
		playText.setTranslation(playButton.x(), playButton.y());
		PlayNObject.centerImageLayer(playText);
		fadeInLayer.add(playText);
		
		Button buildButton = new Button(modeImage, false);
		buildButton.setPosition(4 * width() / 5, midY);
		buildButton.setTint(tintPressed, tintUnpressed);
		register(buildButton, Tag.Title_Build);
		fadeInLayer.add(buildButton.layerAddable());
		
		ImageLayer buildText = graphics().createImageLayer();
		buildText.setImage(CanvasUtils.createText("Build", optionFormat, Colors.WHITE));
		buildText.setTranslation(buildButton.x(), buildButton.y());
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
					Tutorial.trigger(Trigger.Title_Play);
					DifficultyScreen ds = new DifficultyScreen(screens, background);
					pushScreen(ds, screens.slide().left());
				}
			}
		});
		
		buildButton.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					Tutorial.trigger(Trigger.Title_Build);
					GameState state = new GameState(new Difficulty());
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
				PlayN.openURL(Constant.TUX_URL);
			}
			
			@Override
			public void onPointerDrag(Event event) { }
			
			@Override
			public void onPointerCancel(Event event) { }
		});
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
		untilSnap = SNAP_TIME;
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
			fadeInLayer.setAlpha(PlayNObject.lerpTime(fadeInLayer.alpha(), 1, 0.998f, clock.dt(), 0.01f));
		}
	}
	
	@Override
	protected void popThis() {
		if (PlayN.platformType() == Type.ANDROID) {
			super.popThis();
		}
	}
}
