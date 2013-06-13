package tuxkids.tuxblocks.core.utils;

import playn.core.CanvasImage;
import playn.core.Graphics;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;

public class CanvasUtils {
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
}
