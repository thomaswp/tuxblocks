package tuxkids.tuxblocks.core.utils;

import playn.core.util.Clock;

/**
 * A {@link Clock} implementation which ensures that the sum of all
 * dt's returned over a given time-interval will nearly equal the sum of
 * deltas passed in the update method. This is, for some unknown reason, not
 * the case with {@link Clock.Source}. 
 */
public class SolidClock implements Clock {
	private final int updateRate;
	private int elapsed;
	private float current, paintCurrent, paintDelta, alpha;

	public SolidClock(int updateRate) {
		this.updateRate = updateRate;
	}

	@Override
	public float time() {
		return current;
	}

	@Override
	public float dt() {
		return paintDelta;
	}

	@Override
	public float alpha() {
		return alpha;
	}

	/** Call this from {@link playn.core.Game.Default#update}. */
	public void update(int delta) {
		elapsed += delta;
		current = elapsed;
	}

	/** Call this from {@link playn.core.Game.Default#paint}. */
	public void paint(float alpha) {
		float newCurrent = elapsed + alpha * updateRate;
		paintDelta = newCurrent - paintCurrent;
		paintCurrent = newCurrent;
		this.alpha = alpha;
	}
}
