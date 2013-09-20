package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.Path;
import playn.core.util.Clock;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.defense.tower.Zapper;

/**
 * A {@link ConnectionProjectile} fired from {@link Zapper}s.
 */
public class Lightning extends ChainProjectile {

	// only create the image once
	private static Image image;
	private int flip = 1;
	private int flipTimer = 0;
	
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
			// create the zig-zag image
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
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		layer.transform().setScaleY(layer.transform().scaleY() * flip);
		flipTimer += clock.dt();
		if (flipTimer > 50) {
			// flip the lightning every 50ms
			flipTimer -= 50;
			flip *= -1;
		}
	}

	@Override
	protected ChainProjectile copy(int hits, float damageReduceFactor,
			float rangeReduceFactor) {
		return new Lightning(hits, damageReduceFactor, rangeReduceFactor);
	}

}
