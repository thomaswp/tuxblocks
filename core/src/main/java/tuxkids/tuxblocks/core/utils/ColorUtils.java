package tuxkids.tuxblocks.core.utils;

import playn.core.Color;

public class ColorUtils {
	public static int hsvToRgb(float hue, float saturation, float value) {

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
	
	private static int rgbFloatToInt(float r, float g, float b) {
		return Color.rgb((int)(255 * r), (int)(255 * g), (int)(255 * b));
	}
	
	private static float rSeed = (float) Math.random();
	public static float goldenRandom() {
		rSeed += 0.618033988749895f;
		rSeed %= 1;
		return rSeed;
	}
}
