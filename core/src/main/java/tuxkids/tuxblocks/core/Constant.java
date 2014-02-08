package tuxkids.tuxblocks.core;

import java.util.List;
import java.util.ArrayList;

import playn.core.PlayN;

/**
 * Class containing constant values, such as resource paths
 * and storage keys.
 */
public class Constant {
	
	private final static List<String> preloadImages = new ArrayList<String>(),
			preloadMusic = new ArrayList<String>(),
			preloadSound = new ArrayList<String>();
	
	private static String preloadImage(String imagePath) {
		preloadImages.add(imagePath);
		return imagePath;
	}
	
	private static String preloadMusic(String audioPath) {
		preloadMusic.add(audioPath);
		return audioPath;
	}
	
	private static String preloadSound(String audioPath) {
		preloadSound.add(audioPath);
		return audioPath;
	}
	
	public static void preloadImages() {
		for (String path : preloadImages) {
			PlayN.assets().getImage(path);
		}
	}
	
	public static void preloadAudio() {
		for (String path : preloadMusic) {
			Audio.bg().preload(path);
		}
		for (String path : preloadSound) {
			Audio.se().preload(path);
		}
	}
	
	public final static String IMAGE_PATH = "images/";
	public static final String TUTORIAL_IMAGE_PATH = IMAGE_PATH + "tutorial/";
	public final static String NINEPATCH_PATH = IMAGE_PATH + "np/";
	public final static String AUDIO_PATH = "audio/";
	public final static String BG_PATH = AUDIO_PATH + "bg/";
	public final static String SE_PATH = AUDIO_PATH  + "se/";
	
	public final static String BUTTON_BACK = preloadImage(IMAGE_PATH + "back.png");
	public final static String BUTTON_FORWARD = preloadImage(IMAGE_PATH + "forward.png");
	public final static String BUTTON_UP = preloadImage(IMAGE_PATH + "up.png");
	public final static String BUTTON_DOWN = preloadImage(IMAGE_PATH + "down.png");
	public final static String BUTTON_OK = preloadImage(IMAGE_PATH + "ok.png");
	public final static String BUTTON_CANCEL = preloadImage(IMAGE_PATH + "cancel.png");
	public final static String BUTTON_CENTER = preloadImage(IMAGE_PATH + "center.png");
	public final static String BUTTON_PLUS = preloadImage(IMAGE_PATH + "plus.png");
	public final static String BUTTON_RESET = preloadImage(IMAGE_PATH + "reset.png");
	public final static String BUTTON_CIRCLE = preloadImage(IMAGE_PATH + "circle.png");
	public final static String BUTTON_LESS = preloadImage(IMAGE_PATH + "less.png");
	public final static String BUTTON_MORE = preloadImage(IMAGE_PATH + "more.png");
	public final static String BUTTON_SCRATCH = preloadImage(IMAGE_PATH + "scratch.png");
	public static final String BUTTON_MENU = preloadImage(IMAGE_PATH + "options.png");
	
	public static final String IMAGE_HEART = preloadImage(IMAGE_PATH + "heart.png");
	public static final String IMAGE_CONFIRM = preloadImage(IMAGE_PATH + "confirm.png");
	public static final String IMAGE_HOURGLASS = preloadImage(IMAGE_PATH + "hourglass.png");
	public static final String IMAGE_UPGRADE = preloadImage(IMAGE_PATH + "upgrade.png");
	public static final String IMAGE_LOGO = preloadImage(IMAGE_PATH + "logo.png");
	public static final String IMAGE_TUX = preloadImage(IMAGE_PATH + "tux.png");
	public static final String IMAGE_START_LOCAL = "start.png";
	
	public final static String NINEPATCH_BUBBLE = preloadImage(NINEPATCH_PATH + "bubble.9.png");
	
	public final static String BG_MENU = preloadMusic(BG_PATH + "menu");
	public final static String BG_GAME1 = preloadMusic(BG_PATH + "game1");
	
	public final static String SE_OK = preloadSound(SE_PATH + "ok");
	public final static String SE_BACK = preloadSound(SE_PATH + "back");
	public final static String SE_SUCCESS = preloadSound(SE_PATH + "success");
	public static final String SE_SUCCESS_SPECIAL = preloadSound(SE_PATH + "success-special");
	public final static String SE_TICK = preloadSound(SE_PATH + "tick");
	public final static String SE_DROP = preloadSound(SE_PATH + "drop");
	public static final String SE_SELECT = preloadSound(SE_PATH + "select");
	public static final String SE_BEAT = preloadSound(SE_PATH + "beat");
	public static final String SE_PITCH = preloadSound(SE_PATH + "pitch");
	public static final String SE_PITCH_FINAL = preloadSound(SE_PATH + "pitch-final");
	public static final String SE_DIE = preloadSound(SE_PATH + "die");
	public static final String SE_BOOM = preloadSound(SE_PATH + "boom");
	public static final String SE_ZAP = preloadSound(SE_PATH + "zap");
	public static final String SE_WIND = preloadSound(SE_PATH + "wind");
	public static final String SE_POP0 = preloadSound(SE_PATH + "pop0");
	public static final String SE_POP1 = preloadSound(SE_PATH + "pop1");
	public static final String SE_POP2 = preloadSound(SE_PATH + "pop2");
	public static final String SE_POP3 = preloadSound(SE_PATH + "pop3");
	public static final String SE_GAMEOVER = preloadSound(SE_PATH + "gameover");
	public static final String SE_VICTORY = preloadSound(SE_PATH + "victory");
	
	public static String SEPop(int n) {
		return SE_PATH + "pop" + n;
	}
	
	// unicode symbols
	public static final String TIMES_SYMBOL = "\u00D7";
	public static final String DIVIDE_SYMBOL = "\u00F7";
	public static final String DOT_SYMBOL = "\u00B7";
	public static final String INFINITY_SYMBOL = "\u221E";
	
	public static final String NUMBER_FONT = "Arial";
	
	public static final String TUX_URL = "http://tux4kids.alioth.debian.org/";
	
	public static final String TUTORIAL_START_PATH = "TutorialStart.txt";
	public static final String TUTORIAL_PLAY_PATH = "TutorialPlay.txt";
	public static final String TUTORIAL_BUILD_PATH = "TutorialBuild.txt";
	
	public static final String TEXT_ABOUT = "About.txt";
	
	public final static String TUTORIAL_TEXT_CLICK = "click";
	public final static String TUTORIAL_TEXT_CLICKING = "clicking";
	public final static String TUTORIAL_TEXT_MOUSE = "mouse";
	public final static String TUTORIAL_TEXT_MENU = "menu";
	
	public final static String KEY_BG_VOLUME = "bg-volume";
	public final static String KEY_SE_VOLUME = "se-volume";
	public final static String KEY_LANG = "lang";
	public static final String KEY_GAME = "<game>";
}
