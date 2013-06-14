package tuxkids.tuxblocks.core.defense.tower;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.defense.projectile.Pea;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class PeaShooter extends Tower {

	@Override
	public int rows() {
		return 1;
	}

	@Override
	public int cols() {
		return 1;
	}

	@Override
	public int damage() {
		return 1;
	}

	@Override
	public int fireRate() {
		return 500;
	}

	@Override
	public float range() {
		return 5;
	}

	@Override
	protected ImageLayer createLayer() {
		int padding = 5, rad = 3;
		CanvasImage image = PlayN.graphics().createImage(width(), height());
		image.canvas().setFillColor(Colors.RED);
		image.canvas().fillRoundRect(padding, padding, 
				image.width() - padding * 2, image.height() - padding * 2, rad);
		image.canvas().setStrokeColor(Colors.BLACK);
		image.canvas().strokeRoundRect(padding, padding, 
				image.width() - padding * 2, image.height() - padding * 2, rad);
		return graphics().createImageLayer(image);
	}

	@Override
	public Projectile createProjectile() {
		return new Pea();
	}

}
