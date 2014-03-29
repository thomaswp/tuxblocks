package tripleplay.particle.init;

import static tripleplay.particle.ParticleBuffer.*;
import playn.core.Color;
import tripleplay.particle.Effector;
import tripleplay.util.Colors;
import tripleplay.util.Interpolator;
import tuxkids.tuxblocks.core.utils.Debug;

/**
 * An {@link Effector} which changes the color of a particle over time.
 */
public abstract class ColorEffector extends Effector {

	public abstract int startColor();
	public abstract int endColor();
	protected abstract Interpolator interp(); 
	
	
	protected float perc;
	public float getPerc(float[] data, int start, float now) {
		return interp().apply(0, 1, now - data[start + BIRTH], data[start + LIFESPAN]);
	}

	/**
	 * Returns an effector that updates the particle's color based on its age, as adjusted by the
	 * supplied interpolator.
	 */
	public static Effector byAge (final Interpolator interp,
			final int startColor, final int endColor) {

		return new ColorEffector() {
			@Override public void apply (int index, float[] data, int start, float now, float dt) {
				this.perc = getPerc(data, start, now);
				int color = Colors.blend(startColor, endColor, 1 - perc);
				
				Debug.write("Encoded: %d %d %d %d", Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
				
				data[start + ALPHA_RED] = Color.encode(playn.core.Color.alpha(color) / 255f, playn.core.Color.red(color) / 255f);
				data[start + GREEN_BLUE] = Color.encode(playn.core.Color.green(color) / 255f, playn.core.Color.blue(color) / 255f);
			}

			@Override
			public int startColor() {
				return startColor;
			}

			@Override
			public int endColor() {
				return endColor;
			}

			@Override
			protected Interpolator interp() {
				return interp;
			}
		};
	}
}
