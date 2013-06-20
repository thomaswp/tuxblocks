package tuxkids.tuxblocks.core;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.ColorUtils;
import tuxkids.tuxblocks.core.utils.Sobol;

public class GameBackgroundSprite extends PlayNObject {

	private final static int RECT_INTERVAL = 300;
	private final static int MAX_RECTS = 30;
	
	private GroupLayer groupLayer;
	private List<BackgroundSprite> backgroundSprites = 
			new ArrayList<BackgroundSprite>();
	private int rectTimer;
	private int primaryColor;
	private float primaryHue;
	private Vector offset = new Vector();
	
	public float width() {
		return graphics().width();
	}
	
	public float height() {
		return graphics().height();
	}
	
	public GroupLayer layer() {
		return groupLayer;
	}
	
	public int primaryColor() {
		return primaryColor;
	}
	
	public void scroll(float x, float y) {
		offset.x += x;
		offset.y += y;
	}
	
	public GameBackgroundSprite() {
	
		groupLayer = graphics().createGroupLayer();
		createBackground();
		update(0);
	}

	private void createBackgroundSprite() {
		BackgroundSprite sprite = new BackgroundSprite(primaryHue, 
				(float)backgroundSprites.size() / 20); 
		backgroundSprites.add(sprite);
		groupLayer.add(sprite.layer);
	}
	
	private void createBackground() {
		primaryHue = (float)Math.random();
		primaryColor = ColorUtils.hsvToRgb(primaryHue, 1, 1);
		for (int i = 0; i < 0; i++) {
			createBackgroundSprite();
		}
		groupLayer.setDepth(-100);
	}
	
	public void update(int delta) {
		
		if (backgroundSprites.size() < MAX_RECTS) {
			rectTimer += delta;
			if (rectTimer >= RECT_INTERVAL) {
				rectTimer -= RECT_INTERVAL;
				createBackgroundSprite();
			}
		}
	}
	
	public void paint(Clock clock) {
		for (BackgroundSprite bg : backgroundSprites) {
			bg.update((int) clock.dt());
		}
	}
	
	private Sobol sobolD4 = new Sobol(4);
	private class BackgroundSprite {
		private ImageLayer layer;
		private float depth;
		private Vector originalPos;
		private float maxAlpha;
		
		public BackgroundSprite(float hue, float r) {
			double[] point = sobolD4.nextPoint();
			int size = (int)(point[0] * 250 + 50);
			float h = hue;
			if (r < 1f / 3) {
				h = h + 0.7f;
			} else if (r < 2f / 3) {
				h = h + 0.3f;
			}

			float s = (float)Math.random() * 0.5f + 0.5f;
			float v = (float)Math.random() * 0.3f + 0.4f;
			CanvasImage image = CanvasUtils.createRect(size, size, 
					ColorUtils.hsvToRgb(h, s, v), 1, Colors.DARK_GRAY);
			layer = graphics().createImageLayer(image);
			maxAlpha = (float)Math.random() * 0.4f + 0.4f;
			layer.setAlpha(0);
			
			layer.setOrigin(image.width() / 2, image.height() / 2);
			originalPos = new Vector(width() * ((float)point[1] * 1.6f - 0.3f), 
					((float)point[2] * 1.6f - 0.3f) * height());
			layer.setTranslation(originalPos.x, originalPos.y);
			
			depth = (float)point[3] * 15 + 5;
			layer.setDepth(-depth);
		}
		
		public void update(int delta) {
			layer.setTranslation(originalPos.x + offset.x / depth, 
					originalPos.y + offset.y / depth);
			layer.setAlpha(lerp(layer.alpha(), maxAlpha, 1 - (float)Math.pow(0.999, delta)));
		}
	}
}
