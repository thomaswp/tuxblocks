package tuxkids.tuxblocks.core;

import java.util.HashMap;

import playn.core.Font;
import playn.core.Font.Style;
import playn.core.Image;
import playn.core.PlayN;
import playn.core.TextFormat;
import tuxkids.tuxblocks.core.lang.Lang;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;
import tuxkids.tuxblocks.core.utils.PlayNObject;

/**
 * Class for caching various resources for reuse.
 */
public class Cache {

	private final static HashMap<Key, Image> imageMap = new HashMap<Cache.Key, Image>();
	private final static HashMap<Key, Font> fontMap = new HashMap<Cache.Key, Font>();
	
	private final static FontKey fontKey = new FontKey(); 
	
	public static Image getImage(Key key) {
		return imageMap.get(key);
	}
	
	public static Image putImage(Key key, Image image) {
		imageMap.put(key.copy(), image);
		return image;
	}
	
	public static void clear() {
		imageMap.clear();
		fontMap.clear();
	}
	
	public static Font getFont(String name, Style style, float size) {
		Font font = fontMap.get(fontKey.set(name, style, size));
		if (font == null) {
			font = PlayN.graphics().createFont(name, style, size);
			fontMap.put(fontKey.copy(), font);
		}
		return font;
	}
	
	public static TextFormat createFormat(float size) {
		return createFormat(Lang.font(), Style.PLAIN, size);
	}
	
	public static TextFormat createNumberFormat(float size) {
		return createFormat(Constant.NUMBER_FONT, Style.PLAIN, size);
	}
	
	public static TextFormat createFormat(String name, Style style, float size) {
		return new TextFormat().withFont(getFont(name, style, size));
	}
	
	/**
	 * Identifies a resource for reuse later. Should be able to copy iteself
	 * to a new instance if necessary.
	 */
	public static abstract class Key extends PlayNObject implements Hashable {
		public abstract Key copy();

		/**
		 * Creates a Key based on the supplied class and any Object.
		 * Note: Keys are compared using equals(), and so will
		 * the supplied Object.
		 */
		public static Key fromClass(Class<?> clazz, Object key) {
			return new ClassKey(clazz, key);
		}
	}
	
	private static class ClassKey extends Key {

		private Class<?> clazz;
		private Object key;
		
		public ClassKey(Class<?> clazz, Object key) {
			this.clazz = clazz;
			this.key = key;
		}
		
		@Override
		public void addFields(HashCode hashCode) {
			hashCode.addField(clazz);
			hashCode.addField(key);
		}

		@Override
		public Key copy() {
			return new ClassKey(clazz, key);
		}
		
	}
	
	private static class FontKey extends Key {

		private String name;
		private Style style;
		private float size;
		
		public FontKey set(String name, Style style, float size) {
			this.name = name;
			this.style = style;
			this.size = size;
			return this;
		}
		
		@Override
		public void addFields(HashCode hashCode) {
			hashCode.addField(name);
			hashCode.addField(style);
			hashCode.addField(size);
		}

		@Override
		public Key copy() {
			return new FontKey().set(name, style, size);
		}
		
	}
}
