package tuxkids.tuxblocks.core.utils;

import playn.core.CanvasImage;

public class ImageSaver {
	public static Saver saver;
	
	public static void save(CanvasImage image, String path) {
		if (saver != null) {
			saver.save(image, path);
		}
	}
	
	public interface Saver {
		void save(CanvasImage image, String path);
	}
}
