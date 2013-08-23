package tuxkids.tuxblocks.core.utils;

import playn.core.PlayN;

/** Used to check for GL capabilities */
public class GLStatus {
	
	/** Returns true of OpenGL is enabled on this platform */
	public static boolean enabled() {
		return PlayN.graphics().ctx() != null;
//		return false;
	}
}
