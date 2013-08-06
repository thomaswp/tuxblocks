package tuxkids.tuxblocks.core;

import playn.core.PlayN;

public class GLStatus {
	public static boolean enabled() {
		return PlayN.graphics().ctx() != null;
//		return false;
	}
}
