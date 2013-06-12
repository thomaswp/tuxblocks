package tuxkids.tuxblocks.core.defense;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.ColorUtils;
import tuxkids.tuxblocks.core.utils.Sobol;

public class NumberSelectScreen extends GameScreen implements Listener {

	private final static int SPACING = 150;
	private final static int MAX_NUMS = 70;
	
	private List<Point> numberPoints = new ArrayList<Point>();
	private List<ImageLayer> numberImages = new ArrayList<ImageLayer>();
	private TextFormat textFormat;
	private Point selectedPoint, possibleSelectedPoint;
	private Vector velocity = new Vector();
	private Vector position = new Vector();
	private Vector dragOffset = new Vector();
	private List<Vector> positionTrail = new ArrayList<Vector>();
	private List<Double> timeTrail = new ArrayList<Double>();
	
	private GroupLayer foreground;
	private GroupLayer background;
	private List<BackgroundSprite> backgroundSprites = 
			new ArrayList<BackgroundSprite>();
	private int backgroundPrimaryColor;
	
	public NumberSelectScreen(ScreenStack screens) {
		super(screens);
	}

	@Override
	public void wasAdded() {
		textFormat = new TextFormat().withFont(
				graphics().createFont("Arial", Style.PLAIN, 50));
		selectedPoint = new Point(1, 0);
		background = graphics().createGroupLayer();
		
		foreground = graphics().createGroupLayer();
		foreground.setOrigin(-width() / 2, -height() / 2);
		layer.add(foreground);
		PlayN.pointer().setListener(this);
		createBackground();
	}
	
	private void createBackground() {
		float hue = (float)Math.random();
		backgroundPrimaryColor = ColorUtils.hsvToRgb(hue, 1, 1);
		for (int i = 0; i < 30; i++) {
			BackgroundSprite sprite = new BackgroundSprite(hue, (float)i / 20); 
			backgroundSprites.add(sprite);
			background.add(sprite.layer);
		}
		//background.setTranslation(-width() / 2, -height() / 2);
		background.setDepth(-100);
		
		CanvasImage bgImage = CanvasUtils.createRect(width(), height(), Colors.WHITE);
		ImageLayer bg = graphics().createImageLayer(bgImage);
		bg.setDepth(-101);
		
		CanvasImage circleImage = CanvasUtils.createCircle(SPACING / 2, 
				Color.argb(0, 0, 0, 0), 20, Colors.WHITE);
		ImageLayer circle = graphics().createImageLayer(circleImage);
		circle.setOrigin(circleImage.width() / 2, circleImage.height() / 2);
		circle.setTranslation(width() / 2, height() / 2);
		circle.setAlpha(0.7f);
		circle.setDepth(-99);
		
		layer.add(circle);
		//layer.add(bg);
		layer.add(background);
	}
	
	@Override
	public void update(int delta) {
		int left = (int)((position.x - width() / 2) / SPACING - 0.5);
		int right = (int)((position.x + width() / 2) / SPACING + 0.5);
		int top = (int)((position.y - height() / 2) / SPACING - 0.5);
		int bot = (int)((position.y + height() / 2) / SPACING + 0.5);
		
		
		Point p = new Point();
		for (int i = left; i <= right; i++) {
			for (int j = top; j <= bot; j++) {
				p.setLocation(i, j);
				ImageLayer layer = getNumberSprite(p);
				float dx = position.x - p.x * SPACING;
				float dy = position.y - p.y * SPACING;
				float distance = (float)Math.sqrt(dx * dx + dy * dy);
				float alpha = 1 - Math.min(distance / SPACING / 5, 1);
				if (p.equals(selectedPoint)) {
					layer.setTint(backgroundPrimaryColor);
				} else {
					layer.setTint(Colors.WHITE);
				}
				layer.setAlpha(alpha);
			}
		}
		
		for (BackgroundSprite bg : backgroundSprites) {
			bg.update(delta);
		}
	}
	
	@Override
	public void paint(Clock clock) {
		if (selectedPoint == null) {
			position.x += velocity.x * clock.dt();
			position.y += velocity.y * clock.dt();
			velocity.x *= Math.pow(0.995, clock.dt());
			velocity.y *= Math.pow(0.995, clock.dt());
		} else {
			lerp(position, selectedPoint.x * SPACING, selectedPoint.y * SPACING, 
					1 - (float)Math.pow(0.99, clock.dt()));
		}
		foreground.setTranslation(-position.x, -position.y);
	}
	
