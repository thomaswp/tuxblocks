package tuxkids.tuxblocks.core;

import java.util.HashMap;

import playn.core.Image;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public class Cache {

	private static HashMap<Key, Image> imageMap = new HashMap<Cache.Key, Image>();
	
	public static Image getImage(Key key) {
		return imageMap.get(key);
	}
	
	public static Image putImage(Key key, Image image) {
		imageMap.put(key.copy(), image);
		return image;
	}
	
	public static void clear() {
		imageMap.clear();
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
}
