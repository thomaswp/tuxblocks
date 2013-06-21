package tuxkids.tuxblocks.core.utils;

import org.w3c.dom.html.HTMLCollection;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Graphics;
import playn.core.Image;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;

public class CanvasUtils extends PlayNObject {
	public static CanvasImage createRect(float width, float height, int fillColor, 
			float strokeWidth, int strokeColor) {
		width = (int)width; height = (int)height;
		CanvasImage image = PlayN.graphics().createImage(width, height);
		image.canvas().setFillColor(fillColor);
		image.canvas().fillRect(0, 0, image.width(), image.height());
		if (strokeWidth > 0) {
			image.canvas().setStrokeColor(strokeColor);
			image.canvas().setStrokeWidth(strokeWidth);
			image.canvas().strokeRect(0, 0, width - strokeWidth / 2, 
					height - strokeWidth / 2);
		}
		return image;
	}

	public static CanvasImage createRect(float width, float height, int fillColor) {
		return createRect(width, height, fillColor, 0, 0);
	}

	public static CanvasImage createCircle(float rad, int fillColor, 
			float strokeWidth, int strokeColor) {
		CanvasImage image = PlayN.graphics().createImage(rad * 2, rad * 2);
		image.canvas().setFillColor(fillColor);
		image.canvas().fillCircle(rad, rad, rad);
		if (strokeWidth > 0) {
			image.canvas().setStrokeColor(strokeColor);
			image.canvas().setStrokeWidth(strokeWidth);
			image.canvas().strokeCircle(rad, rad, rad - strokeWidth / 2);
		}
		return image;
	}

	public static CanvasImage createCircle(float rad, int fillColor) {
		return createCircle(rad, fillColor, 0, 0);
	}

	public static CanvasImage createText(String text,
			TextFormat format, int color) {
		TextLayout layout = PlayN.graphics().layoutText(text, format);
		CanvasImage image = PlayN.graphics().createImage(layout.width(), layout.height());
		image.canvas().setFillColor(color);
		image.canvas().fillText(layout, 0, 0);
		return image;
	}

	public static Image tintImage(Image image, int tint) {
		return tintImage(image, tint, 1);
	}
	
	public static CanvasImage ci;
	
	public static Image tintImage(Image image, int tint, float perc) {
		int width = (int)image.width(), height = (int)image.height();
		CanvasImage shifted = graphics().createImage(width, height);
		ci = shifted;
		int[] rgb = new int[width * height];
		image.getRgb(0, 0, width, height, rgb, 0, width);
		for (int i = 0; i < rgb.length; i++) {
			rgb[i] = blendAdditive(rgb[i], tint, perc);
		}
		shifted.canvas().setFillColor(Colors.GRAY);
		shifted.canvas().fillRect(10, 0, 10, 10);
		shifted.setRgb(0, 0, width, height, rgb, 0, width);
		shifted.canvas().fillRect(0, 0, 10, 10);
		return shifted;
	}

	public static int blendAdditive(int c1, int c2, float perc) {
		return Color.argb(Color.alpha(c1),
				255 - Math.min((int)(255 - Color.red(c1) + (255 - Color.red(c2)) * perc), 255),
				255 - Math.min((int)(255 - Color.green(c1) + (255 - Color.green(c2)) * perc), 255),
				255 - Math.min((int)(255 - Color.blue(c1) + (255 - Color.blue(c2)) * perc), 255));
	}
}
