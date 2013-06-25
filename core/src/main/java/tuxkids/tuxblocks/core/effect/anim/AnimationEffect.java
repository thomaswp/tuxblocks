package tuxkids.tuxblocks.core.effect.anim;

import playn.core.Image;
import playn.core.ImageLayer;
import tuxkids.tuxblocks.core.effect.Effect;

public class AnimationEffect extends Effect {
	
	private Animation animation;
	private ImageLayer imageLayer;
	private int index;
	private int frameLength;
	private int timer;
	
	public AnimationEffect(Animation animation, int frameLength) {
		this.animation = animation;
		imageLayer = graphics().createImageLayer();
		if (animation.images.length > 0) imageLayer.setImage(animation.images[0]);
		centerImageLayer(imageLayer);
		layer.add(imageLayer);
		this.frameLength = frameLength;
	}

	@Override
	public boolean update(int delta) {
		if (index >= animation.images.length) {
			layer.destroy();
			return true;
		}
		timer += delta;
		if (timer > frameLength) {
			imageLayer.setImage(animation.images[index++]);
			timer -= frameLength;
		}
		return false;
	}
}
