package tuxkids.tuxblocks.core.utils;

import playn.core.CanvasImage;

/**
 * Used on the Java platform to save a CanvasImage to a file.
 */
public class ImageSaver {
	public static Saver saver;
	
	/**
	 * Used on the Java platform to save a CanvasImage to a file.
	 */
	public static void save(CanvasImage image, String path) {
		if (saver != null) {
			saver.save(image, path);
		}
	}
	
	public interface Saver {
		void save(CanvasImage image, String path);
	}
}
