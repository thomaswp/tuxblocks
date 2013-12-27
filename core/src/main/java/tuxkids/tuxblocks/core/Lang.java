package tuxkids.tuxblocks.core;

import playn.core.PlayN;
import playn.core.Json.Object;
import playn.core.json.JsonParserException;
import playn.core.util.Callback;
import tuxkids.tuxblocks.core.utils.PlayNObject;

public class Lang extends PlayNObject {
	
	public final static String EN = "en";
	public final static String FR = "fr";
	
	private final static String TEXT_PATH = "text/";
	private final static String STRINGS_PATH = "Strings.json";
	private final static String DEFAULT_LANGUAGE = EN;
	
	private static String language = DEFAULT_LANGUAGE;
	private static Object dictionary;
	private static Object defaultDictionary;
	
	public static String language() {
		return language;
	}
	
	public static boolean loaded() {
		return dictionary != null;
	}
	
	public static void setLanguage(final String language, final Callback<Void> callback) {
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
	
	private static void loadLangauge(final String language, final Callback<Object> callback) {
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
		return getString(defaultDictionary, domain, key);
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
}
