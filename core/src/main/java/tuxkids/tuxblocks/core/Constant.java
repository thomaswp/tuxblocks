package tuxkids.tuxblocks.core;

import java.util.List;
import java.util.ArrayList;

import playn.core.PlayN;

public class Constant {
	
	private final static List<String> preload = new ArrayList<String>();
	
	private static String preload(String imagePath) {
		preload.add(imagePath);
		return imagePath;
	}
	
	public static void preloadImages() {
		for (String path : preload) {
			PlayN.assets().getImage(path);
		}
	}
	
	public final static String IMAGE_PATH = "images/";
	public final static String TUTORIAL_IMAGE_PATH = IMAGE_PATH + "tutorial/"; 
	public final static String NINEPATCH_PATH = IMAGE_PATH + "np/";
	
	public final static String BUTTON_BACK = preload(IMAGE_PATH + "back.png");
	public final static String BUTTON_FORWARD = preload(IMAGE_PATH + "forward.png");
	public final static String BUTTON_UP = preload(IMAGE_PATH + "up.png");
	public final static String BUTTON_DOWN = preload(IMAGE_PATH + "down.png");
	public final static String BUTTON_OK = preload(IMAGE_PATH + "ok.png");
	public final static String BUTTON_CANCEL = preload(IMAGE_PATH + "cancel.png");
	public final static String BUTTON_CENTER = preload(IMAGE_PATH + "center.png");
	public final static String BUTTON_PLUS = preload(IMAGE_PATH + "plus.png");
	public final static String BUTTON_RESET = preload(IMAGE_PATH + "reset.png");
	public final static String BUTTON_CIRCLE = preload(IMAGE_PATH + "circle.png");
	public final static String BUTTON_LESS = preload(IMAGE_PATH + "less.png");
	public final static String BUTTON_MORE = preload(IMAGE_PATH + "more.png");
	public final static String BUTTON_SCRATCH = preload(IMAGE_PATH + "scratch.png");
	public static final String BUTTON_MENU = preload(IMAGE_PATH + "options.png");
	
	public static final String IMAGE_HEART = preload(IMAGE_PATH + "heart.png");
	public static final String IMAGE_CONFIRM = preload(IMAGE_PATH + "confirm.png");
	public static final String IMAGE_HOURGLASS = preload(IMAGE_PATH + "hourglass.png");
	public static final String IMAGE_UPGRADE = preload(IMAGE_PATH + "upgrade.png");
	public static final String IMAGE_LOGO = preload(IMAGE_PATH + "logo.png");
	public static final String IMAGE_START = preload(IMAGE_PATH + "start.png");
	public static final String IMAGE_TUX = preload(IMAGE_PATH + "tux.png");
	
	public final static String NINEPATCH_BUBBLE = preload(NINEPATCH_PATH + "bubble.9.png");
	
	public static final String FONT_NAME = "Arial";
	
	public static final String TIMES_SYMBOL = "\u00D7";
	public static final String DIVIDE_SYMBOL = "\u00F7";
	public static final String DOT_SYMBOL = "\u00B7";
	public static final String INFINITY_SYMBOL = "\u221E";
	
	public static final String TUX_URL = "http://tux4kids.alioth.debian.org/";
	
	public static final String TUTORIAL_PATH = "tutorial.txt";

	public static String click() {
		return PlayN.touch().hasTouch() ? "tap" : "click";
	}

	public static CharSequence mouse() {
		return PlayN.touch().hasTouch() ? "finger" : "mouse";
	}
}
