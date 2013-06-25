package tuxkids.tuxblocks.core.effect.anim;

import playn.core.Image;
import tuxkids.tuxblocks.core.PlayNObject;

public class Animation extends PlayNObject {

	protected Image[] images;
	
	public Animation(String folder, int start, int end, String ext) {
		images = new Image[end - start + 1];
		for (int i = start; i <= end; i++) {
			images[i - start] = assets().getImage("anim/" + folder + "/" + i + ".png");
		}
	}
}
