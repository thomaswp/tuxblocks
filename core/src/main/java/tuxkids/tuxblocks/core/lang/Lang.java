package tuxkids.tuxblocks.core.lang;

import playn.core.Image;
import playn.core.Json.Object;
import playn.core.PlayN;
import playn.core.json.JsonParserException;
import playn.core.util.Callback;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.utils.Formatter;
import tuxkids.tuxblocks.core.utils.PlayNObject;

public class Lang extends PlayNObject {
	
	public enum Language {
		
		EN("English", "en", "Welcome"), 
//		FR("Français", "fr", "Bienvenu"), 
		PA("ਪੰਜਾਬੀ", "pa", "ਜੀ ਆਇਆ ਨੂੰ ", "Raavi"),
		HI("हिंदी ","hi", "स्वागतम ", "Mangal");
		
		private String name, code, font, welcome;
		
		private Language(String name, String code, String welcome) {
			this(name, code, welcome, "Arial");
		}
		
		private Language(String name, String code, String welcome, String font) {
			this.name = name;
			this.code = code;
			this.welcome = welcome;
			this.font = font;
		}
		
		public String fullName() { return name; }		
		public String code() { return code; }
		public String font() { return font; }
		public String welcome() { return welcome; }

		@Override
		public String toString() {
			return code;
		}
	}
	
	private static final String DEVICE_PC = "pc";
	private static final String DEVICE_MOBILE = "mobile";
	
	private final static String TEXT_PATH = "text/";
	private final static String STRINGS_PATH = "Strings.json";
	public final static Language DEFAULT_LANGUAGE = Language.EN;
	
	private static Language language = DEFAULT_LANGUAGE;
	private static Object dictionary;
	private static Object defaultDictionary;
	
	public static Language language() {
		return language;
	}

	public static String font() {
		return language.font;
	}
	
	public static boolean loaded() {
		return dictionary != null;
	}
	
	public static void clear() {
		language = DEFAULT_LANGUAGE;
		dictionary = null;
		defaultDictionary = null;
	}
	
	public static void setLanguage(final Language language, final Callback<Void> callback) {
		dictionary = null;
		if (defaultDictionary == null) {
			loadLangauge(DEFAULT_LANGUAGE, new Callback<Object>() {
				@Override
				public void onSuccess(Object dictionary) {
					defaultDictionary = dictionary;
					setLanguage(language, callback);
				}

				@Override
				public void onFailure(Throwable cause) {
					callback.onFailure(cause);
				}
			});
			return;
		}
		
		if (language == DEFAULT_LANGUAGE) {
			dictionary = defaultDictionary;
			Lang.language = language;
			callback.onSuccess(null);
		} else {
			loadLangauge(language, new Callback<Object>() {
				@Override
				public void onSuccess(Object result) {
					dictionary = result;
					Lang.language = language;
					callback.onSuccess(null);
				}

				@Override
				public void onFailure(Throwable cause) {
					callback.onFailure(cause);
				}
			});
		}
	}
	
	private static void loadLangauge(final Language language, final Callback<Object> callback) {
		assets().getText(TEXT_PATH + language + "/" + STRINGS_PATH, new Callback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					Object dictionary = PlayN.json().parse(result);
					callback.onSuccess(dictionary);
				} catch (JsonParserException e) {
					callback.onFailure(e);
				}
			}

			@Override
			public void onFailure(Throwable cause) {
				callback.onFailure(cause);
			}
		});
	}
	
	public static void getText(final String path, final Callback<String> callback) {
		assets().getText(TEXT_PATH + language + "/" + path, new Callback<String>() {
			@Override
			public void onSuccess(String result) {
				callback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable cause) {
				assets().getText(TEXT_PATH + DEFAULT_LANGUAGE + "/" + path, callback);
			}
		});
	}
	
	public static String getString(String key) {
		return getString(null, key);
	}
	
	public static String getString(String domain, String key) {
		String result = getString(dictionary, domain, key);
		if (result != null) return result;
		result = getString(defaultDictionary, domain, key);
		if (result == null) {
			PlayN.log().warn(Formatter.format(
					"No value found for key '%s%s' in '%s%s/Strings.json' or the default '%s%s/Strings.json'.", 
					domain == null ? "[null]" : domain + ":", key, TEXT_PATH, language, TEXT_PATH, DEFAULT_LANGUAGE));
		}
		return result;
	}
	
	private static String getString(Object dictionary, String domain, String key) {
		if (dictionary == null) return null;
		if (domain == null) {
			return dictionary.getString(key);
		} else {
			Object dic = dictionary.getObject(domain);
			if (dic == null) return null;
			return dic.getString(key);
		}
	}
	
	public static void getImage(final String path, final Callback<Image> callback) {
		Image image = assets().getImage(Constant.IMAGE_PATH + language + "/" + path);
		image.addCallback(new Callback<Image>() {
			@Override
			public void onSuccess(Image result) {
				callback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable cause) {
				assets().getImage(Constant.IMAGE_PATH + DEFAULT_LANGUAGE + "/" + path).addCallback(callback);
			}
		});
	}
	
	private static String getPlatformTextPostfix() {
		return "-" + (PlayN.touch().hasTouch() ? DEVICE_MOBILE : DEVICE_PC);
	}
	
	public static String getDeviceString(String domain, String key) {
		if (key == null) return null;
		return getString(domain, key + getPlatformTextPostfix());
	}
}
