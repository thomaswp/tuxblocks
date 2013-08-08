package tuxkids.tuxblocks.core;

import java.util.HashMap;

import playn.core.Font;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.Font.Style;
import playn.core.Image;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

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
		return createFormat(Constant.FONT_NAME, Style.PLAIN, size);
	}
	
	public static TextFormat createFormat(String name, Style style, float size) {
		return new TextFormat().withFont(getFont(Constant.FONT_NAME, style, size));
	}
	
	public static abstract class Key extends PlayNObject implements Hashable {
		public abstract Key copy();

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
