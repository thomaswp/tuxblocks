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
import tuxkids.tuxblocks.core.utils.Sobol;

public class GameBackgroundSprite extends PlayNObject {

	private final static int CREATE_RECT_INTERVAL = 300;
	private static final int REMOVE_RECT_INTERVAL = 1500;
	private final static int MAX_RECTS = 30;
	
	private final static float HUE_OFFSET_1 = 0.3f;
	private final static float HUE_OFFSET_2 = 0.7f;
	
	private GroupLayer groupLayer;
	private List<BackgroundSprite> 
		backgroundSprites = new ArrayList<BackgroundSprite>(),
		toRemove = new ArrayList<BackgroundSprite>();
	private int rectTimer;
	private int primaryColor, secondaryColor, ternaryColor;
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
	
	public float primaryHue() {
		return primaryHue;
	}
	
	public int primaryColor() {
		return primaryColor;
	}
	
	public int secondaryColor() {
		return secondaryColor;
	}
	
	public int ternaryColor() {
		return ternaryColor;
	}
	
	public void scroll(float x, float y) {
		offset.x += x;
		offset.y += y;
	}
	
	public GameBackgroundSprite() {
		groupLayer = graphics().createGroupLayer();
		groupLayer.setDepth(-100);
		newThemeColor();
		update(0);
	}

	private void createBackgroundSprite() {
		BackgroundSprite sprite = new BackgroundSprite(primaryHue, 
				(float)backgroundSprites.size() / 20); 
		backgroundSprites.add(sprite);
		groupLayer.add(sprite.layer);
	}
	
	private void removeBackgroundSprite() {
		if (backgroundSprites.size() == 0) return;
		backgroundSprites.get((int)(Math.random() * backgroundSprites.size())).beginRemove();
	}
	

	public void newThemeColor() {
		primaryHue = (float)Math.random();
		primaryColor = CanvasUtils.hsvToRgb(primaryHue, 1, 1);
		secondaryColor = CanvasUtils.hsvToRgb(primaryHue + HUE_OFFSET_1, 1, 1);
		ternaryColor = CanvasUtils.hsvToRgb(primaryHue + HUE_OFFSET_2, 1, 1);
	}
	
	public void update(int delta) {

		rectTimer += delta;
		if (backgroundSprites.size() < MAX_RECTS) {
			if (rectTimer >= CREATE_RECT_INTERVAL) {
				rectTimer -= CREATE_RECT_INTERVAL;
				createBackgroundSprite();
			}
		} else {
			if (rectTimer >= REMOVE_RECT_INTERVAL) {
				rectTimer -= REMOVE_RECT_INTERVAL;
				removeBackgroundSprite();
			}
		}
	}
	
	public void paint(Clock clock) {
		for (BackgroundSprite bg : backgroundSprites) {
			bg.update((int) clock.dt());
		}
		backgroundSprites.removeAll(toRemove);
		toRemove.clear();
	}
	
	private float maxSize() {
		return height() / 2;
	}
	
	private float minSize() {
		return maxSize() / 6;
	}
	
	private Sobol sobolD5 = new Sobol(5);
	private class BackgroundSprite {
		private ImageLayer layer;
		private float depth;
		private Vector originalPos;
		private float maxAlpha;
		private boolean removing;
		
		public BackgroundSprite(float hue, float r) {
			double[] point = sobolD5.nextPoint();
			int size = (int)(point[0] * (maxSize() - minSize()) + minSize());
			r = (float)point[4];
			float h = hue;
			if (r < 1f / 3) {
				h = h + HUE_OFFSET_1;
			} else if (r < 2f / 3) {
				h = h + HUE_OFFSET_2;
			}

			float s = (float)Math.random() * 0.5f + 0.5f;
			float v = (float)Math.random() * 0.3f + 0.4f;
			CanvasImage image = CanvasUtils.createRect(size, size, 
					CanvasUtils.hsvToRgb(h, s, v), 1, Colors.DARK_GRAY);
			layer = graphics().createImageLayer(image);
			maxAlpha = (float)Math.random() * 0.4f + 0.4f;
			layer.setAlpha(0);
			
			depth = (float)point[3] * 15 + 5;
			layer.setDepth(-depth);
			
			layer.setOrigin(image.width() / 2, image.height() / 2);
			originalPos = new Vector(width() * ((float)point[1] * 1.6f - 0.3f) - offset.x / depth, 
					((float)point[2] * 1.6f - 0.3f) * height() - offset.y / depth);
			layer.setTranslation(originalPos.x, originalPos.y);
		}
		
		public void update(int delta) {
			layer.setTranslation(originalPos.x + offset.x / depth, 
					originalPos.y + offset.y / depth);
			float targetAlpha = removing ? 0 : maxAlpha;
			layer.setAlpha(lerpTime(layer.alpha(), targetAlpha, 0.999f, delta));
			if (removing && layer.alpha() < 0.01f) {
				layer.destroy();
				toRemove.add(this);
			}
		}
		
		public void beginRemove() {
			removing = true;
		}
	}
}