	private ImageLayer getNumberSprite(Point p) {
		
		int index = numberPoints.indexOf(p);
		if (index >= 0) {
			ImageLayer layer = numberImages.remove(index);
			numberImages.add(layer);
			Point point = numberPoints.remove(index);
			numberPoints.add(point);
			return layer;
		}

		p = p.clone();
		
		int border = 10;
		TextLayout layout = PlayN.graphics().layoutText("" + getNumber(p), textFormat);
		CanvasImage image = graphics().createImage(layout.width() + border * 2, 
				layout.height() + border * 2);
		image.canvas().setFillColor(Color.rgb(100, 100, 100));
		//image.canvas().fillRect(0, 0, image.width(), image.height());
		image.canvas().setFillColor(Colors.WHITE);
		image.canvas().fillText(layout, border, border);
		ImageLayer layer = graphics().createImageLayer(image);
		layer.setOrigin(image.width() / 2, image.height() / 2);
		layer.setTranslation(p.x * SPACING, p.y * SPACING);
		layer.addListener(new NumberListener(p));
		foreground.add(layer);
		numberImages.add(layer);
		numberPoints.add(p);
		
		if (numberImages.size() > MAX_NUMS) {
			ImageLayer rem = numberImages.remove(0);
			numberPoints.remove(0);
			foreground.remove(rem);
		}
		
		return layer;
	}
	
	private int getNumber(Point p) {
		return p.x - p.y * 10;
	}

	@Override
	public void onPointerStart(Event event) {
		dragOffset.set(position.x + event.x(), position.y + event.y());
		selectedPoint = null;
		positionTrail.clear();
		timeTrail.clear();
		velocity.set(0, 0);
	}

	@Override
	public void onPointerEnd(Event event) {
		if (positionTrail.size() > 1) {
			Vector last = positionTrail.get(0);
			double lastTime = timeTrail.get(0);
			
			float dx = (position.x - last.x) / (float)(event.time() - lastTime);
			float dy = (position.y - last.y) / (float)(event.time() - lastTime);
			velocity.set(dx, dy);
			if (velocity.length() > 0.5f) {
				possibleSelectedPoint = null;
				selectedPoint = null;
			}
		}
	}

	@Override
	public void onPointerDrag(Event event) {
		position.set(-event.x() + dragOffset.x, -event.y() + dragOffset.y);
		positionTrail.add(position.clone());
		timeTrail.add(event.time());
		if (positionTrail.size() > 100) {
			positionTrail.remove(0);
			timeTrail.remove(0);
		}
	}

	@Override
	public void onPointerCancel(Event event) {
		
	}
	
	private class NumberListener implements Listener {

		private Point point;
		
		public NumberListener(Point point) {
			this.point = point;
		}
		
		@Override
		public void onPointerStart(Event event) {
			event.flags().setPropagationStopped(false);
			possibleSelectedPoint = point;
		}

		@Override
		public void onPointerEnd(Event event) { 
			if (point.equals(possibleSelectedPoint)) {
				selectedPoint = point;
			}
		}

		@Override
		public void onPointerDrag(Event event) { }

		@Override
		public void onPointerCancel(Event event) { }
	}
	
	private static Sobol sobol = new Sobol(3);
	
	private class BackgroundSprite {
		private ImageLayer layer;
		private float depth;
		private Vector originalPos;
		
		public BackgroundSprite(float hue, float r) {
			double[] point = sobol.nextPoint();
			int size = (int)(point[2] * 250 + 50);
			CanvasImage image = graphics().createImage(size, size);
			float h = hue;
			if (r < 1f / 3) {
				h = h + 0.7f;
			} else if (r < 2f / 3) {
				h = h + 0.3f;
			}
//			if (r < 0.5f) h += 0.5f;
			float s = (float)Math.random() * 0.5f + 0.5f;
			float v = (float)Math.random() * 0.3f + 0.7f;
			image.canvas().setFillColor(ColorUtils.hsvToRgb(h, s, v));
			image.canvas().setStrokeColor(Colors.DARK_GRAY);
			image.canvas().fillRect(0, 0, image.width(), image.height());
			image.canvas().strokeRect(0, 0, image.width() - 1, image.height() - 1);
			layer = graphics().createImageLayer(image);
			layer.setAlpha((float)Math.random() * 0.4f + 0.4f);
//			layer.setAlpha(0.7f);
			layer.setOrigin(image.width() / 2, image.height() / 2);
			originalPos = new Vector(width() * ((float)point[0] * 1.6f - 0.3f), 
					((float)point[1] * 1.6f - 0.3f) * height());
			layer.setTranslation(originalPos.x, originalPos.y);
			depth = (float)Math.random() * 15 + 5;
			layer.setDepth(-depth);
		}
		
		public void update(int delta) {
			layer.setTranslation(originalPos.x + foreground.tx() / depth, 
					originalPos.y + foreground.ty() / depth);
		}
	}
}
