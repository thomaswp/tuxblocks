package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Path;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Lightning extends ChainProjectile {


	public Lightning(int hits, float damageReduceFactor, float rangeReduceFactor) {
		super(hits, damageReduceFactor, rangeReduceFactor);
	}

	@Override
	protected int duration() {
		return 200;
	}

	@Override
	public Image createImage() {
		CanvasImage image = graphics().createImage(100, 10);
		Path path = image.canvas().createPath();
		int n = 5;
		path.moveTo(0, image.height() / 2);
		for (int i = 1; i <= n; i++) {
			float x = image.width() / n * (i - 0.5f);
			float y = (i % 2) * (image.height() - 1) + 0.5f;
			path.lineTo(x, y);
		}
		path.lineTo(image.width() - 1, image.height() / 2);
		image.canvas().setStrokeColor(Colors.BLUE);
		image.canvas().strokePath(path);
		return image;
	}
	
	int t = 1;
	int timer = 0;
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		layer.transform().setScaleY(layer.transform().scaleY() * t);
		timer += clock.dt();
		if (timer > 50) {
			timer -= 50;
			t *= -1;
		}
	}

	@Override
	protected ChainProjectile copy(int hits, float damageReduceFactor,
			float rangeReduceFactor) {
		return new Lightning(hits, damageReduceFactor, rangeReduceFactor);
	}

}
