package tuxkids.tuxblocks.core.utils;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Image;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Font.Style;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Cache.Key;
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.PlayNObject;

public class CanvasUtils extends PlayNObject {

	public static final int TRANSPARENT = Color.argb(0, 0, 0, 0);
	
	private static class RectKey extends Key {

		protected float width, height, strokeWidth;
		protected int fillColor, strokeColor;
		
		public RectKey set(float width, float height, int fillColor, 
				float strokeWidth, int strokeColor) {
			this.width = width;
			this.height = height;
			this.fillColor = fillColor;
			this.strokeWidth = strokeWidth;
			this.strokeColor = strokeColor;
			return this;
		}
		
		@Override
		public void addFields(HashCode hashCode) {
			hashCode.addField(width);
			hashCode.addField(height);
			hashCode.addField(fillColor);
			hashCode.addField(strokeWidth);
			hashCode.addField(strokeColor);
		}

		@Override
		public Key copy() {
			return new RectKey().set(width, height, 
					fillColor, strokeWidth, strokeColor);
		}	
	}
	
	private static RectKey rectKey = new RectKey();
	
	public static CanvasImage createRect(float width, float height, int fillColor) {
		return createRect(width, height, fillColor, 0, 0);
	}
	
	public static CanvasImage createRect(float width, float height, int fillColor, 
			float strokeWidth, int strokeColor) {
//		debug("createRect");
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
	
	public static Image createRectCached(float width, float height, int fillColor) {
		return createRectCached(width, height, fillColor, 0, 0);
	}
	
	public static Image createRectCached(float width, float height, int fillColor, 
			float strokeWidth, int strokeColor) {
		rectKey.set(width, height, fillColor, strokeWidth, strokeColor);
		Image image = Cache.getImage(rectKey);
		if (image != null) return image;
		return Cache.putImage(rectKey, createRect(width, height, fillColor, strokeWidth, strokeColor));
	}
	
	private static class RoundRectKey extends RectKey {
		
		protected float rad;
		
		public RoundRectKey set(float width, float height, float rad, int fillColor, 
				float strokeWidth, int strokeColor) {
			super.set(width, height, fillColor, strokeWidth, strokeColor);
			this.rad = rad;
			return this;
		}
		
		@Override
		public void addFields(HashCode hashCode) {
			super.addFields(hashCode);
			hashCode.addField(rad);
		}

		@Override
		public Key copy() {
			return new RoundRectKey().set(width, height, rad, 
					fillColor, strokeWidth, strokeColor);
		}
	}
	private static RoundRectKey roundRectKey = new RoundRectKey();
	
	public static CanvasImage createRoundRect(float width, float height, float rad, int fillColor) {
		return createRoundRect(width, height, rad, fillColor, 0, 0);
	}
	
	public static CanvasImage createRoundRect(float width, float height, float rad, int fillColor, 
			float strokeWidth, int strokeColor) {
//		debug("createRoundRect");
		width = Math.round(width); height = Math.round(height);
		CanvasImage image = PlayN.graphics().createImage(width, height);
		image.canvas().setFillColor(fillColor);
		image.canvas().fillRoundRect(strokeWidth / 2, strokeWidth / 2, 
				image.width() - strokeWidth, image.height() - strokeWidth, rad);
		if (strokeWidth > 0) {
			image.canvas().setStrokeColor(strokeColor);
			image.canvas().setStrokeWidth(strokeWidth);
			int indent = Math.round(strokeWidth / 2); 
			image.canvas().strokeRoundRect(indent, indent, 
					image.width() - indent * 2 - 1, image.height() - indent * 2 - 1, rad);
		}
		return image;
	}
	
	public static Image createRoundRectCached(float width, float height, float rad, int fillColor) {
		return createRoundRectCached(width, height, rad, fillColor, 0, 0);
	}
	
	public static Image createRoundRectCached(float width, float height, float rad, int fillColor, 
			float strokeWidth, int strokeColor) {
		roundRectKey.set(width, height, rad, fillColor, strokeWidth, strokeColor);
		Image image = Cache.getImage(roundRectKey);
		if (image != null) return image;
		return Cache.putImage(roundRectKey, createRoundRect(width, height, rad, fillColor, strokeWidth, strokeColor));
	}

	private static class CircleKey extends Key {

		protected float rad, strokeWidth;
		protected int fillColor, strokeColor;
		
		public CircleKey set(float rad, int fillColor,
				float strokeWidth, int strokeColor) {
			this.rad = rad;
			this.fillColor = fillColor;
			this.strokeWidth = strokeWidth;
			this.strokeColor = strokeColor;
			return this;
		}
		
		@Override
		public void addFields(HashCode hashCode) {
			hashCode.addField(rad);
			hashCode.addField(fillColor);
			hashCode.addField(strokeWidth);
			hashCode.addField(strokeColor);
		}

		@Override
		public Key copy() {
			return new CircleKey().set(
					rad, fillColor, strokeWidth, strokeColor);
		}	
	}
	private static CircleKey circleKey = new CircleKey();

	public static CanvasImage createCircle(float rad, int fillColor) {
		return createCircle(rad, fillColor, 0, 0);
	}
	
	public static CanvasImage createCircle(float rad, int fillColor, 
			float strokeWidth, int strokeColor) {
//		debug("createCircle");
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

	public static Image createCircleCached(float rad, int fillColor) {
		return createCircleCached(rad, fillColor, 0, 0);
	}
	
	public static Image createCircleCached(float rad, int fillColor, 
			float strokeWidth, int strokeColor) {
		circleKey.set(rad, fillColor, strokeWidth, strokeColor);
		Image image = Cache.getImage(circleKey);
		if (image != null) return image;
		return Cache.putImage(circleKey, createCircle(rad, fillColor, strokeWidth, strokeColor));
	}
	
	private static class TextKey extends Key {

		protected String text, font;
		protected float size;
		protected Style style;
		protected int color;
		
		public TextKey set(String text,	TextFormat format, int color) {
			this.text = text;
			this.color = color;
			this.size = format.font.size();
			this.font = format.font.name();
			this.style = format.font.style();
			return this;
		}
		
		private TextKey set(String text, int color, float size, 
				String font, Style style) {
			this.text = text;
			this.color = color;
			this.size = size;
			this.font = font;
			this.style = style;
			return this;
		}
		
		@Override
		public void addFields(HashCode hashCode) {
			hashCode.addField(text);
			hashCode.addField(font);
			hashCode.addField(size);
			hashCode.addField(style);
			hashCode.addField(color);
		}

		@Override
		public Key copy() {
			return new TextKey().set(text, color, size, font, style);
		}	
	}
	private static TextKey textKey = new TextKey();
	
	public static CanvasImage createText(String text,
			TextFormat format, int color) {
		if (text.isEmpty() || text == null) {
			text = " ";
		}
//		debug("createText");
		TextLayout layout = PlayN.graphics().layoutText(text, format);
		CanvasImage image = PlayN.graphics().createImage(layout.width(), layout.height());
		image.canvas().setFillColor(color);
		image.canvas().fillText(layout, 0, 0);
		return image;
	}
	
	public static Image createTextCached(String text,
			TextFormat format, int color) {
		textKey.set(text, format, color);
		Image image = Cache.getImage(textKey);
		if (image != null) return image;
		return Cache.putImage(textKey, createText(text, format, color));
	}

	public static Image tintImage(Image image, int tint) {
		return tintImage(image, tint, 1);
	}

	public static Image tintImage(Image image, int tint, float perc) {
		if (!image.isReady()) return null;

		int width = (int)image.width(), height = (int)image.height();
		CanvasImage shifted = graphics().createImage(width, height);
		int[] rgb = new int[width * height];
		image.getRgb(0, 0, width, height, rgb, 0, width);
		for (int i = 0; i < rgb.length; i++) {
			rgb[i] = blendTint(rgb[i], tint, perc);
		}
		if (pixelSetter != null) {
			pixelSetter.set(shifted, 0, 0, width, height, rgb, 0, width);
		} else {
			shifted.setRgb(0, 0, width, height, rgb, 0, width);
		}
		return shifted;
	}

	public static PixelSetter pixelSetter;

	public interface PixelSetter {
		public void set(CanvasImage o, int x, int y, int width, int height, int[] rgb, int offset, int scanSize);
	}

	public static String colorToString(int c) {
		return Formatter.format("[%d,%d,%d,%d]", Color.alpha(c), Color.red(c), Color.green(c), Color.blue(c));
	}

	public static int blendTint(int c1, int c2, float perc) {
		return Color.argb(Math.min(Color.alpha(c1), Color.alpha(c2)),
				255 - Math.min((int)(255 - Color.red(c1) + (255 - Color.red(c2)) * perc), 255),
				255 - Math.min((int)(255 - Color.green(c1) + (255 - Color.green(c2)) * perc), 255),
				255 - Math.min((int)(255 - Color.blue(c1) + (255 - Color.blue(c2)) * perc), 255));
	}

	public static int blendAddative(int c1, int c2, float perc) {
		return Color.argb(Math.max(Color.alpha(c1), Color.alpha(c2)),
				Math.min((int)(Color.red(c1) + Color.red(c2) * perc), 255),
				Math.min((int)(Color.green(c1) + Color.green(c2) * perc), 255),
				Math.min((int)(Color.blue(c1) +  Color.blue(c2) * perc), 255));
	}

	public static int hsvToRgb(float hue, float saturation, float value) {
		while (hue < 0) hue++;
		int h = (int)(hue * 6) % 6;
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
		case 0: return rgbFloatToInt(value, t, p);
		case 1: return rgbFloatToInt(q, value, p);
		case 2: return rgbFloatToInt(p, value, t);
		case 3: return rgbFloatToInt(p, q, value);
		case 4: return rgbFloatToInt(t, p, value);
		case 5: return rgbFloatToInt(value, p, q);
		default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
		}
	}

	public static void rgbToHsv(int color, float[] hsv){

		int r = Color.red(color), g = Color.green(color), b = Color.blue(color);
		float h, s, v;
		float min, max, delta;

		min = Math.min(Math.min(r, g), b);
		max = Math.max(Math.max(r, g), b);
		
		// V
		v = max / 255f;
		delta = max - min;

		// S
		if( max != 0 )
			s = delta / max;
		else {
			hsv[0] = 0; hsv[1] = 0; hsv[2] = 0;
			return;
		}
		
		if (delta == 0) {
			hsv[0] = 0; hsv[1] = 0; hsv[2] = max / 255f;
			return;
		}

		// H
		if( r == max )
			h = (g - b) / delta; // between yellow & magenta
		else if( g == max )
			h = 2 + (b - r) / delta; // between cyan & yellow
		else
			h = 4 + (r - g) / delta; // between magenta & cyan

		h /= 6;    // degrees

		while (h < 0) h += 1;

		hsv[0] = h; hsv[1] = s; hsv[2] = v;
	}

	private static int rgbFloatToInt(float r, float g, float b) {
		return Color.rgb((int)(255 * r), (int)(255 * g), (int)(255 * b));
	}

	public static Image createString(TextFormat format, String text, int color) {
		if (text == null) return null;
		if (text.isEmpty()) PlayN.log().warn("Creating image with empty String.");
		TextLayout layout =  graphics().layoutText(text, format);
		float width = Math.max(layout.width(), 1);
		float height = Math.max(layout.height(), 1);
		CanvasImage image = graphics().createImage(width, height);
		image.canvas().setFillColor(color);
		image.canvas().fillText(layout, 0, 0);
		return image;
	}
}
