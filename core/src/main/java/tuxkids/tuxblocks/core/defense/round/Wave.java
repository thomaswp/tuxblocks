package tuxkids.tuxblocks.core.defense.round;

import tuxkids.tuxblocks.core.defense.walker.Walker;

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
			return walker.copy();
		}
		return null;
	}
	
	public boolean finished() {
		return count == produced;
	}
}
