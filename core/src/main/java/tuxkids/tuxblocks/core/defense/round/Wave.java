package tuxkids.tuxblocks.core.defense.round;

import tuxkids.tuxblocks.core.defense.walker.Walker;

/**
 * Component of a {@link Round} which contains a group of a single type of 
 * {@link Walker}s which spawn one after another without a break. 
 * A Round might have multiple Waves with a different Walker type. 
 */
public class Wave {
	private Walker walker;
	private int interval;
	private int count;
	
	private int timer;
	private int produced;
	
	public Walker walker() { return walker; }
	public int interval() { return interval; }
	public int count() { return count; }
	
	public Wave(Walker walker, int interval, int count) {
		this.walker = walker;
		this.interval = interval;
		this.count = count;
	}
	
	public Walker update(int delta) {
		if (finished()) return null;
		timer += delta;
		if (timer > interval) {
			timer -= interval;
			produced++;
			// don't return the original as it will be put on the Grid
			return walker.copy(); 
		}
		return null;
	}
	
	public boolean finished() {
		return count == produced;
	}
}
