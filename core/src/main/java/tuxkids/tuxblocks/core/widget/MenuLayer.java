package tuxkids.tuxblocks.core.widget;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Platform.Type;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.screen.BaseScreen;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.widget.SlideLayer.FillListener;

public class MenuLayer extends LayerWrapper {

	private static MenuLayer instance;
	
	public static void show(BaseScreen screen) {
		if (instance == null) {
			instance = new MenuLayer();
		}
		instance.showInstance(screen);
	}
	
	public static void toggle(BaseScreen screen) {
		if (instance == null || !instance.showing) {
			show(screen);
		} else {
			instance.hideInstance();
		}
	}
	
	public static void update(int delta) {
		if (instance != null) {
			instance.updateInstance(delta);
		}
	}
	
	public static void paint(Clock clock) {
		if (instance != null) {
			instance.paintInstance(clock);
		}
	}
	
	public static void clear() {
		if (instance != null) {
			instance.destroy();
			instance = null;
		}
	}
	
	public static boolean showing() {
		return instance != null && instance.showing;
	}
	
	protected final GroupLayer layer;
	protected final ImageLayer dimmer, background;
	protected final float width, height;
	protected final SlideLayer slideBG, slideSE;
	protected final Button buttonExit;
	protected final TextFormat buttonFormat;
	
	protected boolean showing = false;
	protected BaseScreen screen;
	protected float startBGVolume;
	
	public MenuLayer() {
		super(graphics().createGroupLayer());
		layer = (GroupLayer) layerAddable();
		layer.setDepth(100);
		layer.setTranslation(gWidth() / 2, gHeight() / 2);
		graphics().rootLayer().add(layer);
		
		setVisible(false);
		
		dimmer = graphics().createImageLayer(
				CanvasUtils.createRect(1, 1, Colors.BLACK));
		dimmer.setSize(gWidth(), gHeight());
		dimmer.setDepth(-10);
		dimmer.setAlpha(0.25f);
		dimmer.setTranslation(-gWidth() / 2, -gHeight() / 2);
		layer.add(dimmer);
		
		width = gWidth() * 0.7f; height = gHeight() * 0.7f;
		background = graphics().createImageLayer();
		background.setImage(CanvasUtils.createRoundRect(width, height, height * 0.05f,
				Color.rgb(102, 107, 164), height * 0.02f, Color.rgb(45, 47, 104)));
		background.setAlpha(0.98f);
		centerImageLayer(background);
		layer.add(background);
		
		slideBG = createSlider("Music Volume", 0);
		slideBG.setFillListener(new FillListener() {
			@Override
			public void onFillChanged(float fill) {
				Audio.bg().setVolume(fill);
			}
		});;
		
		slideSE = createSlider("Effects Volume", 0.3f * height);
		slideSE.setFillListener(new FillListener() {
			@Override
			public void onFillChanged(float fill) {
				Audio.se().setVolume(fill);
			}
		});;
		
		
		buttonFormat = createFormat(slideSE.height * 0.8f);
		buttonExit = new Button(null, false);
		buttonExit.setPosition(0, -0.3f * height);
		buttonExit.setTint(Colors.WHITE, Colors.darker(Colors.WHITE));
		layer.add(buttonExit.layerAddable());
	}
	
	protected SlideLayer createSlider(String caption, float y) {
		SlideLayer slider = new SlideLayer(width * 0.8f, height * 0.2f, Colors.GRAY);
		slider.setOrigin(slider.width / 2, slider.height / 2);
		slider.setTranslation(0, y);
		layer.add(slider.layerAddable());

		TextFormat captionFormat = createFormat(slider.height * 0.3f);
		ImageLayer textLayer = graphics().createImageLayer(
				CanvasUtils.createText(caption, captionFormat, Colors.BLACK));
		centerImageLayer(textLayer);
		layer.add(textLayer);
		textLayer.setTranslation(0, y);
		
		return slider;
	}
	
	protected void showInstance(BaseScreen screen) {
		this.screen = screen;
		setAlpha(0);
		setVisible(true);
		dimmer.setInteractive(true);
		showing = true;
		
		slideBG.setFill(Audio.bg().volume());
		slideSE.setFill(Audio.se().volume());
		startBGVolume = Audio.bg().volume();
		
		updateButton();
		
		pointer().cancelLayerDrags();
	}
	
	protected void updateButton() {
		String text;
		OnReleasedListener action;
		
		text = "Quit Game";
		action = new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					if (screen instanceof GameScreen) {
						((GameScreen) screen).quit();
						showing = false;
					}
				}
			}
		};
		
		float height = buttonFormat.font.size() * 1.5f;
		CanvasImage image = CanvasUtils.createRoundRect(width * 0.8f, height, height * 0.05f, 
				Color.argb(100, 255, 255, 255), height * 0.025f, Color.rgb(45, 47, 104));
		image.canvas().setFillColor(Colors.BLACK);
		TextLayout layout = graphics().layoutText(text, buttonFormat);
		image.canvas().fillText(layout, (image.width() - layout.width()) / 2, 
				(image.height() - layout.height()) / 2);
		buttonExit.setImage(image);
		buttonExit.setSize(image.width(), image.height());
		buttonExit.setOnReleasedListener(action);
		
		buttonExit.setEnabled(screen instanceof GameScreen);
		buttonExit.layerAddable().setAlpha(buttonExit.enabled() ? 1f : 0.5f);
	}
	
	protected void hideInstance() {
		dimmer.setInteractive(false);
		showing = false;
		PlayN.storage().setItem(Constant.KEY_BG_VOLUME, "" + Audio.bg().volume());
		PlayN.storage().setItem(Constant.KEY_SE_VOLUME, "" + Audio.se().volume());
		if (PlayN.platformType() == Type.HTML) {
			if (startBGVolume != Audio.bg().volume()) {
				Audio.bg().restart();
			}
		}
	}
	
	protected void updateInstance(int delta) {
	}
	
	protected void paintInstance(Clock clock) {
		if (showing) {
			lerpAlpha(this, 1, 0.99f, clock.dt());

			slideBG.paint(clock);
			slideSE.paint(clock);
			
		} else if (alpha() > 0) {
			lerpAlpha(this, 0, 0.99f, clock.dt());
		} else if (visible()) {
			setVisible(false);
		}
	}
}
