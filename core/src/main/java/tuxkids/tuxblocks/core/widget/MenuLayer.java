package tuxkids.tuxblocks.core.widget;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button.OnPressedListener;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

public class MenuLayer extends LayerWrapper {

	protected static MenuLayer instance;
	protected static List<MenuLayer> shown = new ArrayList<MenuLayer>();
	
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
		for (MenuLayer menu : shown) {
			if (menu != null) {
				menu.destroy();
				menu = null;
			}
		}
		shown.clear();
		instance = null;
	}
	
	public static void show(MenuLayer layer) {
		if (instance == null && layer != null) {
			layer.showInstance();
			MenuLayer.instance = layer;
			if (!shown.contains(instance)) shown.add(instance);
		}
	}
	
	public static void hide() {
		if (instance != null) {
			instance.hideInstance();
		}
	}
	
	public static boolean showing() {
		return instance != null && instance.showing;
	}
	
	protected final float width, height;
	protected final GroupLayer layer;
	protected final ImageLayer dimmer, background;
	protected boolean showing = false;
	
	public MenuLayer(float width, float height) {
		super(graphics().createGroupLayer());
		
		layer = (GroupLayer) layerAddable();
		layer.setDepth(100);
		layer.setTranslation(gWidth() / 2, gHeight() / 2);
		graphics().rootLayer().add(layer);
		
		this.width = width;
		this.height = height;
		
		setVisible(false);
		
		dimmer = graphics().createImageLayer(
				CanvasUtils.createRect(1, 1, Colors.BLACK));
		dimmer.setSize(gWidth(), gHeight());
		dimmer.setDepth(-10);
		dimmer.setAlpha(0.25f);
		dimmer.setTranslation(-gWidth() / 2, -gHeight() / 2);
		layer.add(dimmer);
		
		background = graphics().createImageLayer();
		background.setImage(CanvasUtils.createRoundRect(width, height, height * 0.05f,
				Color.rgb(102, 107, 164), height * 0.02f, Color.rgb(45, 47, 104)));
		background.setAlpha(0.98f);
		centerImageLayer(background);
		layer.add(background);
	}
	
	protected void createButton(Button button, float width, String text, float textSize, OnReleasedListener action) {
		TextFormat buttonFormat = createFormat(textSize);
		float height = buttonFormat.font.size() * 1.5f;
		float rad = gHeight() * 0.008f, border = gHeight() * 0.008f;
		CanvasImage image = CanvasUtils.createRoundRect(width, height, rad, 
				Color.argb(100, 255, 255, 255), border, Color.rgb(45, 47, 104));
		image.canvas().setFillColor(Colors.BLACK);
		TextLayout layout = graphics().layoutText(text, buttonFormat);
		image.canvas().fillText(layout, (image.width() - layout.width()) / 2, 
				(image.height() - layout.height()) / 2);
		button.setImage(image);
		button.setSize(image.width(), image.height());
		button.setOnReleasedListener(action);
		button.setTint(Colors.WHITE, Colors.darker(Colors.WHITE));
		button.setOnPressListener(new OnPressedListener() {
			@Override
			public void onPress(Event event) {
				Audio.se().play(Constant.SE_OK);
			}
		});
	}
	
	protected void showInstance() {
		setAlpha(0);
		setVisible(true);
		dimmer.setInteractive(true);
		showing = true;
	
		pointer().cancelLayerDrags();
	}
	
	protected void hideInstance() {
		dimmer.setInteractive(false);
		showing = false;
	}
	
	protected void updateInstance(int delta) {
	}
	
	protected void paintInstance(Clock clock) {
		if (showing) {
			lerpAlpha(this, 1, 0.99f, clock.dt());
		} else if (alpha() > 0) {
			lerpAlpha(this, 0, 0.99f, clock.dt());
		} else if (visible()) {
			setVisible(false);
			instance = null;
		}
	}
	
}
