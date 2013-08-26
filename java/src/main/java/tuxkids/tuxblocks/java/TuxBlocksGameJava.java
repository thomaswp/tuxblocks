package tuxkids.tuxblocks.java;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;

import playn.core.CanvasImage;
import playn.core.PlayN;
import playn.java.JavaPlatform;
import tuxkids.tuxblocks.core.TuxBlocksGame;
import tuxkids.tuxblocks.core.utils.ImageSaver;

public class TuxBlocksGameJava {

	public static void main(String[] args) {
		ImageSaver.saver = new ImageSaver.Saver() {
			@Override
			public void save(CanvasImage image, String path) {
				try {
					Method snapshot = image.getClass().getMethod("snapshot");
					snapshot.setAccessible(true);
					Object jImage = snapshot.invoke(image);
					Field img = jImage.getClass().getSuperclass().getDeclaredField("img");
					img.setAccessible(true);
					BufferedImage bImage = (BufferedImage) img.get(jImage);
					ImageIO.write(bImage, "png", new File(path));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		JavaPlatform.Config config = new JavaPlatform.Config();
		config.width = 1000;
		config.height = 620;
		config.storageFileName = "TuxBlocks.dat";
		// use config to customize the Java platform, if needed
		JavaPlatform platform = JavaPlatform.register(config);
		// TODO: Display.setIcon()
		platform.setTitle("TuxBlocks");
		PlayN.run(new TuxBlocksGame());
	}
}
