package tuxkids.tuxblocks.core.widget.menu;

import playn.core.Font.Style;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.util.Callback;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.Lang;
import tuxkids.tuxblocks.core.Lang.Language;
import tuxkids.tuxblocks.core.title.TitleScreen;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;

public class LanguageMenu extends MenuLayer {

	private static int COLS = 3;
	
	// Keep one instance to avoid recreation
	private static LanguageMenu instance; 
	
	/** Show the main menu from the given BaseScreen */
	public static void show(ScreenStack screens) {
		if (instance == null || instance.destroyed()) {
			instance = new LanguageMenu();
			instance.screens = screens;
		}
		show(instance);
	}
	
	private final Button[] buttons;
	private int selectedIndex = 0;
	private int startIndex;
	private boolean closing;
	private ScreenStack screens;
	
	public LanguageMenu() {
		super(graphics().width() * 0.7f, graphics().height() * 0.7f);
		
		buttons = new Button[Language.values().length];
		
		int index = 0;
		int col = 0;
		float rowHeight = 0, rowY = -height * 0.4f;
		float textSize = height * 0.07f;
		float border = textSize * 0.2f;
		for (Language lang : Language.values()) {
			float x = ((col + 1) / (COLS + 1f) - 0.5f) * width * 1.2f;
			TextFormat format = createFormat(lang.font(), Style.PLAIN, textSize);
			TextLayout layout = graphics().layoutText(lang.fullName(), format);
			Image buttonImage = CanvasUtils.createRoundRectCached(layout.width() + border * 4, layout.height() + border * 4 , border, CanvasUtils.TRANSPARENT, border, Colors.WHITE);
			Button button = new Button(buttonImage, false);
			button.setPosition(x, rowY + button.height() / 2);
			layer.add(button.layerAddable());
			
			Image textImage = CanvasUtils.createTextCached(lang.fullName(), format, Colors.WHITE);
			ImageLayer textLayer = graphics().createImageLayer(textImage);
			centerImageLayer(textLayer);
			textLayer.setTranslation(button.x(), button.y());
			layer.add(textLayer);
			
			final int fi = index;
			button.setOnReleasedListener(new OnReleasedListener() {
				@Override
				public void onRelease(Event event, boolean inButton) {
					if (inButton) select(fi);
				}
			});
			
			buttons[index++] = button;
			rowHeight = Math.max(button.height(), rowHeight);
			col++;
			if (col >= COLS) {
				col = 0;
				rowY += rowHeight;
				rowHeight = 0;
			}
		}
		
		
		float size = height * 0.2f;
		Button okButton = new Button(Constant.BUTTON_OK, size, size, true);
		okButton.setPosition((width - size * 1.4f) / 2, (height - size * 1.4f) / 2);
		layer.add(okButton.layerAddable());
		okButton.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) hideInstance();
			}
		});
	}

	@Override
	protected void showInstance() {
		super.showInstance();
		closing = false;
		startIndex = Lang.language().ordinal();
		select(startIndex);
	}
	
	@Override
	protected void hideInstance() {
		if (closing) return;
		
		if (selectedIndex == startIndex) {
			super.hideInstance();
			return;
		}

		closing = true;
		Lang.setLanguage(Language.values()[selectedIndex], new Callback<Void>() {

			@Override
			public void onSuccess(Void result) {
				Cache.clear();
				MenuLayer.clear();
				Tutorial.clear();
				PlayN.storage().setItem(Constant.KEY_LANG, Lang.language().name());
				((TitleScreen) screens.top()).reload();
			}

			@Override
			public void onFailure(Throwable cause) {
				cause.printStackTrace();
				LanguageMenu.super.hideInstance();
			}
		});
	}
	
	private void select(int index) {
		selectedIndex = index;
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setTint(i == selectedIndex ? Colors.RED : Colors.WHITE);
		}
	}
	
}
