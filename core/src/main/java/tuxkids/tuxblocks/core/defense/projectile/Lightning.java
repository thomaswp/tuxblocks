package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.Path;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.Cache.Key;

public class Lightning extends ChainProjectile {

	private static Image image;
	
	public Lightning(int hits, float damageReduceFactor, float rangeReduceFactor) {
		super(hits, damageReduceFactor, rangeReduceFactor);
	}

	@Override
	protected int duration() {
		return 200;
	}
	
	@Override
	public Image createImage() {
		if (image == null) {
			CanvasImage cImage = graphics().createImage(100, 10);
			Path path = cImage.canvas().createPath();
			int n = 5;
			path.moveTo(0, cImage.height() / 2);
			for (int i = 1; i <= n; i++) {
				float x = cImage.width() / n * (i - 0.5f);
				float y = (i % 2) * (cImage.height() - 1) + 0.5f;
				path.lineTo(x, y);
			}
			path.lineTo(cImage.width() - 1, cImage.height() / 2);
			cImage.canvas().setStrokeColor(Colors.BLUE);
			cImage.canvas().strokePath(path);
			image = cImage;
		}
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
