package tuxkids.tuxblocks.html;
import playn.core.Font;
import playn.core.CanvasImage;
import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;
import tuxkids.tuxblocks.core.Lang.Language;
import tuxkids.tuxblocks.core.TuxBlocksGame;
import tuxkids.tuxblocks.core.utils.CanvasUtils.PixelSetter;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.LocalizedNames;

public class TuxBlocksGameHtml extends HtmlGame implements PixelSetter {

	private HtmlPlatform platform;

	@Override
	public void start() {
		HtmlPlatform.Config config = new HtmlPlatform.Config();
		// use config to customize the HTML platform, if needed
		platform = HtmlPlatform.register(config);
		platform.assets().setPathPrefix("tuxblocks/");
//		platform.graphics().registerFontMetrics("Raavi", Font.Style.PLAIN, 4,40);
//		platform.graphics().registerFontMetrics("Mangal", Font.Style.PLAIN, 0,0);

		//CanvasUtils.pixelSetter = this;
		
		// GWT localization: a work in progress
		LocalizedNames locales = LocaleInfo.getCurrentLocale().getLocalizedNames();
		String[] langs = locales.getLikelyRegionCodes();
		String language = null;
		for (String lang : langs) {
			lang = lang.toLowerCase();
			for (Language acceptedLang : Language.values()) {
				if (lang.contains(acceptedLang.code().toLowerCase())) {
					language = acceptedLang.code();
					break;
				}
			}
			if (language != null) break;
		}
		PlayN.run(new TuxBlocksGame(language));
	}

	@Override
	public void set(CanvasImage c, int x, int y, int width, int height, int[] rgb,
			int offset, int scanSize) {
		doSet(c, x, y, width, height, rgb, offset, scanSize);
	}
	
	public static native void doSet(Object o, int startX, int startY, int width, int height, int[] rgb,
			int offset, int scanSize) /*-{
		jCanvas = o.@playn.html.HtmlCanvasImage::canvas;
		jCanvas.@playn.core.gl.AbstractCanvasGL::isDirty = true;
		ctx = jCanvas.@playn.html.AbstractHtmlCanvas::ctx;
		imageData = ctx.createImageData(width, height);
		pixelData = imageData.data;
		i = 0;
		dst = 0;
		for (y = 0; y < height; ++y) {
		  for (x = 0; x < width; ++x) {
		    rgba = rgb[dst + x];
		    pixelData[i++] = rgba >> 16 & 255;
		    pixelData[i++] = rgba >> 8 & 255;
		    pixelData[i++] = rgba & 255;
		    pixelData[i++] = rgba >> 24 & 255;
		  }
		  dst += scanSize;
		}
		ctx.putImageData(imageData, startX, startY);
	}-*/;

}
