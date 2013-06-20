package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Lightning extends ConnectionProjectile {

	@Override
	protected int duration() {
		return 200;
	}

	@Override
	public Image createImage() {
		CanvasImage image = CanvasUtils.createRect(100, 10, Colors.BLUE);
		return image;
	}

}
