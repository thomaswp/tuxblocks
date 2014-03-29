package tuxkids.tuxblocks.core.title;

import playn.core.Color;
import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.TextFormat;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.lang.Lang.Language;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.widget.menu.LanguageMenu;

public class LanguageLayer extends LayerWrapper {
	
	private final static int FLIP_TIME = 5000;
		
	protected final GroupLayer groupLayer;
	protected final ImageLayer[] imageLayers;
	protected int showingIndex;
	protected float sinceLastFlip;
	protected float height;

	public LanguageLayer(int themeColor, final ScreenStack screens) {
		super(graphics().createGroupLayer());
		
		groupLayer = (GroupLayer) layerAddable();
		imageLayers = new ImageLayer[Language.values().length];
		
		int i = 0;
		float width = 0;
		float height = 0;
		for (Language language : Language.values()) {
			TextFormat format = createFormat(language.font(), Style.PLAIN, graphics().height() / 20);
			ImageLayer imageLayer = graphics().createImageLayer();
			imageLayer.setImage(CanvasUtils.createTextCached(language.welcome(), format, themeColor));
			centerImageLayer(imageLayer);
			imageLayer.setAlpha(0);
			width = Math.max(width, imageLayer.width());
			height = Math.max(height, imageLayer.height());
			groupLayer.add(imageLayer);
			imageLayers[i++] = imageLayer;
		}
		
		float border = graphics().width() * 0.015f;
		Image buttonImage = CanvasUtils.createRoundRectCached(width + border * 3, height + border * 3, 
				border, CanvasUtils.TRANSPARENT, border, Colors.WHITE);
		Button button = new Button(buttonImage, false);
		button.setTint(Colors.WHITE, Color.rgb(200, 200, 200));
		groupLayer.add(button.layerAddable());
		this.height = buttonImage.height();
		
		button.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					LanguageMenu.show(screens);
				}
			}
		});
	}

	public void paint(Clock clock) {
		sinceLastFlip += clock.dt();
		if (sinceLastFlip > FLIP_TIME) {
			sinceLastFlip -= FLIP_TIME;
			showingIndex = (showingIndex + 1) % imageLayers.length;
		}
		
		float moveBase = 0.995f;
		float alphaBase = 0.99f;
		for (int i = 0; i < imageLayers.length; i++) {
			ImageLayer layer = imageLayers[i];
			float height = layer.height() * 0.7f;
			if (i == showingIndex) {
				lerpAlpha(layer, 1, alphaBase, clock.dt());
				layer.setTy(lerpTime(layer.ty(), 0, moveBase, clock.dt()));
			} else if (i == (showingIndex + imageLayers.length - 1) % imageLayers.length) {
				lerpAlpha(layer, 0, alphaBase, clock.dt());
				layer.setTy(lerpTime(layer.ty(), -height, moveBase, clock.dt()));
			} else if (i == (showingIndex + 1) % imageLayers.length) {
				lerpAlpha(layer, 0, alphaBase, clock.dt());
				layer.setTy(lerpTime(layer.ty(), height, moveBase, clock.dt()));
			} else {
				layer.setAlpha(0);
				layer.setTy(-height);
			}
		}
	}

	public float height() {
		return height;
	}
}
