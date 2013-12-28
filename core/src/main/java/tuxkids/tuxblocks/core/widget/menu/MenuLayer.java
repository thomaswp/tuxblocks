package tuxkids.tuxblocks.core.widget.menu;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Game;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Pointer.Event;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.Lang;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnPressedListener;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

/**
 * Base class for showing menus in the game. Only one menu many be shown
 * at a time. You must call {@link MenuLayer#paint(Clock)} and 
 * {@link MenuLayer#update(int)} in {@link Game}'s main loops
 * for the menu to function correctly. You must also call {@link MenuLayer#clear()}
 * before the game starts, in case any static references remain.
 */
public abstract class MenuLayer extends LayerWrapper {

	protected static int BACKGROUND_COLOR = Color.rgb(102, 107, 164);
	protected static int BACKGROUND_BORDER_COLOR = Color.rgb(45, 47, 104);
	
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
	
	/** Resets the menu for the beginning of the game */
	public static void clear() {
		for (MenuLayer menu : shown) {
			if (menu != null) {
				// destroy all previously shown menus, so they know to recreate themselves
				menu.destroy();
				menu = null;
			}
		}
		shown.clear();
		instance = null;
	}
	
	/** Shows the given MenuLayer if none is currently active */
	public static void show(MenuLayer layer) {
		if (instance == null && layer != null) {
			layer.showInstance();
			MenuLayer.instance = layer;
			if (!shown.contains(instance)) shown.add(instance);
		}
	}
	
	/** Hides the current MenuLayer */
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
		
		// Create a layer to dim the rest of the screen
		dimmer = graphics().createImageLayer(
				CanvasUtils.createRect(1, 1, Colors.BLACK));
		dimmer.setSize(gWidth(), gHeight());
		dimmer.setDepth(-10);
		dimmer.setAlpha(0.25f);
		dimmer.setTranslation(-gWidth() / 2, -gHeight() / 2);
		layer.add(dimmer);
		
		background = graphics().createImageLayer();
		background.setImage(CanvasUtils.createRoundRect(width, height, height * 0.05f,
				BACKGROUND_COLOR, height * 0.02f, BACKGROUND_BORDER_COLOR));
		background.setAlpha(0.98f);
		centerImageLayer(background);
		layer.add(background);
	}
	
	/** Sets the given button with the given attributes and creates a background of the appropriate size. */
	protected void setButton(Button button, float width, String text, float textSize, OnReleasedListener action) {
		TextFormat buttonFormat = createFormat(textSize);
		float height = buttonFormat.font.size() * 1.5f;
		float rad = gHeight() * 0.008f, border = gHeight() * 0.008f;
		CanvasImage image = CanvasUtils.createRoundRect(width, height, rad, 
				Color.argb(100, 255, 255, 255), border, BACKGROUND_BORDER_COLOR);
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
	
	/** Called when this MenuLayer is shown */
	protected void showInstance() {
		setAlpha(0);
		setVisible(true);
		dimmer.setInteractive(true);
		showing = true;
	
		pointer().cancelLayerDrags();
	}
	
	/** Called when this MenuLayer is hidden */
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
	
	protected String getString(String key) {
		return Lang.getString("menu", key);
	}
}
