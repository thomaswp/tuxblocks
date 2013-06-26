package tuxkids.tuxblocks.core.defense.round;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.defense.walker.Walker;

public abstract class Round {
	private List<Wave> waves = new ArrayList<Wave>();
	private List<Integer> waitTimes = new ArrayList<Integer>();
	
	private int timer;
	private Wave currentWave;
	private int nextDepth;
	
	protected abstract void populateRound();

	public float nextDepth() {
		return nextDepth;
	}
	
	public Round() {
		populateRound();
	}
	
	protected void addWave(Wave wave, int waitTime) {
		waves.add(wave);
		waitTimes.add(waitTime);
	}
	
	public Walker update(int delta) {
		if (isFinished()) return null;
		if (currentWave != null) {
			Walker walker = currentWave.update(delta);
			if (walker != null) nextDepth--;
			if (currentWave.finished()) {
				currentWave = null;
			}
			return walker;
		}
		timer += delta;
		if (currentWave == null && waitTimes.size() > 0 && timer >= waitTimes.get(0)) {
			currentWave = waves.remove(0);
			waitTimes.remove(0);
			nextDepth = Walker.MAX_BASE_DEPTH;
			timer = 0;
		}
		return null;
	}
	
	public boolean isFinished() {
		return waves.size() == 0 && currentWave == null;
	}
}
