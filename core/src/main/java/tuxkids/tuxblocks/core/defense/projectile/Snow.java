package tuxkids.tuxblocks.core.defense.projectile;

import playn.core.Color;
import playn.core.Image;
import playn.core.util.Callback;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Snow extends BodyProjectile {

	private static Image image;
	
	@Override
	public void place(Grid grid, Walker target, Tower source) {
		super.place(grid, target, source);
		layer.setScale(0.7f);
		layer.setAlpha(0.5f);
	}
	
	@Override
	public float maxSpeed() {
		return 0.08f;
	}

	@Override
	public float acceleration() {
		return 0.01f;
	}

	@Override
	public Image createImage() {
		if (image == null) {
			image = assets().getImage(Constant.IMAGE_PATH + "puff.png");
			image.addCallback(new Callback<Image>() {
				@Override
				public void onSuccess(Image result) {
					image = CanvasUtils.tintImage(
							image,
							Color.rgb(150, 150, 255));
				}

				@Override
				public void onFailure(Throwable cause) {
				}
				
			});
		}
		return image;
	}

}
