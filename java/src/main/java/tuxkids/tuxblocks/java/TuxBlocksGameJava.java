package tuxkids.tuxblocks.java;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.Display;

import playn.core.CanvasImage;
import playn.core.PlayN;
import playn.java.JavaPlatform;
import tuxkids.tuxblocks.core.TuxBlocksGame;
import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.action.callbacks.SolveActionCallback;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
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
		// load the icon.. laboriously
		try {
			URL url16 = TuxBlocksGame.class.getClassLoader().getResource("assets/images/icon-16.png");
			URL url32 = TuxBlocksGame.class.getClassLoader().getResource("assets/images/icon-32.png");
			BufferedImage icon16 = ImageIO.read(url16);
			BufferedImage icon32 = ImageIO.read(url32);

			Display.setIcon(new ByteBuffer[] { createBuffer2(icon16), createBuffer2(icon32) });
		} catch (IOException e) {
			e.printStackTrace();
		}
		platform.setTitle("TuxBlocks");
		
		// currently we don't register fonts on Java because it cannot handle the variants (eg Bold) 
//		JavaGraphics graphics = platform.graphics();
//		graphics.registerFont("Raavi", "fonts/RAAVI.TTF");
//		graphics.registerFont("Mangal", "fonts/MANGAL.TTF");
	
		TuxBlocksGame game = new TuxBlocksGame(Locale.getDefault().getLanguage());
		TuxBlocksGame.loggingCallback = new LogCallback();
		PlayN.run(game);
	}

	// modified from: http://www.jpct.net/forum2/index.php?topic=795.0
	private static ByteBuffer createBuffer2(BufferedImage img) {
		int len=img.getHeight(null)*img.getWidth(null);
		ByteBuffer temp=ByteBuffer.allocateDirect(len<<2);;
		temp.order(ByteOrder.LITTLE_ENDIAN);

		int[] pixels=new int[len];

		PixelGrabber pg = new PixelGrabber(img, 0, 0, 
				img.getWidth(null), img.getHeight(null), 
				pixels, 0, img.getWidth(null));

		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i=0; i<len; i++) {
			int pos=i<<2;
			int texel=pixels[i];
			int a = (texel >> 24) & 0xff;
			int r = (texel >> 16) & 0xff;
			int g = (texel >> 8) & 0xff;
			int b = (texel >> 0) & 0xff;
			int color = a << 24 | b << 16 | g << 8 | r;
			temp.putInt(pos, color);
		}

		return temp;
	}
	
	private static class LogCallback implements SolveActionCallback {

		PrintWriter writer;
		
		public LogCallback() {
			try {
				File f = new File("logging.csv");
				boolean start = !f.exists();
				FileOutputStream fos = new FileOutputStream(f, true);
				writer = new PrintWriter(fos);
				if (start) {
					writeLine("Time", "Before", "Action", "Success", "Tags", "Description");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void writeLine(Object... cols) {
			for (int i = 0; i < cols.length; i++) {
				if (i != 0) writer.print(", ");
				writer.print(sanitize(cols[i].toString()));
			}
			writer.println();
			writer.flush();
		}
		
		private String sanitize(String txt) {
			txt = txt.replace("\"", "\"\"");
			return "\"" + txt + "\"";
		}
		
		@Override
		public void onActionPerformed(SolveAction action, Equation before) {
			writeLine(action.timestamp, before.getPlainText(), action.name(), 
					action.success, action.tags(), action);
		}
		
	}
}
